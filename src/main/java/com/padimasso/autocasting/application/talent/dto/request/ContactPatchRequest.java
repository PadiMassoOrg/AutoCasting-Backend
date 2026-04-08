package com.padimasso.autocasting.application.talent.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ContactPatchRequest(
    @Size(max = 255, message = "talent.phone_number_max_length")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "talent.phone_number_format")
    String phoneNumber
) {
}
