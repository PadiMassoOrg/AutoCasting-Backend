package com.padimasso.autocasting.application.admin.controller;

import com.padimasso.autocasting.application.admin.dto.response.AdminHistoryRowResponse;
import com.padimasso.autocasting.application.admin.service.AdminHistoryService;
import com.padimasso.autocasting.application.common.dto.PageResponse;
import com.padimasso.autocasting.application.common.model.EntityType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.ADMIN_HISTORY_ENTITY_API_URL;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Administrative modification history endpoints.")
public class AdminHistoryController {

    private final AdminHistoryService adminHistoryService;

    @Operation(
        summary = "List modification history for an entity",
        description = "Returns the modification history for a specific entity type and ID, without filters.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(ADMIN_HISTORY_ENTITY_API_URL)
    public PageResponse<AdminHistoryRowResponse> listEntityHistory(
        @Parameter(description = "Entity type.") @PathVariable EntityType entityType,
        @Parameter(description = "Entity ID.") @PathVariable UUID entityId,
        @Parameter(description = "Page index, starting from 0.") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size.") @RequestParam(defaultValue = "20") int size
    ) {
        return adminHistoryService.listHistory(entityType, entityId, page, size);
    }
}
