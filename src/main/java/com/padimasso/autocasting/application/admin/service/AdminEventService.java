package com.padimasso.autocasting.application.admin.service;

import com.padimasso.autocasting.application.admin.dto.response.AdminEventRowResponse;
import com.padimasso.autocasting.application.common.dto.PageResponse;
import com.padimasso.autocasting.application.events.dto.response.EventDetailsResponse;

import java.time.LocalDate;

public interface AdminEventService {
    PageResponse<AdminEventRowResponse> listEvents(int page, int size, String q, LocalDate dateFrom, LocalDate dateTo);
    EventDetailsResponse getEventDetailsBySlug(String slug);
}
