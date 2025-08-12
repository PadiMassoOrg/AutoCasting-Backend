package com.padimasso.autocasting.application.profile.dto.response;

import java.util.UUID;

public record EducationResponse(
    UUID id,
    String institution,
    String courseName,
    String graduationYear
) {
}
