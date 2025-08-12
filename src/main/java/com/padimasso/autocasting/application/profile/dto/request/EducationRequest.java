package com.padimasso.autocasting.application.profile.dto.request;

public record EducationRequest(
    String institution,
    String courseName,
    String graduationYear
) {
}
