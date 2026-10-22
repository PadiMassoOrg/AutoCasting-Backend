package com.padimasso.autocasting.application.admin.dto.response;

import java.util.List;
import java.util.UUID;

public record AdminBulkEmailResultResponse(
    int sentCount,
    int skippedCount,
    int failedCount,
    List<Skipped> skipped,
    List<Failure> failures
) {
    public enum SkipReason {
        USER_INACTIVE,
        AUDIENCE_MISMATCH,
        UNSUBSCRIBED,
        RECENTLY_SENT
    }

    public record Skipped(
        UUID userId,
        SkipReason reason
    ) {
    }

    public record Failure(
        UUID userId,
        String reason
    ) {
    }
}
