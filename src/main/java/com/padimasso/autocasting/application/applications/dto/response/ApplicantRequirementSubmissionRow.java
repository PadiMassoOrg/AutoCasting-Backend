package com.padimasso.autocasting.application.applications.dto.response;

import java.util.UUID;

public record ApplicantRequirementSubmissionRow(
    UUID castingRequirementId,
    boolean requiresAudio,
    boolean requiresVideo,
    String audioUrl,
    String videoUrl,
    String notes
) {
}
