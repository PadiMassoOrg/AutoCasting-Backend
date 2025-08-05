package com.padimasso.autocasting.application.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(@NotBlank(message = "auth.required_field")
                              String name,
                              @NotBlank(message = "auth.required_field")
                              @Pattern(regexp = "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$", message = "auth.email_invalid")
                              String email,
                              @NotBlank(message = "auth.required_field")
                              @Size(min = 8, message = "auth.password_length")
                              String password,
                              @NotNull(message = "auth.required_field")
                              String role) {
}

