package com.padimasso.autocasting.application.castings.controller;


import com.padimasso.autocasting.application.castings.dto.EmployerCastingRequirementsFilter;
import com.padimasso.autocasting.application.castings.dto.request.CastingRequirementBulkRequest;
import com.padimasso.autocasting.application.castings.dto.response.CastingRequirementResponse;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingRequirementCardResponse;
import com.padimasso.autocasting.application.castings.dto.response.section.CastingRequirementsSectionResponse;
import com.padimasso.autocasting.application.castings.service.CastingRequirementService;
import com.padimasso.autocasting.application.common.dto.LastModifiedResponse;
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

import static com.padimasso.autocasting.config.AppConstants.CASTING_REQUIREMENT_REQUIREMENTS_URL;
import static com.padimasso.autocasting.config.AppConstants.CASTING_REQUIREMENT_URL;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Casting Requirements", description = "Endpoints for managing casting requirement sections and items.")
@SuppressWarnings("unused")
public class CastingRequirementController {

    private final CastingRequirementService castingRequirementService;

    @Operation(
        summary = "Get requirement section by section ID",
        description = "Returns requirement section metadata for the provided section ID.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(CASTING_REQUIREMENT_URL + "/{sectionId}")
    public ResponseEntity<CastingRequirementsSectionResponse> getSectionRequirementsById(
        @Parameter(description = "Casting requirement section ID.") @PathVariable UUID sectionId
    ) {
        return ResponseEntity.ok().body(castingRequirementService.getBySectionId(sectionId));
    }

    @Operation(
        summary = "List requirements by section ID",
        description = "Returns paginated requirement cards for the provided section ID.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(CASTING_REQUIREMENT_REQUIREMENTS_URL + "/{sectionId}")
    public ResponseEntity<List<CastingRequirementCardResponse>> getRequirementsBySectionId(
        @Parameter(description = "Casting requirement section ID.") @PathVariable UUID sectionId,
        @Parameter(description = "Page index, starting from 0.") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size.") @RequestParam(defaultValue = "50") int size
    ) {
        var filter = new EmployerCastingRequirementsFilter(sectionId);
        return ResponseEntity.ok().body(castingRequirementService.getRequirementsBySectionId(filter, page, size));
    }

    @Operation(
        summary = "Create requirements (bulk)",
        description = "Creates multiple requirement entries for a casting section in one request.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(CASTING_REQUIREMENT_URL)
    public ResponseEntity<List<CastingRequirementCardResponse>> createRequirementsBulk(
        @Valid @RequestBody CastingRequirementBulkRequest request
    ) {
        return ResponseEntity.ok().body(castingRequirementService.createRequirementsBulk(request));
    }

    @Operation(
        summary = "Update casting requirement",
        description = "Updates one requirement entry by requirement ID.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping(CASTING_REQUIREMENT_URL + "/{requirementId}")
    public ResponseEntity<CastingRequirementResponse> updateCastingRequirement(
        @Parameter(description = "Casting requirement ID.") @PathVariable UUID requirementId,
        @Valid @RequestBody CastingRequirementBulkRequest request
    ) {
        return ResponseEntity.ok().body(castingRequirementService.updateCastingRequirement(requirementId, request));
    }

    @Operation(
        summary = "Delete casting requirement",
        description = "Soft-deletes one requirement entry by requirement ID.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping(CASTING_REQUIREMENT_URL + "/{requirementId}")
    public ResponseEntity<LastModifiedResponse> deleteCastingRequirement(
        @Parameter(description = "Casting requirement ID.") @PathVariable UUID requirementId
    ) {
        return ResponseEntity.ok(castingRequirementService.deleteCastingRequirement(requirementId));
    }


}
