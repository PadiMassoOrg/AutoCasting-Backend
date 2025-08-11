package com.padimasso.autocasting.application.profile.controller;

import com.padimasso.autocasting.application.profile.dto.request.CreditRequest;
import com.padimasso.autocasting.application.profile.dto.response.CreditResponse;
import com.padimasso.autocasting.application.profile.service.CreditService;
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
@Tag(name = "Credits", description = "Operaciones relacionadas a los Credits del perfil del usuario.")
@SuppressWarnings("unused")
public class CreditController {

    private final CreditService creditsService;

    @Operation(summary = "Creacion de Credit para el profile", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(AppConstants.CREDIT_API_URL)
    public ResponseEntity<CreditResponse> createNewCredit(@Valid @RequestBody CreditRequest request) {
        return ResponseEntity.ok().body(creditsService.createCredit(request));
    }

}
