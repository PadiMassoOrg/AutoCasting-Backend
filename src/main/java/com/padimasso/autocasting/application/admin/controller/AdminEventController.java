package com.padimasso.autocasting.application.admin.controller;

import com.padimasso.autocasting.application.events.dto.response.EventImageCleanupResponse;
import com.padimasso.autocasting.application.events.dto.response.EventImageCleanupStatusResponse;
import com.padimasso.autocasting.application.events.service.EventImageCleanupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.padimasso.autocasting.config.AppConstants.ADMIN_EVENTS_CLEANUP_IMAGES_API_URL;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Administrative event endpoints.")
public class AdminEventController {

    private final EventImageCleanupService eventImageCleanupService;

    @Operation(
        summary = "When the dead event images cleanup was last run",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(ADMIN_EVENTS_CLEANUP_IMAGES_API_URL)
    public EventImageCleanupStatusResponse lastCleanupRun() {
        return eventImageCleanupService.lastRun();
    }

    @Operation(
        summary = "Delete event images in storage that no live event uses (dry run by default); a real run saves its timestamp",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(ADMIN_EVENTS_CLEANUP_IMAGES_API_URL)
    public EventImageCleanupResponse cleanupEventImages(@RequestParam(defaultValue = "true") boolean dryRun) {
        return eventImageCleanupService.cleanup(dryRun);
    }
}
