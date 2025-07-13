package com.padimasso.autocasting.dto.response;

public record PublicProfileResponse(
    String email,
    String roleStringCode,
    String planStringCode,
    String name,
    String publicSlug
) {
}
