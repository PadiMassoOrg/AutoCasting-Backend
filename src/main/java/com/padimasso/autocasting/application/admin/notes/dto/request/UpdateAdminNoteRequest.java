package com.padimasso.autocasting.application.admin.notes.dto.request;

import com.padimasso.autocasting.application.admin.notes.model.AdminNoteType;

import java.util.Map;

public record UpdateAdminNoteRequest(
    AdminNoteType noteType,
    String title,
    String body,
    Map<String, Object> metadata,
    Boolean pinned
) {
}
