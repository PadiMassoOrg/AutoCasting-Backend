package com.padimasso.autocasting.application.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(@NotBlank(message = "auth.required_field")
                           @Pattern(regexp = "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$",
                               message = "auth.email_invalid") String email,
                           @NotBlank(message = "auth.required_field")
                           @Size(min = 6, message = "auth.password_length") String password) {
}
