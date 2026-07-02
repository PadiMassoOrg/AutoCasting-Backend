package com.padimasso.autocasting.application.history.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.padimasso.autocasting.application.common.model.EntityType;
import com.padimasso.autocasting.application.history.model.HistoryEntity;
import com.padimasso.autocasting.application.history.repository.HistoryRepository;
import com.padimasso.autocasting.application.history.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository historyRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void createHistoryEntry(EntityType entityType, UUID entityId, String note, Object changes) {
        var historyEntry = HistoryEntity.builder()
            .entityType(entityType)
            .entityId(entityId)
            .note(note)
            .changes(serializeChanges(changes))
            .build();

        historyRepository.save(historyEntry);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HistoryEntity> listHistoryByEntity(EntityType entityType, UUID entityId, Pageable pageable) {
        return historyRepository.findAllByEntityTypeAndEntityIdAndDeletedFalse(entityType, entityId, pageable);
    }

    private String serializeChanges(Object changes) {
        if (changes == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(changes);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize history changes", exception);
        }
    }
}
