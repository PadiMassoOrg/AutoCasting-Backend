package com.padimasso.autocasting.application.talent.dto.request;

import java.util.UUID;

public record CreditRequest(
    UUID productionTypeId,
    String projectName,
    String producerName,
    String role,
    String year
) {
}
