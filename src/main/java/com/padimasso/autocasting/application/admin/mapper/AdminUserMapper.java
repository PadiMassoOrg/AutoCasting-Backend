package com.padimasso.autocasting.application.admin.mapper;

import com.padimasso.autocasting.application.admin.dto.response.AdminUserDetailResponse;
import com.padimasso.autocasting.application.admin.dto.response.AdminUserRowResponse;
import com.padimasso.autocasting.application.admin.dto.response.AdminUsersPageResponse;
import com.padimasso.autocasting.application.auth.model.RoleEntity;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.Page;

import java.util.Comparator;
import java.util.List;

@Component
public class AdminUserMapper {

    public AdminUserRowResponse toRowResponse(
        UserEntity user,
        String employerCompanyName,
        String talentStageName
    ) {
        return new AdminUserRowResponse(
            user.getId(),
            user.getEmail(),
            employerCompanyName,
            talentStageName,
            toRoleCodes(user),
            user.getUserAccountProvider(),
            user.getActiveMode(),
            user.getTalentOnboardingStatus(),
            user.getEmployerOnboardingStatus(),
            user.isSuspended(),
            user.isDeleted(),
            user.getCreatedAt(),
            user.getCreatedBy(),
            user.getModifiedAt(),
            user.getModifiedBy()
        );
    }

    public AdminUserDetailResponse toDetailResponse(UserEntity user) {
        return new AdminUserDetailResponse(
            user.getId(),
            user.getEmail(),
            user.getUserAccountProvider(),
            toRoleCodes(user),
            user.getActiveMode(),
            user.getTalentOnboardingStatus(),
            user.getEmployerOnboardingStatus(),
            user.isSuspended(),
            user.isDeleted(),
            user.getCreatedAt(),
            user.getCreatedBy(),
            user.getModifiedAt(),
            user.getModifiedBy()
        );
    }

    public AdminUsersPageResponse toPageResponse(
        List<AdminUserRowResponse> items,
        Page<?> result
    ) {
        return new AdminUsersPageResponse(
            items,
            result.getNumber(),
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages(),
            result.hasNext()
        );
    }

    private List<String> toRoleCodes(UserEntity user) {
        return user.getRoles().stream()
            .map(RoleEntity::getCode)
            .sorted(Comparator.naturalOrder())
            .toList();
    }
}
