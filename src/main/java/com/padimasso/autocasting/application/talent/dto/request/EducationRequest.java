package com.padimasso.autocasting.application.talent.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EducationRequest(
    @Size(max = 255, message = "talent.institution_max_length")
    String institution,
    @Size(max = 255, message = "talent.course_name_max_length")
    String courseName,
    @Size(max = 255, message = "talent.graduation_year_max_length")
    @Pattern(regexp = "^\\d{4}$", message = "talent.year_format")
    String graduationYear
) {
}
