package com.padimasso.autocasting.application.castings.controller;

import com.padimasso.autocasting.application.castings.dto.request.section.CastingBasicInfoPatchRequest;
import com.padimasso.autocasting.application.castings.dto.response.section.CastingBasicInfoResponse;
import com.padimasso.autocasting.application.castings.service.CastingBasicInfoService;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "CastingBasicInfo", description = "Operaciones relacionadas a la BasicInfo de un casting.")
@SuppressWarnings("unused")
public class CastingBasicInfoController {
    private final CastingBasicInfoService castingBasicInfoService;

    @Operation(summary = "GET BasicInfo by SECTION ID", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(CASTING_BASIC_INFO_URL + "/{sectionId}")
    public ResponseEntity<CastingBasicInfoResponse> getBasicInfoBySectionId(@PathVariable UUID sectionId) {
        return ResponseEntity.ok().body(castingBasicInfoService.getBySectionId(sectionId));
    }

    @Operation(summary = "PATCH BasicInfo (parcial)", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping(CASTING_BASIC_INFO_URL)
    public ResponseEntity<CastingBasicInfoResponse> patchMyBasicInfo(@Valid @RequestBody CastingBasicInfoPatchRequest request) {
        return ResponseEntity.ok(castingBasicInfoService.patchCastingBasicInfo(request));
    }

}
