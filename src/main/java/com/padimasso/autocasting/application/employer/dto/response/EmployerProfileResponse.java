package com.padimasso.autocasting.application.employer.dto.response;

import java.util.UUID;

public record EmployerProfileResponse(
    UUID id,
    String email,
    String userAccountProvider,
    String publicSlug,
    String planStringCode,
    EmployerBasicInfoResponse basicInfo
) {
}
