package com.padimasso.autocasting.application.admin.notes.controller;

import com.padimasso.autocasting.application.admin.notes.dto.request.CreateAdminNoteRequest;
import com.padimasso.autocasting.application.admin.notes.dto.request.UpdateAdminNoteRequest;
import com.padimasso.autocasting.application.admin.notes.dto.response.AdminNoteResponse;
import com.padimasso.autocasting.application.admin.notes.model.AdminNoteEntityType;
import com.padimasso.autocasting.application.admin.notes.service.AdminNoteService;
import com.padimasso.autocasting.application.shared.web.SliceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.ADMIN_NOTE_BY_ID_API_URL;
import static com.padimasso.autocasting.config.AppConstants.ADMIN_NOTES_API_URL;
import static com.padimasso.autocasting.config.AppConstants.ADMIN_NOTES_BY_ENTITY_API_URL;
import static com.padimasso.autocasting.config.AppConstants.ADMIN_NOTES_BY_RELATED_USER_API_URL;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin Notes", description = "Administrative notes endpoints for internal operational context.")
public class AdminNoteController {

    private final AdminNoteService adminNoteService;

    @Operation(
        summary = "Create admin note",
        description = "Creates an internal administrative note for a target entity.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(ADMIN_NOTES_API_URL)
    public ResponseEntity<AdminNoteResponse> createNote(@Valid @RequestBody CreateAdminNoteRequest request) {
        return ResponseEntity.ok(adminNoteService.createNote(request));
    }

    @Operation(
        summary = "List notes by entity",
        description = "Returns paginated notes for a concrete entity.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(ADMIN_NOTES_BY_ENTITY_API_URL)
    public SliceResponse<AdminNoteResponse> listNotesByEntity(
        @Parameter(description = "Entity type.") @PathVariable AdminNoteEntityType entityType,
        @Parameter(description = "Entity ID.") @PathVariable UUID entityId,
        @Parameter(description = "Page index, starting from 0.") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size.") @RequestParam(defaultValue = "20") int size
    ) {
        return adminNoteService.listNotesByEntity(entityType, entityId, page, size);
    }

    @Operation(
        summary = "List notes by related user",
        description = "Returns paginated notes associated to a given root user, including notes created on related entities.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(ADMIN_NOTES_BY_RELATED_USER_API_URL)
    public SliceResponse<AdminNoteResponse> listNotesByRelatedUser(
        @Parameter(description = "Root user ID.") @PathVariable("userId") UUID userId,
        @Parameter(description = "Page index, starting from 0.") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size.") @RequestParam(defaultValue = "20") int size
    ) {
        return adminNoteService.listNotesByRelatedUser(userId, page, size);
    }

    @Operation(
        summary = "Update admin note",
        description = "Updates editable fields of an internal admin note.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping(ADMIN_NOTE_BY_ID_API_URL)
    public ResponseEntity<AdminNoteResponse> updateNote(
        @Parameter(description = "Note ID.") @PathVariable UUID noteId,
        @RequestBody UpdateAdminNoteRequest request
    ) {
        return ResponseEntity.ok(adminNoteService.updateNote(noteId, request));
    }

    @Operation(
        summary = "Delete admin note",
        description = "Soft deletes an internal admin note.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping(ADMIN_NOTE_BY_ID_API_URL)
    public ResponseEntity<Void> deleteNote(
        @Parameter(description = "Note ID.") @PathVariable UUID noteId
    ) {
        adminNoteService.deleteNote(noteId);
        return ResponseEntity.noContent().build();
    }
}
