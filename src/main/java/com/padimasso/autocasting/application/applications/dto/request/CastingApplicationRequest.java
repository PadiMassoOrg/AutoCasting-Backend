package com.padimasso.autocasting.application.applications.dto.request;


import java.util.List;
import java.util.UUID;

public record CastingApplicationRequest(
    String message,
    List<RequirementSubmission> submissions
) {
    public record RequirementSubmission(
        UUID castingRequirementId,
        String audioUrl,
        String videoUrl,
        String notes
    ) {
    }
}
