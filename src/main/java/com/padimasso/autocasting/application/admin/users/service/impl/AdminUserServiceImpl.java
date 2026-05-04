package com.padimasso.autocasting.application.admin.users.service.impl;

import com.padimasso.autocasting.application.admin.notes.dto.request.CreateAdminNoteCommand;
import com.padimasso.autocasting.application.admin.notes.model.AdminNoteEntityType;
import com.padimasso.autocasting.application.admin.notes.model.AdminNoteType;
import com.padimasso.autocasting.application.admin.notes.service.AdminNoteService;
import com.padimasso.autocasting.application.admin.users.dto.request.AdminUserAccountActionRequest;
import com.padimasso.autocasting.application.admin.users.dto.response.AdminUserRowResponse;
import com.padimasso.autocasting.application.admin.users.dto.response.AdminUsersPageResponse;
import com.padimasso.autocasting.application.admin.users.service.AdminUserService;
import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.employer.dto.response.EmployerProfileResponse;
import com.padimasso.autocasting.application.employer.mapper.EmployerProfileMapper;
import com.padimasso.autocasting.application.employer.repository.EmployerProfileRepository;
import com.padimasso.autocasting.application.talent.dto.response.PublicProfileResponse;
import com.padimasso.autocasting.application.talent.mapper.TalentProfileMapper;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.MAX_PAGE_SIZE;
import static com.padimasso.autocasting.exception.ApiException.badRequest;
import static com.padimasso.autocasting.exception.ApiException.notFound;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.ADMIN_USER_ALREADY_BLOCKED;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.ADMIN_USER_ALREADY_RESTORED;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.AUTH_USER_NOT_FOUND;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.PROFILE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final AdminNoteService adminNoteService;
    private final AuthContext authContext;
    private final TalentProfileRepository talentProfileRepository;
    private final TalentProfileMapper talentProfileMapper;
    private final EmployerProfileRepository employerProfileRepository;
    private final EmployerProfileMapper employerProfileMapper;

    @Override
    public AdminUsersPageResponse listUsers(int page, int size) {
        int normalizedSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int normalizedPage = Math.max(page, 0);

        var pageable = PageRequest.of(
            normalizedPage,
            normalizedSize,
            Sort.by(Sort.Direction.DESC, "modifiedAt", "id")
        );

        var result = userRepository.findAllIncludingDeleted(pageable);
        var items = result.getContent().stream()
            .map(AdminUserRowResponse::from)
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
    public AdminUserRowResponse blockUser(UUID userId, AdminUserAccountActionRequest request) {
        var user = userRepository.findById(userId)
            .orElseThrow(() -> notFound(AUTH_USER_NOT_FOUND));

        if (user.isBlocked()) {
            throw badRequest(ADMIN_USER_ALREADY_BLOCKED);
        }

        user.setBlocked(true);
        user.setBlockedAt(LocalDateTime.now());
        user.setBlockedBy(authContext.getCurrentUserOrThrow().getEmail());

        var savedUser = userRepository.save(user);

        adminNoteService.createNote(new CreateAdminNoteCommand(
            AdminNoteEntityType.USER,
            savedUser.getId(),
            savedUser.getId(),
            AdminNoteType.WARNING,
            null,
            "Account blocked",
            request.note().trim(),
            Map.of("action", "BLOCK_ACCOUNT"),
            true
        ));

        return AdminUserRowResponse.from(savedUser);
    }

    @Override
    @Transactional
    public AdminUserRowResponse restoreUser(UUID userId, AdminUserAccountActionRequest request) {
        var user = userRepository.findById(userId)
            .orElseThrow(() -> notFound(AUTH_USER_NOT_FOUND));

        if (!user.isBlocked()) {
            throw badRequest(ADMIN_USER_ALREADY_RESTORED);
        }

        user.setBlocked(false);
        user.setBlockedAt(null);
        user.setBlockedBy(null);

        var savedUser = userRepository.save(user);

        adminNoteService.createNote(new CreateAdminNoteCommand(
            AdminNoteEntityType.USER,
            savedUser.getId(),
            savedUser.getId(),
            AdminNoteType.MANUAL_CHANGE,
            null,
            "Account restored",
            request.note().trim(),
            Map.of("action", "RESTORE_ACCOUNT"),
            false
        ));

        return AdminUserRowResponse.from(savedUser);
    }

    @Override
    public PublicProfileResponse getTalentProfileForAdmin(UUID userId) {
        var profile = talentProfileRepository.findTalentProfileForAdminByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException(PROFILE_NOT_FOUND));

        return talentProfileMapper.toPublicProfileResponse(profile);
    }

    @Override
    public EmployerProfileResponse getEmployerProfileForAdmin(UUID userId) {
        var profile = employerProfileRepository.findEmployerProfileForAdminByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException(PROFILE_NOT_FOUND));

        return employerProfileMapper.toProfileResponse(profile, profile.getUser());
    }
}
