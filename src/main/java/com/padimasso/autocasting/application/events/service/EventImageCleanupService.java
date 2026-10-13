package com.padimasso.autocasting.application.events.service;

import com.padimasso.autocasting.application.events.dto.response.EventImageCleanupResponse;
import com.padimasso.autocasting.application.events.dto.response.EventImageCleanupStatusResponse;

public interface EventImageCleanupService {
    // Deletes the event images in storage that no live event uses. A dry run only reports what it would delete.
    // A completed real run saves its timestamp as the last run.
    EventImageCleanupResponse cleanup(boolean dryRun);

    EventImageCleanupStatusResponse lastRun();
}
