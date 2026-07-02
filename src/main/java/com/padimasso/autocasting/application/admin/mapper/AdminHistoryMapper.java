package com.padimasso.autocasting.application.admin.mapper;

import com.padimasso.autocasting.application.admin.dto.response.AdminHistoryRowResponse;
import com.padimasso.autocasting.application.common.dto.PageResponse;
import com.padimasso.autocasting.application.history.model.HistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminHistoryMapper {

    public AdminHistoryRowResponse toRowResponse(HistoryEntity historyEntry) {
        return new AdminHistoryRowResponse(
            historyEntry.getId(),
            historyEntry.getEntityType(),
            historyEntry.getEntityId(),
            historyEntry.getNote(),
            historyEntry.getCreatedAt(),
            historyEntry.getCreatedBy(),
            historyEntry.getModifiedAt(),
            historyEntry.getModifiedBy(),
            historyEntry.isDeleted()
        );
    }

    public PageResponse<AdminHistoryRowResponse> toPageResponse(List<AdminHistoryRowResponse> items, Page<?> result) {
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
