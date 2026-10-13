package com.padimasso.autocasting.application.admin.mapper;

import com.padimasso.autocasting.application.admin.dto.response.AdminEventRowResponse;
import com.padimasso.autocasting.application.common.dto.PageResponse;
import com.padimasso.autocasting.application.events.model.EventEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminEventMapper {

    public AdminEventRowResponse toRowResponse(EventEntity event) {
        return new AdminEventRowResponse(
            event.getId(),
            event.getSlug(),
            event.getName(),
            event.getStartDate(),
            event.getEndDate()
        );
    }

    public PageResponse<AdminEventRowResponse> toPageResponse(List<AdminEventRowResponse> items, Page<?> result) {
        return new PageResponse<>(
            items,
            result.getNumber(),
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages(),
            result.hasNext()
        );
    }
}
