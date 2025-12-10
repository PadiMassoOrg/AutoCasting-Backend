package com.padimasso.autocasting.application.auth.dto.response;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;

public record EmployerPrincipal(
    UserEntity user,
    EmployerProfileEntity employerProfile
) {
}
