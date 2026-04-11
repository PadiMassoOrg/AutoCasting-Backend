package com.padimasso.autocasting.application.talent.controller;

import com.padimasso.autocasting.application.common.dto.LastModifiedResponse;
import com.padimasso.autocasting.application.talent.dto.request.CreditRequest;
import com.padimasso.autocasting.application.talent.dto.response.CreditResponse;
import com.padimasso.autocasting.application.talent.service.CreditService;
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
@Tag(name = "Credits", description = "Operaciones relacionadas a los Credits del perfil del usuario.")
@SuppressWarnings("unused")
public class CreditController {

    private final CreditService creditsService;

    @Operation(summary = "CREATE Credit", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(AppConstants.CREDIT_API_URL)
    public ResponseEntity<CreditResponse> createNewCredit(@Valid @RequestBody CreditRequest request) {
        return ResponseEntity.ok().body(creditsService.createCredit(request));
    }

    @Operation(summary = "GET all Credits", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(AppConstants.CREDIT_API_URL)
    public ResponseEntity<List<CreditResponse>> listMine() {
        return ResponseEntity.ok(creditsService.listMyCredits());
    }

    @Operation(summary = "GET Credit by Id", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(AppConstants.CREDIT_API_URL + "/{id}")
    public ResponseEntity<CreditResponse> getOne(@Parameter @PathVariable UUID id) {
        return ResponseEntity.ok(creditsService.getMyCredit(id));
    }

    @Operation(summary = "PATCH Credit (parcial)", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping(AppConstants.CREDIT_API_URL + "/{id}")
    public ResponseEntity<CreditResponse> patch(@Parameter @PathVariable UUID id,
                                                @Valid @RequestBody CreditRequest request) {
        return ResponseEntity.ok(creditsService.patchMyCredit(id, request));
    }

    @Operation(summary = "DELETE Credit (soft)", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping(AppConstants.CREDIT_API_URL + "/{id}")
    public ResponseEntity<LastModifiedResponse> delete(@Parameter @PathVariable UUID id) {
        return ResponseEntity.ok(creditsService.deleteMyCredit(id));
    }

}
