package com.padimasso.autocasting.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PublicProfileRequest(@NotBlank(message = "auth.required_field") String slug) {
}
