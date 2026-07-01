package com.padimasso.autocasting.application.admin.dto.response;

import com.padimasso.autocasting.application.auth.model.UserAccountProvider;
import com.padimasso.autocasting.application.auth.model.UserMode;
import com.padimasso.autocasting.application.auth.model.OnboardingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AdminUserDetailResponse(
    UUID id,
    String email,
    UserAccountProvider userAccountProvider,
    List<String> roles,
    UserMode activeMode,
    OnboardingStatus talentOnboardingStatus,
    OnboardingStatus employerOnboardingStatus,
    boolean suspended,
    boolean deleted,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime modifiedAt,
    String modifiedBy
) {
}
