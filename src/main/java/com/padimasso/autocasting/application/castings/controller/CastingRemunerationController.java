package com.padimasso.autocasting.application.castings.controller;


import com.padimasso.autocasting.application.castings.dto.request.CastingRemunerationPatchRequest;
import com.padimasso.autocasting.application.castings.dto.request.section.CastingRemunerationsSectionPatchRequest;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleRemunerationResponse;
import com.padimasso.autocasting.application.castings.dto.response.section.CastingRemunerationsSectionResponse;
import com.padimasso.autocasting.application.castings.service.CastingRemunerationService;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "CastingRemuneration", description = "Operaciones relacionadas a los Remuneration de un casting.")
@SuppressWarnings("unused")
public class CastingRemunerationController {

    private final CastingRemunerationService castingRemunerationService;

    @Operation(summary = "GET Remunerations Section by SECTION ID", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(CASTING_REMUNERATION_URL + "/{sectionId}")
    public ResponseEntity<CastingRemunerationsSectionResponse> getSectionRemunerationsById(@PathVariable UUID sectionId) {
        return ResponseEntity.ok().body(castingRemunerationService.getBySectionId(sectionId));
    }

    @Operation(summary = "PATCH Section Remuneration (parcial)", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping(CASTING_REMUNERATION_URL)
    public ResponseEntity<CastingRemunerationsSectionResponse> patchSectionRemuneration(@Valid @RequestBody CastingRemunerationsSectionPatchRequest request) {
        return ResponseEntity.ok(castingRemunerationService.patchSectionRemuneration(request));
    }

    @Operation(summary = "PATCH Role Remuneration (parcial)", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping(CASTING_REMUNERATION_REMUNERATIONS_URL)
    public ResponseEntity<CastingRoleRemunerationResponse> patchRoleRemuneration(@Valid @RequestBody CastingRemunerationPatchRequest request) {
        return ResponseEntity.ok(castingRemunerationService.patchRoleRemuneration(request));
    }

}
