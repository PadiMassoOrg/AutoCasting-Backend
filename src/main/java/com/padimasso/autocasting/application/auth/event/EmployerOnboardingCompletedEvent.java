package com.padimasso.autocasting.application.auth.event;

import com.padimasso.autocasting.application.auth.model.UserEntity;

public record EmployerOnboardingCompletedEvent(
    UserEntity user
) {
}
