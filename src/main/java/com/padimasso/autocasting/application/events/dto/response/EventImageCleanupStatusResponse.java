package com.padimasso.autocasting.application.events.dto.response;

import java.time.LocalDateTime;

// Every field is null when the cleanup has never been run.
public record EventImageCleanupStatusResponse(
    LocalDateTime lastRunAt,
    String lastRunBy,
    Integer lastDeletedObjects
) {
}
