package com.padimasso.autocasting.application.castings.controller;


import com.padimasso.autocasting.application.castings.dto.request.CastingRoleRemunerationPatchRequest;
import com.padimasso.autocasting.application.castings.dto.request.section.CastingRemunerationsSectionPatchRequest;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleRemunerationResponse;
import com.padimasso.autocasting.application.castings.dto.response.section.CastingRemunerationsSectionResponse;
import com.padimasso.autocasting.application.castings.service.CastingRemunerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.CASTING_REMUNERATION_REMUNERATIONS_URL;
import static com.padimasso.autocasting.config.AppConstants.CASTING_REMUNERATION_URL;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Casting Remuneration", description = "Endpoints for managing casting remuneration sections and role remuneration.")
@SuppressWarnings("unused")
public class CastingRemunerationController {

    private final CastingRemunerationService castingRemunerationService;

    @Operation(
        summary = "Get remuneration section by section ID",
        description = "Returns remuneration section data for the provided section ID.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(CASTING_REMUNERATION_URL + "/{sectionId}")
    public ResponseEntity<CastingRemunerationsSectionResponse> getSectionRemunerationsById(
        @Parameter(description = "Casting remuneration section ID.") @PathVariable UUID sectionId
    ) {
        return ResponseEntity.ok().body(castingRemunerationService.getBySectionId(sectionId));
    }

    @Operation(
        summary = "Patch section remuneration",
        description = "Partially updates section-level remuneration data for a casting.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping(CASTING_REMUNERATION_URL)
    public ResponseEntity<CastingRemunerationsSectionResponse> patchSectionRemuneration(@Valid @RequestBody CastingRemunerationsSectionPatchRequest request) {
        return ResponseEntity.ok(castingRemunerationService.patchSectionRemuneration(request));
    }

    @Operation(
        summary = "Patch role remuneration",
        description = "Partially updates remuneration values for a specific casting role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping(CASTING_REMUNERATION_REMUNERATIONS_URL)
    public ResponseEntity<CastingRoleRemunerationResponse> patchRoleRemuneration(@Valid @RequestBody CastingRoleRemunerationPatchRequest request) {
        return ResponseEntity.ok(castingRemunerationService.patchRoleRemuneration(request));
    }

}
