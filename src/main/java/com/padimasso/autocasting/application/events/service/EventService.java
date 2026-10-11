package com.padimasso.autocasting.application.events.service;

import com.padimasso.autocasting.application.events.dto.EventFilter;
import com.padimasso.autocasting.application.events.dto.response.EventCardResponse;
import com.padimasso.autocasting.application.events.dto.response.EventDetailsResponse;
import com.padimasso.autocasting.application.shared.web.SliceResponse;

public interface EventService {
    SliceResponse<EventCardResponse> search(EventFilter filter, int page, int size);

    EventDetailsResponse getBySlug(String slug);
}
