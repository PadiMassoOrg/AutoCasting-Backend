package com.padimasso.autocasting.application.admin.service.impl;

import com.padimasso.autocasting.application.admin.dto.response.AdminEventRowResponse;
import com.padimasso.autocasting.application.admin.mapper.AdminEventMapper;
import com.padimasso.autocasting.application.admin.repository.specification.AdminEventSpecs;
import com.padimasso.autocasting.application.admin.service.AdminEventService;
import com.padimasso.autocasting.application.common.dto.PageResponse;
import com.padimasso.autocasting.application.events.dto.response.EventDetailsResponse;
import com.padimasso.autocasting.application.events.mapper.EventMapper;
import com.padimasso.autocasting.application.events.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.padimasso.autocasting.config.AppConstants.MAX_PAGE_SIZE;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.EVENTS_NOT_FOUND;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.GENERAL_SLUG_REQUIRED;

@Service
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {

    private final EventRepository eventRepository;
    private final AdminEventMapper adminEventMapper;
    private final EventMapper eventMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdminEventRowResponse> listEvents(int page, int size, String q, LocalDate dateFrom, LocalDate dateTo) {
        int normalizedSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int normalizedPage = Math.max(page, 0);

        var pageable = PageRequest.of(normalizedPage, normalizedSize, Sort.by(Sort.Direction.DESC, "createdAt", "id"));
        var spec = AdminEventSpecs.fromSearchText(q);
        if (dateFrom != null || dateTo != null) {
            spec = spec.and(AdminEventSpecs.overlapsDateWindow(dateFrom, dateTo));
        }

        var result = eventRepository.findAll(spec, pageable);

        var items = result.getContent().stream()
            .map(adminEventMapper::toRowResponse)
            .toList();

        return adminEventMapper.toPageResponse(items, result);
    }

    @Override
    @Transactional(readOnly = true)
    public EventDetailsResponse getEventDetailsBySlug(String slug) {
        if (slug == null || slug.isBlank()) throw new IllegalArgumentException(GENERAL_SLUG_REQUIRED);

        return eventRepository.findAdminBySlug(slug.trim())
            .map(eventMapper::toDetailsResponse)
            .orElseThrow(() -> new IllegalArgumentException(EVENTS_NOT_FOUND));
    }
}
