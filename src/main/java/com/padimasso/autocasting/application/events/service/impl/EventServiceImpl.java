package com.padimasso.autocasting.application.events.service.impl;

import com.padimasso.autocasting.application.castings.util.CastingDeadlines;
import com.padimasso.autocasting.application.events.dto.EventFilter;
import com.padimasso.autocasting.application.events.dto.response.EventCardResponse;
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
}
