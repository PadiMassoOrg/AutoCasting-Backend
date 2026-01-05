package com.padimasso.autocasting.application.castings.controller;


import com.padimasso.autocasting.application.castings.dto.EmployerCastingRequirementsFilter;
import com.padimasso.autocasting.application.castings.dto.request.CastingRequirementBulkRequest;
import com.padimasso.autocasting.application.castings.dto.response.CastingRequirementResponse;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingRequirementCardResponse;
import com.padimasso.autocasting.application.castings.dto.response.section.CastingRequirementsSectionResponse;
import com.padimasso.autocasting.application.castings.service.CastingRequirementService;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "CastingRequirement", description = "Operaciones relacionadas a los Requirements de un casting.")
@SuppressWarnings("unused")
public class CastingRequirementController {

    private final CastingRequirementService castingRequirementService;

    @Operation(summary = "GET Requirements Section by SECTION ID", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(CASTING_REQUIREMENT_URL + "/{sectionId}")
    public ResponseEntity<CastingRequirementsSectionResponse> getSectionRequirementsById(@PathVariable UUID sectionId) {
        return ResponseEntity.ok().body(castingRequirementService.getBySectionId(sectionId));
    }

    @Operation(summary = "GET Requirements by SECTION ID", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(CASTING_REQUIREMENT_REQUIREMENTS_URL + "/{sectionId}")
    public ResponseEntity<List<CastingRequirementCardResponse>> getRequirementsBySectionId(
        @PathVariable UUID sectionId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "50") int size
    ) {
        var filter = new EmployerCastingRequirementsFilter(sectionId);
        return ResponseEntity.ok().body(castingRequirementService.getRequirementsBySectionId(filter, page, size));
    }

    @Operation(summary = "CREATE Requirements (bulk)", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(CASTING_REQUIREMENT_URL)
    public ResponseEntity<List<CastingRequirementCardResponse>> createRequirementsBulk(
        @Valid @RequestBody CastingRequirementBulkRequest request
    ) {
        return ResponseEntity.ok().body(castingRequirementService.createRequirementsBulk(request));
    }

    @Operation(summary = "UPDATE Casting Requirement", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping(CASTING_REQUIREMENT_URL + "/{requirementId}")
    public ResponseEntity<CastingRequirementResponse> updateCastingRequirement(
        @PathVariable UUID requirementId,
        @Valid @RequestBody CastingRequirementBulkRequest request
    ) {
        return ResponseEntity.ok().body(castingRequirementService.updateCastingRequirement(requirementId, request));
    }

    @Operation(summary = "DELETE Casting Requirement", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping(CASTING_REQUIREMENT_URL + "/{requirementId}")
    public ResponseEntity<Void> deleteCastingRequirement(@PathVariable UUID requirementId) {
        castingRequirementService.deleteCastingRequirement(requirementId);
        return ResponseEntity.noContent().build();
    }


}
