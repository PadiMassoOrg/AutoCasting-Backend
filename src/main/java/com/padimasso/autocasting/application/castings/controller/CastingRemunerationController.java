package com.padimasso.autocasting.application.castings.controller;


import com.padimasso.autocasting.application.castings.dto.request.CastingRemunerationPatchRequest;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleRemunerationResponse;
import com.padimasso.autocasting.application.castings.service.CastingRemunerationService;
import com.padimasso.autocasting.config.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "CastingRemuneration", description = "Operaciones relacionadas a los Remuneration de un casting.")
@SuppressWarnings("unused")
public class CastingRemunerationController {

    private final CastingRemunerationService castingRemunerationService;

    @Operation(summary = "PATCH Role Remuneration (parcial)", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping(AppConstants.CASTING_REMUNERATION_URL)
    public ResponseEntity<CastingRoleRemunerationResponse> patchRoleRemuneration(
        @Valid @RequestBody CastingRemunerationPatchRequest request
    ) {
        return ResponseEntity.ok(castingRemunerationService.patchRoleRemuneration(request));
    }

}
