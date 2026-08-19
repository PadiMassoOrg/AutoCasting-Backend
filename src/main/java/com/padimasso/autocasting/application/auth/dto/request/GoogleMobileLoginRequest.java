package com.padimasso.autocasting.application.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GoogleMobileLoginRequest(@NotBlank(message = "auth.required_field")
                                       String idToken) {
}
