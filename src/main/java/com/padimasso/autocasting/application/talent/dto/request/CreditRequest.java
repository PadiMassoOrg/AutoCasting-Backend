package com.padimasso.autocasting.application.talent.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreditRequest(
    @NotNull(message = "talent.production_type_required")
    UUID productionTypeId,
    @Size(max = 255, message = "talent.project_name_max_length")
    String projectName,
    @Size(max = 255, message = "talent.producer_name_max_length")
    String producerName,
    @Size(max = 255, message = "talent.role_max_length")
    String role,
    @Size(max = 255, message = "talent.year_max_length")
    @Pattern(regexp = "^\\d{4}$", message = "talent.year_format")
    String year
) {
}
