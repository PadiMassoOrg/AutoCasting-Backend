package com.padimasso.autocasting.application.admin.service.impl;

import com.padimasso.autocasting.application.admin.dto.response.AdminUserRowResponse;
import com.padimasso.autocasting.application.admin.dto.response.AdminUsersPageResponse;
import com.padimasso.autocasting.application.admin.dto.request.AdminUserSuspensionRequest;
import com.padimasso.autocasting.application.admin.repository.specification.AdminUserSpecs;
import com.padimasso.autocasting.application.admin.service.AdminUserService;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.common.model.EntityType;
import com.padimasso.autocasting.application.employer.dto.response.EmployerProfileResponse;
import com.padimasso.autocasting.application.employer.mapper.EmployerProfileMapper;
import com.padimasso.autocasting.application.employer.repository.EmployerProfileRepository;
import com.padimasso.autocasting.application.notes.service.NoteService;
import com.padimasso.autocasting.application.talent.dto.response.PublicProfileResponse;
import com.padimasso.autocasting.application.talent.mapper.TalentProfileMapper;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import com.padimasso.autocasting.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.MAX_PAGE_SIZE;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.PROFILE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final TalentProfileRepository talentProfileRepository;
    private final TalentProfileMapper talentProfileMapper;
    private final EmployerProfileRepository employerProfileRepository;
    private final EmployerProfileMapper employerProfileMapper;
    private final NoteService noteService;

    @Override
    public AdminUsersPageResponse listUsers(int page, int size, String q) {
        int normalizedSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int normalizedPage = Math.max(page, 0);

        var pageable = PageRequest.of(
            normalizedPage,
            normalizedSize,
            Sort.by(Sort.Direction.DESC, "modifiedAt", "id")
        );

        var result = userRepository.findAllIncludingDeleted(AdminUserSpecs.fromSearchText(q), pageable);

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
        if (!userIds.isEmpty()) {
            talentProfileRepository.findAllByUserIdInForAdmin(userIds).forEach(profile -> {
                var basicInfo = profile.getBasicInfo();
                if (basicInfo != null && basicInfo.getStageName() != null) {
                    talentStageNames.put(profile.getUser().getId(), basicInfo.getStageName());
                }
            });
        }

        var items = users.stream()
            .map(user -> AdminUserRowResponse.from(
                user,
                employerCompanyNames.get(user.getId()),
                talentStageNames.get(user.getId())
            ))
            .toList();

        return new AdminUsersPageResponse(
            items,
            result.getNumber(),
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages(),
            result.hasNext()
        );
    }

    @Override
    @Transactional
    public void updateSuspension(UUID userId, AdminUserSuspensionRequest request) {
        var user = userRepository.findByIdIncludingDeleted(userId)
            .orElseThrow(() -> ApiException.notFound(PROFILE_NOT_FOUND));

        user.setSuspended(request.suspended());
        userRepository.save(user);
        noteService.createNote(EntityType.USER, userId, request.reason());
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
}
