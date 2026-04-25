package com.padimasso.autocasting.application.castings.controller;

import com.padimasso.autocasting.application.castings.dto.request.section.CastingBasicInfoPatchRequest;
import com.padimasso.autocasting.application.castings.dto.response.section.CastingBasicInfoResponse;
import com.padimasso.autocasting.application.castings.service.CastingBasicInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.CASTING_BASIC_INFO_URL;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Casting Basic Info", description = "Endpoints for managing the casting basic-info section.")
@SuppressWarnings("unused")
public class CastingBasicInfoController {
    private final CastingBasicInfoService castingBasicInfoService;

    @Operation(
        summary = "Get basic info by section ID",
        description = "Returns casting basic-info section data for the provided section ID.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(CASTING_BASIC_INFO_URL + "/{sectionId}")
    public ResponseEntity<CastingBasicInfoResponse> getBasicInfoBySectionId(
        @Parameter(description = "Casting basic-info section ID.") @PathVariable UUID sectionId
    ) {
        return ResponseEntity.ok().body(castingBasicInfoService.getBySectionId(sectionId));
    }

    @Operation(
        summary = "Patch casting basic info",
        description = "Partially updates the casting basic-info section.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping(CASTING_BASIC_INFO_URL)
    public ResponseEntity<CastingBasicInfoResponse> patchMyBasicInfo(@Valid @RequestBody CastingBasicInfoPatchRequest request) {
        return ResponseEntity.ok(castingBasicInfoService.patchCastingBasicInfo(request));
    }

}
