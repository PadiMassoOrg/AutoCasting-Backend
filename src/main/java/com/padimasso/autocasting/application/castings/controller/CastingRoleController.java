package com.padimasso.autocasting.application.castings.controller;

import com.padimasso.autocasting.application.castings.dto.EmployerCastingRoleFilter;
import com.padimasso.autocasting.application.castings.dto.request.CastingRoleRequest;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleResponse;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingRoleEmployerCardResponse;
import com.padimasso.autocasting.application.castings.dto.response.section.CastingRolesSectionResponse;
import com.padimasso.autocasting.application.castings.service.CastingRoleService;
import com.padimasso.autocasting.application.common.dto.LastModifiedResponse;
import com.padimasso.autocasting.config.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.CASTING_ROLE_URL;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Casting Roles", description = "Endpoints for managing casting role sections and role entries.")
@SuppressWarnings("unused")
public class CastingRoleController {

    private final CastingRoleService castingRoleService;

    @Operation(
        summary = "Get role section by section ID",
        description = "Returns the role section metadata for the provided section ID. Employer owner access only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(CASTING_ROLE_URL + "/{sectionId}")
    public ResponseEntity<CastingRolesSectionResponse> getSectionRolesById(
        @Parameter(description = "Casting role section ID.") @PathVariable UUID sectionId
    ) {
        return ResponseEntity.ok().body(castingRoleService.getBySectionId(sectionId));
    }

    @Operation(
        summary = "List roles by section ID",
        description = "Returns paginated role cards for the provided section ID. Employer owner access only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(AppConstants.CASTING_ROLE_ROLES_URL + "/{sectionId}")
    public ResponseEntity<List<CastingRoleEmployerCardResponse>> getRolesBySectionId(
        @Parameter(description = "Casting role section ID.") @PathVariable UUID sectionId,
        @Parameter(description = "Page index, starting from 0.") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size.") @RequestParam(defaultValue = "50") int size
    ) {
        var filter = new EmployerCastingRoleFilter(sectionId);
        return ResponseEntity.ok().body(castingRoleService.getCastingRolesBySectionId(filter, page, size));
    }

    @Operation(
        summary = "Create casting role",
        description = "Creates a new role in a casting role section. Employer owner access only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(AppConstants.CASTING_ROLE_URL)
    public ResponseEntity<CastingRoleResponse> createNewCastingRole(@Valid @RequestBody CastingRoleRequest request) {
        return ResponseEntity.ok().body(castingRoleService.createCastingRole(request));
    }

    @Operation(
        summary = "Update casting role",
        description = "Updates an existing casting role by role ID. Employer owner access only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping(AppConstants.CASTING_ROLE_URL + "/{roleId}")
    public ResponseEntity<CastingRoleResponse> updateCastingRole(
        @Parameter(description = "Casting role ID.") @PathVariable UUID roleId,
        @Valid @RequestBody CastingRoleRequest request
    ) {
        return ResponseEntity.ok().body(castingRoleService.updateCastingRole(roleId, request));
    }

    @Operation(
        summary = "Delete casting role",
        description = "Soft-deletes an existing casting role by role ID. Employer owner access only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping(AppConstants.CASTING_ROLE_URL + "/{roleId}")
    public ResponseEntity<LastModifiedResponse> deleteCastingRole(
        @Parameter(description = "Casting role ID.") @PathVariable UUID roleId
    ) {
        return ResponseEntity.ok(castingRoleService.deleteCastingRole(roleId));
    }

}
