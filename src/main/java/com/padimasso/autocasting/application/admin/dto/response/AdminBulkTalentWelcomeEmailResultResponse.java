package com.padimasso.autocasting.application.admin.dto.response;

import java.util.List;
import java.util.UUID;

public record AdminBulkTalentWelcomeEmailResultResponse(
    int sentCount,
    int failedCount,
    List<Failure> failures
) {
    public record Failure(
        UUID userId,
        String reason
    ) {
    }
}
