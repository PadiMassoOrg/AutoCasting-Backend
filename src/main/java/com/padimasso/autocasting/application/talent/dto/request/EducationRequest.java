package com.padimasso.autocasting.application.talent.dto.request;

public record EducationRequest(
    String institution,
    String courseName,
    String graduationYear
) {
}
