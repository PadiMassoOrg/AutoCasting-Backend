package com.padimasso.autocasting.application.auth.dto.response;

import com.padimasso.autocasting.application.auth.model.OnboardingStatus;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.model.UserMode;

import java.util.UUID;

public record MeResponse(
    UUID id,
    String email,
    UserMode activeMode,
    OnboardingStatus talentOnboardingStatus,
    OnboardingStatus employerOnboardingStatus
) {
    public static MeResponse from(UserEntity user) {
        return new MeResponse(
            user.getId(),
            user.getEmail(),
            user.getActiveMode(),
            user.getTalentOnboardingStatus(),
            user.getEmployerOnboardingStatus()
        );
    }
}
