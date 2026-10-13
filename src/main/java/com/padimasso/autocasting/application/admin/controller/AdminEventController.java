package com.padimasso.autocasting.application.admin.controller;

import com.padimasso.autocasting.application.admin.dto.response.AdminEventRowResponse;
import com.padimasso.autocasting.application.admin.service.AdminEventService;
import com.padimasso.autocasting.application.common.dto.PageResponse;
import com.padimasso.autocasting.application.events.dto.response.EventDetailsResponse;
import com.padimasso.autocasting.application.events.dto.response.EventImageCleanupResponse;
import com.padimasso.autocasting.application.events.dto.response.EventImageCleanupStatusResponse;
import com.padimasso.autocasting.application.events.service.EventImageCleanupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static com.padimasso.autocasting.config.AppConstants.ADMIN_EVENTS_API_URL;
import static com.padimasso.autocasting.config.AppConstants.ADMIN_EVENTS_CLEANUP_IMAGES_API_URL;
import static com.padimasso.autocasting.config.AppConstants.ADMIN_EVENT_DETAILS_API_URL;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Administrative event endpoints.")
public class AdminEventController {

    private final AdminEventService adminEventService;
    private final EventImageCleanupService eventImageCleanupService;

    @Operation(
        summary = "List events (paginated)",
        description = "Returns every event that is not deleted, including ended ones, for administrative review.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(ADMIN_EVENTS_API_URL)
    public PageResponse<AdminEventRowResponse> listEvents(
        @Parameter(description = "Page index, starting from 0.") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size.") @RequestParam(defaultValue = "20") int size,
        @Parameter(description = "Free text search over event name and slug.") @RequestParam(required = false) String q,
        @Parameter(description = "Only events that end on or after this date.") @RequestParam(required = false) LocalDate dateFrom,
        @Parameter(description = "Only events that start on or before this date.") @RequestParam(required = false) LocalDate dateTo
    ) {
        return adminEventService.listEvents(page, size, q, dateFrom, dateTo);
    }

    @Operation(
        summary = "Get event details for admin",
        description = "Returns the same event details as the public endpoint, even when the event already ended.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(ADMIN_EVENT_DETAILS_API_URL)
    public EventDetailsResponse getEventDetails(@Parameter(description = "Event slug.") @PathVariable String slug) {
        return adminEventService.getEventDetailsBySlug(slug);
    }

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
