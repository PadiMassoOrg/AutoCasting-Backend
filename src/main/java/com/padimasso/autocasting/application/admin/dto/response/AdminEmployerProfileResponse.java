package com.padimasso.autocasting.application.admin.dto.response;

import com.padimasso.autocasting.application.auth.model.OnboardingStatus;
import com.padimasso.autocasting.application.employer.dto.response.EmployerBasicInfoResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminEmployerProfileResponse(
    UUID id,
    String publicSlug,
    OnboardingStatus onboardingStatus,
    EmployerBasicInfoResponse basicInfo,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime modifiedAt,
    String modifiedBy
) {
}
