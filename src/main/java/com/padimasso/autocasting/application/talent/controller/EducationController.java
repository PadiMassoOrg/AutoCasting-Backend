package com.padimasso.autocasting.application.talent.controller;

import com.padimasso.autocasting.application.common.dto.LastModifiedResponse;
import com.padimasso.autocasting.application.talent.dto.request.EducationRequest;
import com.padimasso.autocasting.application.talent.dto.response.EducationResponse;
import com.padimasso.autocasting.application.talent.service.EducationService;
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

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Education", description = "Endpoints for managing the authenticated talent's education entries.")
@SuppressWarnings("unused")
public class EducationController {

    private final EducationService educationService;

    @Operation(
        summary = "Create education entry",
        description = "Creates a new education entry for the authenticated talent.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(AppConstants.EDUCATION_API_URL)
    public ResponseEntity<EducationResponse> createNewEducation(@Valid @RequestBody EducationRequest request) {
        return ResponseEntity.ok().body(educationService.createEducation(request));
    }

    @Operation(
        summary = "List my education entries",
        description = "Returns all education entries that belong to the authenticated talent.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(AppConstants.EDUCATION_API_URL)
    public ResponseEntity<List<EducationResponse>> listMine() {
        return ResponseEntity.ok(educationService.listMyEducation());
    }

    @Operation(
        summary = "Get education entry by ID",
        description = "Returns one education entry by ID if it belongs to the authenticated talent.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(AppConstants.EDUCATION_API_URL + "/{id}")
    public ResponseEntity<EducationResponse> getOne(@Parameter(description = "Education entry ID.") @PathVariable UUID id) {
        return ResponseEntity.ok(educationService.getMyEducation(id));
    }

    @Operation(
        summary = "Patch education entry",
        description = "Partially updates an education entry by ID for the authenticated talent.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping(AppConstants.EDUCATION_API_URL + "/{id}")
    public ResponseEntity<EducationResponse> patch(@Parameter(description = "Education entry ID.") @PathVariable UUID id,
                                                   @Valid @RequestBody EducationRequest request) {
        return ResponseEntity.ok(educationService.patchMyEducation(id, request));
    }

    @Operation(
        summary = "Soft-delete education entry",
        description = "Soft-deletes an education entry by ID for the authenticated talent.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping(AppConstants.EDUCATION_API_URL + "/{id}")
    public ResponseEntity<LastModifiedResponse> delete(@Parameter(description = "Education entry ID.") @PathVariable UUID id) {
        return ResponseEntity.ok(educationService.deleteMyEducation(id));
    }

}
