package com.padimasso.autocasting.application.profile.dto.response;

public record ProfileResponse(
    String email,
    String roleStringCode,
    String planStringCode,
    String name,
    String publicSlug
) {
}
