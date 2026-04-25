package com.padimasso.autocasting.application.admin.service.impl;

import com.padimasso.autocasting.application.admin.dto.response.AdminUserRowResponse;
import com.padimasso.autocasting.application.admin.dto.response.AdminUsersPageResponse;
import com.padimasso.autocasting.application.admin.service.AdminUserService;
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
