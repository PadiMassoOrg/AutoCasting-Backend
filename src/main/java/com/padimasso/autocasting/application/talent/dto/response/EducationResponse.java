package com.padimasso.autocasting.application.talent.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record EducationResponse(
    UUID id,
    String institution,
    String courseName,
    String graduationYear,
    LocalDateTime modifiedAt
) {
}
