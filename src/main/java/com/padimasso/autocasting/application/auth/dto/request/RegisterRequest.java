package com.padimasso.autocasting.application.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(@NotBlank(message = "auth.required_field")
                              @Pattern(regexp = "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$",
                                  message = "auth.email_invalid")
                              @Size(max = 255, message = "auth.email_max_length")
                              String email,
                              @NotBlank(message = "auth.required_field")
                              @Size(min = 6, max = 255, message = "auth.password_length")
                              String password) {
}

