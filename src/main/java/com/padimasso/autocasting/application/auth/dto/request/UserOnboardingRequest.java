package com.padimasso.autocasting.application.auth.dto.request;

import com.padimasso.autocasting.application.auth.model.OnboardingStatus;
import com.padimasso.autocasting.application.auth.model.UserMode;
import jakarta.validation.constraints.NotNull;

public record UserOnboardingRequest(
    @NotNull UserMode activeMode,
    @NotNull OnboardingStatus talentOnboardingStatus,
    @NotNull OnboardingStatus employerOnboardingStatus
    ) {
}
