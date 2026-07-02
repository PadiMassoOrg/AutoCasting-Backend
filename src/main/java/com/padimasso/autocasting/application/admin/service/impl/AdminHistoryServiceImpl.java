package com.padimasso.autocasting.application.admin.service.impl;

import com.padimasso.autocasting.application.admin.dto.response.AdminHistoryRowResponse;
import com.padimasso.autocasting.application.admin.mapper.AdminHistoryMapper;
import com.padimasso.autocasting.application.admin.service.AdminHistoryService;
import com.padimasso.autocasting.application.common.dto.PageResponse;
import com.padimasso.autocasting.application.common.model.EntityType;
import com.padimasso.autocasting.application.history.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.MAX_PAGE_SIZE;

@Service
@RequiredArgsConstructor
public class AdminHistoryServiceImpl implements AdminHistoryService {

    private final HistoryService historyService;
    private final AdminHistoryMapper adminHistoryMapper;

    @Override
    public PageResponse<AdminHistoryRowResponse> listHistory(EntityType entityType, UUID entityId, int page, int size) {
        int normalizedSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int normalizedPage = Math.max(page, 0);

        var pageable = PageRequest.of(
            normalizedPage,
            normalizedSize,
            Sort.by(Sort.Direction.DESC, "createdAt", "id")
        );

        var result = historyService.listHistoryByEntity(entityType, entityId, pageable);
        var items = result.getContent().stream()
            .map(adminHistoryMapper::toRowResponse)
            .toList();

        return adminHistoryMapper.toPageResponse(items, result);
    }
}
