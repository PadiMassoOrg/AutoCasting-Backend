package com.padimasso.autocasting.application.events.mapper;

import com.padimasso.autocasting.application.events.dto.response.EventCardResponse;
import com.padimasso.autocasting.application.events.dto.response.EventDetailsResponse;
import com.padimasso.autocasting.application.events.model.EventEntity;
import org.springframework.stereotype.Component;

import static com.padimasso.autocasting.application.talent.mapper.TalentProfileMapper.mapToSiteMetadataObject;

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

    public EventDetailsResponse toDetailsResponse(EventEntity event) {
        if (event == null) return null;

        return new EventDetailsResponse(
            event.getId(),
            event.getSlug(),
            event.getName(),
            mapToSiteMetadataObject(event.getEntryAccess()),
            event.getTicketUrl(),
            event.getStartDate(),
            event.getEndDate(),
            event.getDatesInfo(),
            event.getLocation(),
            event.getImageUrl(),
            event.getDescription()
        );
    }
}
