package com.padimasso.autocasting.dto;

public record ProfileResponse(
    String email,
    String roleStringCode,
    String planStringCode,
    String name,
    String publicSlug
) {
}
