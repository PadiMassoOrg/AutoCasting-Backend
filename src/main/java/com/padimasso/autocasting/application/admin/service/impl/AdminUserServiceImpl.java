package com.padimasso.autocasting.application.admin.service.impl;

import com.padimasso.autocasting.application.admin.dto.request.AdminBulkTalentWelcomeEmailRequest;
import com.padimasso.autocasting.application.admin.dto.request.AdminUserSuspensionRequest;
import com.padimasso.autocasting.application.admin.dto.request.AdminUserUpdateRequest;
import com.padimasso.autocasting.application.admin.dto.response.AdminBulkTalentWelcomeEmailResultResponse;
import com.padimasso.autocasting.application.admin.dto.response.AdminUserDetailResponse;
import com.padimasso.autocasting.application.admin.dto.response.AdminUserRowResponse;
import com.padimasso.autocasting.application.admin.mapper.AdminUserMapper;
import com.padimasso.autocasting.application.admin.repository.specification.AdminUserSpecs;
import com.padimasso.autocasting.application.admin.service.AdminUserService;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.common.dto.PageResponse;
import com.padimasso.autocasting.application.common.model.EntityType;
import com.padimasso.autocasting.application.employer.dto.response.EmployerProfileResponse;
import com.padimasso.autocasting.application.employer.mapper.EmployerProfileMapper;
import com.padimasso.autocasting.application.employer.repository.EmployerProfileRepository;
import com.padimasso.autocasting.application.history.dto.HistoryChangeEntry;
import com.padimasso.autocasting.application.history.service.HistoryService;
import com.padimasso.autocasting.application.talent.dto.response.PublicProfileResponse;
import com.padimasso.autocasting.application.talent.mapper.TalentProfileMapper;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import com.padimasso.autocasting.application.talent.service.TalentWelcomeEmailService;
import com.padimasso.autocasting.application.talent.util.TalentCatalogVisibility;
import com.padimasso.autocasting.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.padimasso.autocasting.config.AppConstants.MAX_PAGE_SIZE;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.ADMIN_USER_UPDATE_NO_CHANGES;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.GENERAL_IDS_REQUIRED;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.GENERAL_UNEXPECTED;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.PROFILE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final TalentProfileRepository talentProfileRepository;
    private final TalentProfileMapper talentProfileMapper;
    private final EmployerProfileRepository employerProfileRepository;
    private final EmployerProfileMapper employerProfileMapper;
    private final AdminUserMapper adminUserMapper;
    private final HistoryService historyService;
    private final TalentWelcomeEmailService talentWelcomeEmailService;

    @Override
    public PageResponse<AdminUserRowResponse> listUsers(int page, int size, String q, boolean notVisibleInCatalog) {
        int normalizedSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int normalizedPage = Math.max(page, 0);

        var pageable = PageRequest.of(
            normalizedPage,
            normalizedSize,
            Sort.by(Sort.Direction.DESC, "createdAt", "id")
        );

        var spec = AdminUserSpecs.fromSearchText(q);
        if (notVisibleInCatalog) {
            var notVisibleSpec = AdminUserSpecs.notVisibleInTalentCatalog();
            spec = spec == null ? notVisibleSpec : spec.and(notVisibleSpec);
        }

        var result = userRepository.findAllIncludingDeleted(spec, pageable);

        List<UserEntity> users = result.getContent();
        List<UUID> userIds = users.stream().map(UserEntity::getId).toList();

        Map<UUID, String> employerCompanyNames = new HashMap<>();
        if (!userIds.isEmpty()) {
            employerProfileRepository.findAllByUserIdInForAdmin(userIds).forEach(profile -> {
                var basicInfo = profile.getBasicInfo();
                if (basicInfo != null && basicInfo.getCompanyName() != null) {
                    employerCompanyNames.put(profile.getUser().getId(), basicInfo.getCompanyName());
                }
            });
        }

        Map<UUID, String> talentStageNames = new HashMap<>();
        // A talent profile always has a slug (generated on creation) regardless of catalog
        // visibility. Only entered into this map — and thus only returned to the admin row —
        // when isVisibleInTalentCatalog is true, so the response's null/non-null here reflects
        // catalog visibility, not slug existence.
        Map<UUID, String> talentPublicSlugs = new HashMap<>();
        if (!userIds.isEmpty()) {
            talentProfileRepository.findAllByUserIdInForAdmin(userIds).forEach(profile -> {
                var userId = profile.getUser().getId();
                var basicInfo = profile.getBasicInfo();
                if (basicInfo != null && basicInfo.getStageName() != null) {
                    talentStageNames.put(userId, basicInfo.getStageName());
                }
                if (TalentCatalogVisibility.isVisibleInCatalog(profile)) {
                    talentPublicSlugs.put(userId, profile.getPublicSlug());
                }
            });
        }

        var items = users.stream()
            .map(user -> adminUserMapper.toRowResponse(
                user,
                employerCompanyNames.get(user.getId()),
                talentStageNames.get(user.getId()),
                talentPublicSlugs.get(user.getId())
            ))
            .toList();

        return adminUserMapper.toPageResponse(items, result);
    }

    @Override
    public AdminUserDetailResponse getUserDetail(UUID userId) {
        var user = userRepository.findByIdIncludingDeleted(userId)
            .orElseThrow(() -> ApiException.notFound(PROFILE_NOT_FOUND));

        return adminUserMapper.toDetailResponse(user);
    }

    @Override
    @Transactional
    public AdminUserDetailResponse updateUserDetail(UUID userId, AdminUserUpdateRequest request) {
        var user = userRepository.findByIdIncludingDeleted(userId)
            .orElseThrow(() -> ApiException.notFound(PROFILE_NOT_FOUND));

        var nextEmail = request.email().trim();
        var nextActiveMode = request.activeMode();
        var nextSuspended = request.suspended();
        var nextDeleted = request.deleted();
        var changes = new ArrayList<HistoryChangeEntry>();

        if (!Objects.equals(user.getEmail(), nextEmail)) {
            changes.add(new HistoryChangeEntry("email", user.getEmail(), nextEmail));
            user.setEmail(nextEmail);
        }

        if (!Objects.equals(user.getActiveMode(), nextActiveMode)) {
            changes.add(new HistoryChangeEntry("activeMode", user.getActiveMode(), nextActiveMode));
            user.setActiveMode(nextActiveMode);
        }

        if (user.isSuspended() != nextSuspended) {
            changes.add(new HistoryChangeEntry("suspended", user.isSuspended(), nextSuspended));
            user.setSuspended(nextSuspended);
        }

        if (user.isDeleted() != nextDeleted) {
            changes.add(new HistoryChangeEntry("deleted", user.isDeleted(), nextDeleted));
            user.setDeleted(nextDeleted);
        }

        if (changes.isEmpty()) {
            throw ApiException.badRequest(ADMIN_USER_UPDATE_NO_CHANGES);
        }

        userRepository.save(user);
        historyService.createHistoryEntry(EntityType.USER, userId, request.note(), changes);

        return adminUserMapper.toDetailResponse(user);
    }

    @Override
    @Transactional
    public void updateSuspension(UUID userId, AdminUserSuspensionRequest request) {
        var user = userRepository.findByIdIncludingDeleted(userId)
            .orElseThrow(() -> ApiException.notFound(PROFILE_NOT_FOUND));

        var previousSuspended = user.isSuspended();

        user.setSuspended(request.suspended());
        userRepository.save(user);
        historyService.createHistoryEntry(
            EntityType.USER,
            userId,
            request.reason(),
            List.of(new HistoryChangeEntry("suspended", previousSuspended, request.suspended()))
        );
    }

    @Override
    public PublicProfileResponse getTalentProfileForAdmin(UUID userId) {
        var profile = talentProfileRepository.findTalentProfileForAdminByUserId(userId)
            .orElseThrow(() -> ApiException.notFound(PROFILE_NOT_FOUND));

        return talentProfileMapper.toPublicProfileResponse(profile);
    }

    @Override
    public EmployerProfileResponse getEmployerProfileForAdmin(UUID userId) {
        var profile = employerProfileRepository.findEmployerProfileForAdminByUserId(userId)
            .orElseThrow(() -> ApiException.notFound(PROFILE_NOT_FOUND));

        return employerProfileMapper.toProfileResponse(profile, profile.getUser());
    }

    @Override
    public AdminBulkTalentWelcomeEmailResultResponse sendBulkTalentWelcomeEmail(AdminBulkTalentWelcomeEmailRequest request) {
        List<UUID> uniqueUserIds = request.userIds().stream()
            .filter(Objects::nonNull)
            .distinct()
            .toList();

        if (uniqueUserIds.isEmpty()) {
            throw ApiException.badRequest(GENERAL_IDS_REQUIRED);
        }

        List<TalentProfileEntity> profiles = talentProfileRepository.findAllByUserIdInForAdmin(uniqueUserIds);
        Map<UUID, TalentProfileEntity> profilesByUserId = new HashMap<>();
        profiles.forEach(profile -> profilesByUserId.put(profile.getUser().getId(), profile));

        int sentCount = 0;
        List<AdminBulkTalentWelcomeEmailResultResponse.Failure> failures = new ArrayList<>();

        for (UUID userId : uniqueUserIds) {
            TalentProfileEntity profile = profilesByUserId.get(userId);
            if (profile == null) {
                failures.add(new AdminBulkTalentWelcomeEmailResultResponse.Failure(userId, PROFILE_NOT_FOUND));
                continue;
            }

            boolean sent = talentWelcomeEmailService.sendWelcomeEmail(profile);
            if (sent) {
                sentCount++;
            } else {
                failures.add(new AdminBulkTalentWelcomeEmailResultResponse.Failure(userId, GENERAL_UNEXPECTED));
            }
        }

        return new AdminBulkTalentWelcomeEmailResultResponse(sentCount, failures.size(), failures);
    }
}
