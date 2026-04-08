package com.padimasso.autocasting.application.applications.dto.request;

import com.padimasso.autocasting.application.shared.validation.ValidationPatterns;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;
import java.util.UUID;

public record CastingApplicationRequest(
    String message,
    @Valid
    List<RequirementSubmission> submissions
) {
    public record RequirementSubmission(
        @NotNull(message = "applications.casting_requirement_required")
        UUID castingRequirementId,
        @Pattern(regexp = ValidationPatterns.HTTP_URL, message = "validation.url_invalid")
        String audioUrl,
        @Pattern(regexp = ValidationPatterns.HTTP_URL, message = "validation.url_invalid")
        String videoUrl,
        String notes
    ) {
    }
}
