package com.padimasso.autocasting.application.notes.service.impl;

import com.padimasso.autocasting.application.common.model.EntityType;
import com.padimasso.autocasting.application.notes.model.NoteEntity;
import com.padimasso.autocasting.application.notes.repository.NoteRepository;
import com.padimasso.autocasting.application.notes.service.NoteService;
import com.padimasso.autocasting.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;

    @Override
    @Transactional
    public void createNote(EntityType entityType, UUID entityId, String reason) {
        if (entityType == null) {
            throw ApiException.badRequest("validation.required");
        }
        if (entityId == null) {
            throw ApiException.badRequest("validation.required");
        }
        if (!StringUtils.hasText(reason)) {
            throw ApiException.badRequest("validation.required");
        }

        var note = NoteEntity.builder()
            .entityType(entityType)
            .entityId(entityId)
            .reason(reason.trim())
            .build();

        noteRepository.save(note);
    }
}
