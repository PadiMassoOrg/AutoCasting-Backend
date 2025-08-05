package com.padimasso.autocasting.application.profile.dto.request;

import jakarta.validation.constraints.NotBlank;

@SuppressWarnings("unused")
public record PublicProfileRequest(@NotBlank(message = "auth.required_field") String slug) {
}
