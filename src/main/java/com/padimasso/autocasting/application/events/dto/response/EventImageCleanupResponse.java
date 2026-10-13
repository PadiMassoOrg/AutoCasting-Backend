package com.padimasso.autocasting.application.events.dto.response;

import java.time.LocalDateTime;

// recordedAt is the timestamp saved as the last run; null for a dry run or a run that did not complete.
public record EventImageCleanupResponse(
    boolean dryRun,
    int listedObjects,
    int candidateObjects,
    int liveEvents,
    int deadObjects,
    int deletedObjects,
    String abortReason,
    LocalDateTime recordedAt
) {
}
