package com.padimasso.autocasting.application.profile.controller;

import com.padimasso.autocasting.application.profile.dto.request.EducationRequest;
import com.padimasso.autocasting.application.profile.dto.response.EducationResponse;
import com.padimasso.autocasting.application.profile.service.EducationService;
import com.padimasso.autocasting.config.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Education", description = "Operaciones relacionadas a la Formacion del perfil del usuario.")
@SuppressWarnings("unused")
public class EducationController {

    private final EducationService educationService;

    @Operation(summary = "Creacion de una Formacion para el profile", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(AppConstants.EDUCATION_API_URL)
    public ResponseEntity<EducationResponse> createNewEducation(@Valid @RequestBody EducationRequest request) {
        return ResponseEntity.ok().body(educationService.createEducation(request));
    }

}
