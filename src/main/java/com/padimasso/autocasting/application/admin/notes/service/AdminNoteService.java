package com.padimasso.autocasting.application.admin.notes.service;

import com.padimasso.autocasting.application.admin.notes.dto.request.CreateAdminNoteCommand;
import com.padimasso.autocasting.application.admin.notes.dto.request.CreateAdminNoteRequest;
import com.padimasso.autocasting.application.admin.notes.dto.request.UpdateAdminNoteRequest;
import com.padimasso.autocasting.application.admin.notes.dto.response.AdminNoteResponse;
import com.padimasso.autocasting.application.admin.notes.model.AdminNoteEntityType;
import com.padimasso.autocasting.application.shared.web.SliceResponse;

import java.util.UUID;

public interface AdminNoteService {
    AdminNoteResponse createNote(CreateAdminNoteRequest request);

    AdminNoteResponse createNote(CreateAdminNoteCommand command);

    AdminNoteResponse createManualChangeNote(
        AdminNoteEntityType entityType,
        UUID entityId,
        UUID relatedUserId,
        String title,
        String body
    );

    SliceResponse<AdminNoteResponse> listNotesByEntity(AdminNoteEntityType entityType, UUID entityId, int page, int size);

    SliceResponse<AdminNoteResponse> listNotesByRelatedUser(UUID userId, int page, int size);

    AdminNoteResponse updateNote(UUID noteId, UpdateAdminNoteRequest request);

    void deleteNote(UUID noteId);
}
