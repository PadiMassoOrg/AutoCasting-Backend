package com.padimasso.autocasting.application.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ForgotPasswordRequest(
    @NotBlank(message = "auth.required_field") 
    @Email(message = "auth.email_invalid")
    @Size(max = 255, message = "auth.email_max_length")
    String email
) {
}
