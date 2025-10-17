package com.padimasso.autocasting.application.talent.dto.response;

import java.util.UUID;

public record EducationResponse(
    UUID id,
    String institution,
    String courseName,
    String graduationYear
) {
}
