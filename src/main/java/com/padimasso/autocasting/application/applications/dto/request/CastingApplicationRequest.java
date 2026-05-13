package com.padimasso.autocasting.application.applications.dto.request;

import com.padimasso.autocasting.application.shared.validation.ValidationPatterns;
import jakarta.validation.constraints.Pattern;

public record CastingApplicationRequest(
    String message,
    @Pattern(regexp = ValidationPatterns.HTTP_URL, message = "validation.url_invalid")
    String audioUrl,
    @Pattern(regexp = ValidationPatterns.HTTP_URL, message = "validation.url_invalid")
    String videoUrl,
    String notes
) {
}
