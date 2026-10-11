package com.padimasso.autocasting.application.events.mapper;

import com.padimasso.autocasting.application.events.dto.response.EventCardResponse;
import com.padimasso.autocasting.application.events.model.EventEntity;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public EventCardResponse toCardResponse(EventEntity event) {
        if (event == null) return null;

        return new EventCardResponse(
            event.getId(),
            event.getSlug(),
            event.getName(),
            event.getStartDate(),
            event.getEndDate(),
            event.getDatesInfo(),
            event.getLocation(),
            event.getImageUrl()
        );
    }
}
