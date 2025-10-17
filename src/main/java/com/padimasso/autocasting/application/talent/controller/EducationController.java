package com.padimasso.autocasting.application.talent.controller;

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
@Tag(name = "Education", description = "Operaciones relacionadas a la Formacion del perfil del usuario.")
@SuppressWarnings("unused")
public class EducationController {

    private final EducationService educationService;

    @Operation(summary = "Creacion de una Formacion para el talent", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(AppConstants.EDUCATION_API_URL)
    public ResponseEntity<EducationResponse> createNewEducation(@Valid @RequestBody EducationRequest request) {
        return ResponseEntity.ok().body(educationService.createEducation(request));
    }

    @Operation(summary = "GET all Education", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(AppConstants.EDUCATION_API_URL)
    public ResponseEntity<List<EducationResponse>> listMine() {
        return ResponseEntity.ok(educationService.listMyEducation());
    }

    @Operation(summary = "GET Education by Id", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(AppConstants.EDUCATION_API_URL + "/{id}")
    public ResponseEntity<EducationResponse> getOne(@Parameter @PathVariable UUID id) {
        return ResponseEntity.ok(educationService.getMyEducation(id));
    }

    @Operation(summary = "PATCH Education (parcial)", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping(AppConstants.EDUCATION_API_URL + "/{id}")
    public ResponseEntity<EducationResponse> patch(@Parameter @PathVariable UUID id,
                                                   @Valid @RequestBody EducationRequest request) {
        return ResponseEntity.ok(educationService.patchMyEducation(id, request));
    }

    @Operation(summary = "DELETE Education (soft)", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping(AppConstants.EDUCATION_API_URL + "/{id}")
    public ResponseEntity<Void> delete(@Parameter @PathVariable UUID id) {
        educationService.deleteMyEducation(id);
        return ResponseEntity.noContent().build();
    }

}
