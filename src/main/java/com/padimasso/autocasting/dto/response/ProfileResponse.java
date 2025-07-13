package com.padimasso.autocasting.dto.response;

public record ProfileResponse(
    String email,
    String roleStringCode,
    String planStringCode,
    String name,
    String publicSlug
) {
}
