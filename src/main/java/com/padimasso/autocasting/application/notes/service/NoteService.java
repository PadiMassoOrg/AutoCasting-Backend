package com.padimasso.autocasting.application.notes.service;

import com.padimasso.autocasting.application.common.model.EntityType;

import java.util.UUID;

public interface NoteService {

    void createNote(EntityType entityType, UUID entityId, String reason);
}
