package com.padimasso.autocasting.application.history.service.impl;

import com.padimasso.autocasting.application.common.model.EntityType;
import com.padimasso.autocasting.application.history.model.HistoryEntity;
import com.padimasso.autocasting.application.history.repository.HistoryRepository;
import com.padimasso.autocasting.application.history.service.HistoryService;
import com.padimasso.autocasting.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository historyRepository;

    @Override
    @Transactional
    public void createHistoryEntry(EntityType entityType, UUID entityId, String note) {
        if (entityType == null) {
            throw ApiException.badRequest("validation.required");
        }
        if (entityId == null) {
            throw ApiException.badRequest("validation.required");
        }

        var historyEntry = HistoryEntity.builder()
            .entityType(entityType)
            .entityId(entityId)
            .note(StringUtils.hasText(note) ? note.trim() : null)
            .build();

        historyRepository.save(historyEntry);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HistoryEntity> listHistoryByEntity(EntityType entityType, UUID entityId, Pageable pageable) {
        if (entityType == null) {
            throw ApiException.badRequest("validation.required");
        }
        if (entityId == null) {
            throw ApiException.badRequest("validation.required");
        }
        if (pageable == null) {
            throw ApiException.badRequest("validation.required");
        }

        return historyRepository.findAllByEntityTypeAndEntityIdAndDeletedFalse(entityType, entityId, pageable);
    }
}
