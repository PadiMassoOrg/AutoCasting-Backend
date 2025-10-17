package com.padimasso.autocasting.application.talent.dto.request;

import jakarta.validation.constraints.NotBlank;

@SuppressWarnings("unused")
public record PublicProfileRequest(@NotBlank(message = "auth.required_field") String slug) {
}
