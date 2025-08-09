package com.padimasso.autocasting.application.profile.dto.response;

import java.util.UUID;

public record PublicProfileResponse(
    UUID id,
    String email,
    String roleStringCode,
    String planStringCode,
    String publicSlug,
    BasicInfoResponse basicInfo
) {
}
