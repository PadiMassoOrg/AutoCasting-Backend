package com.padimasso.autocasting.application.admin.users.dto.response;

import com.padimasso.autocasting.application.auth.model.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public record AdminUserRowResponse(
    UUID id,
    String email,
    List<String> roles,
    UserAccountProvider userAccountProvider,
    UserMode activeMode,
    OnboardingStatus talentOnboardingStatus,
    OnboardingStatus employerOnboardingStatus,
    boolean deleted,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime modifiedAt,
    String modifiedBy
) {
    public static AdminUserRowResponse from(UserEntity user) {
        List<String> roleCodes = user.getRoles().stream()
            .map(RoleEntity::getCode)
            .sorted(Comparator.naturalOrder())
            .toList();

        return new AdminUserRowResponse(
            user.getId(),
            user.getEmail(),
            roleCodes,
            user.getUserAccountProvider(),
            user.getActiveMode(),
            user.getTalentOnboardingStatus(),
            user.getEmployerOnboardingStatus(),
            user.isDeleted(),
            user.getCreatedAt(),
            user.getCreatedBy(),
            user.getModifiedAt(),
            user.getModifiedBy()
        );
    }
}
