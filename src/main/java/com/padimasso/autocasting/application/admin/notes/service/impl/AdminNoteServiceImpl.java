package com.padimasso.autocasting.application.admin.notes.service.impl;

import com.padimasso.autocasting.application.admin.notes.dto.request.CreateAdminNoteCommand;
import com.padimasso.autocasting.application.admin.notes.dto.request.CreateAdminNoteRequest;
import com.padimasso.autocasting.application.admin.notes.dto.request.UpdateAdminNoteRequest;
import com.padimasso.autocasting.application.admin.notes.dto.response.AdminNoteResponse;
import com.padimasso.autocasting.application.admin.notes.model.AdminNoteEntity;
import com.padimasso.autocasting.application.admin.notes.model.AdminNoteEntityType;
import com.padimasso.autocasting.application.admin.notes.model.AdminNoteSource;
import com.padimasso.autocasting.application.admin.notes.model.AdminNoteType;
import com.padimasso.autocasting.application.admin.notes.repository.AdminNoteRepository;
import com.padimasso.autocasting.application.admin.notes.service.AdminNoteService;
import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.shared.web.SliceResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.MAX_PAGE_SIZE;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.ADMIN_NOTES_BODY_REQUIRED;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.ADMIN_NOTES_NOT_FOUND;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.AUTH_USER_NOT_FOUND;
import static com.padimasso.autocasting.exception.ApiException.badRequest;
import static com.padimasso.autocasting.exception.ApiException.notFound;

@Service
@RequiredArgsConstructor
public class AdminNoteServiceImpl implements AdminNoteService {

    private final AdminNoteRepository adminNoteRepository;
    private final UserRepository userRepository;
    private final AuthContext authContext;

    @Override
    @Transactional
    public AdminNoteResponse createNote(CreateAdminNoteRequest request) {
        var command = new CreateAdminNoteCommand(
            request.entityType(),
            request.entityId(),
            request.relatedUserId(),
            request.noteType(),
            AdminNoteSource.ADMIN,
            request.title(),
            request.body(),
            request.metadata(),
            Boolean.TRUE.equals(request.pinned())
        );

        return createNote(command);
    }

    @Override
    @Transactional
    public AdminNoteResponse createNote(CreateAdminNoteCommand command) {
        validateBody(command.body());

        var author = authContext.getCurrentUserOrThrow();
        var relatedUser = command.relatedUserId() != null
            ? userRepository.findById(command.relatedUserId())
                .orElseThrow(() -> notFound(AUTH_USER_NOT_FOUND))
            : null;

        var note = AdminNoteEntity.builder()
            .entityType(command.entityType())
            .entityId(command.entityId())
            .relatedUser(relatedUser)
            .authorUser(author)
            .noteType(command.noteType() != null ? command.noteType() : AdminNoteType.GENERAL)
            .source(command.source() != null ? command.source() : AdminNoteSource.ADMIN)
            .title(normalizeNullableText(command.title()))
            .body(command.body().trim())
            .metadata(normalizeMetadata(command.metadata()))
            .pinned(command.pinned())
            .build();

        return AdminNoteResponse.from(adminNoteRepository.save(note));
    }

    @Override
    @Transactional
    public AdminNoteResponse createManualChangeNote(
        AdminNoteEntityType entityType,
        UUID entityId,
        UUID relatedUserId,
        String title,
        String body
    ) {
        return createNote(new CreateAdminNoteCommand(
            entityType,
            entityId,
            relatedUserId,
            AdminNoteType.MANUAL_CHANGE,
            AdminNoteSource.ADMIN,
            title,
            body,
            Map.of(),
            false
        ));
    }

    @Override
    public SliceResponse<AdminNoteResponse> listNotesByEntity(
        AdminNoteEntityType entityType,
        UUID entityId,
        int page,
        int size
    ) {
        var result = adminNoteRepository.findAllByEntityTypeAndEntityId(
            entityType,
            entityId,
            buildPageable(page, size)
        );

        return new SliceResponse<>(
            result.getContent().stream().map(AdminNoteResponse::from).toList(),
            result.hasNext(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements()
        );
    }

    @Override
    public SliceResponse<AdminNoteResponse> listNotesByRelatedUser(UUID userId, int page, int size) {
        var result = adminNoteRepository.findAllByRelatedUser_Id(userId, buildPageable(page, size));

        return new SliceResponse<>(
            result.getContent().stream().map(AdminNoteResponse::from).toList(),
            result.hasNext(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements()
        );
    }

    @Override
    @Transactional
    public AdminNoteResponse updateNote(UUID noteId, UpdateAdminNoteRequest request) {
        var note = adminNoteRepository.findById(noteId)
            .orElseThrow(() -> notFound(ADMIN_NOTES_NOT_FOUND));

        if (request.noteType() != null) {
            note.setNoteType(request.noteType());
        }

        if (request.title() != null) {
            note.setTitle(normalizeNullableText(request.title()));
        }

        if (request.body() != null) {
            validateBody(request.body());
            note.setBody(request.body().trim());
        }

        if (request.metadata() != null) {
            note.setMetadata(normalizeMetadata(request.metadata()));
        }

        if (request.pinned() != null) {
            note.setPinned(request.pinned());
        }

        return AdminNoteResponse.from(adminNoteRepository.save(note));
    }

    @Override
    @Transactional
    public void deleteNote(UUID noteId) {
        var note = adminNoteRepository.findById(noteId)
            .orElseThrow(() -> notFound(ADMIN_NOTES_NOT_FOUND));

        adminNoteRepository.softDelete(note);
    }

    private PageRequest buildPageable(int page, int size) {
        int normalizedSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int normalizedPage = Math.max(page, 0);

        return PageRequest.of(
            normalizedPage,
            normalizedSize,
            Sort.by(
                Sort.Order.desc("pinned"),
                Sort.Order.desc("createdAt"),
                Sort.Order.desc("id")
            )
        );
    }

    private void validateBody(String body) {
        if (body == null || body.trim().isBlank()) {
            throw badRequest(ADMIN_NOTES_BODY_REQUIRED);
        }
    }

    private String normalizeNullableText(String text) {
        if (text == null) return null;
        String trimmed = text.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private Map<String, Object> normalizeMetadata(Map<String, Object> metadata) {
        if (metadata == null) return Map.of();
        return metadata;
    }
}
