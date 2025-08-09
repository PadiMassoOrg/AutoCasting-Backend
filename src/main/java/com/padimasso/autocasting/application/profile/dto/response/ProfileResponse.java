package com.padimasso.autocasting.application.profile.dto.response;

import java.util.UUID;

public record ProfileResponse(
    UUID id,
    String roleStringCode,
    String planStringCode,
    String publicSlug,
    BasicInfoResponse basicInfo,
    ContactResponse contact
) {
}
