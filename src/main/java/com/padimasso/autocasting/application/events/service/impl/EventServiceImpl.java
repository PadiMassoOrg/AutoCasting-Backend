package com.padimasso.autocasting.application.events.service.impl;

import com.padimasso.autocasting.application.castings.util.CastingDeadlines;
import com.padimasso.autocasting.application.events.dto.EventFilter;
import com.padimasso.autocasting.application.events.dto.response.EventCardResponse;
import com.padimasso.autocasting.application.events.dto.response.EventDetailsResponse;
import com.padimasso.autocasting.application.events.mapper.EventMapper;
import com.padimasso.autocasting.application.events.repository.EventRepository;
import com.padimasso.autocasting.application.events.repository.specification.EventSpecs;
import com.padimasso.autocasting.application.events.service.EventService;
import com.padimasso.autocasting.application.shared.web.SliceResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.padimasso.autocasting.config.AppConstants.MAX_PAGE_SIZE;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.EVENTS_NOT_FOUND;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.GENERAL_SLUG_REQUIRED;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public SliceResponse<EventCardResponse> search(EventFilter filter, int page, int size) {
        int ps = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);

        var pageable = PageRequest.of(
            page,
            ps,
            Sort.by(Sort.Direction.ASC, "startDate", "id")
        );

        var result = eventRepository.findAll(EventSpecs.fromFilter(filter, CastingDeadlines.today()), pageable);

        List<EventCardResponse> items = result.getContent().stream()
            .map(eventMapper::toCardResponse)
            .toList();

        return new SliceResponse<>(items, result.hasNext(), page, ps, result.getTotalElements());
    }

    @Override
    @Transactional
    public EventDetailsResponse getBySlug(String slug) {
        if (slug == null || slug.isBlank()) throw new IllegalArgumentException(GENERAL_SLUG_REQUIRED);

        return eventRepository.findPublicBySlug(slug.trim())
            .map(eventMapper::toDetailsResponse)
            .orElseThrow(() -> new IllegalArgumentException(EVENTS_NOT_FOUND));
    }
}
