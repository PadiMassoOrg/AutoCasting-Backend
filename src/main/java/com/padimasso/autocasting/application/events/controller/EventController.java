package com.padimasso.autocasting.application.events.controller;

import com.padimasso.autocasting.application.events.dto.EventFilter;
import com.padimasso.autocasting.application.events.dto.response.EventCardResponse;
import com.padimasso.autocasting.application.events.dto.response.EventDetailsResponse;
import com.padimasso.autocasting.application.events.service.EventService;
import com.padimasso.autocasting.application.shared.web.SliceResponse;
import com.padimasso.autocasting.config.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@Tag(name = "Events", description = "Public event discovery.")
@SuppressWarnings("unused")
public class EventController {

    private final EventService eventService;

    @Operation(summary = "Search public events (only events that have not ended; overlap with dateFrom/dateTo)")
    @GetMapping(AppConstants.EVENTS_API_URL)
    public SliceResponse<EventCardResponse> searchEvents(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "8") int size,
        @RequestParam(required = false) LocalDate dateFrom,
        @RequestParam(required = false) LocalDate dateTo
    ) {
        return eventService.search(new EventFilter(dateFrom, dateTo), page, size);
    }

    @Operation(summary = "Get a public event by slug")
    @GetMapping(AppConstants.EVENTS_API_URL + "/{slug}")
    public EventDetailsResponse getEvent(@PathVariable String slug) {
        return eventService.getBySlug(slug);
    }
}
