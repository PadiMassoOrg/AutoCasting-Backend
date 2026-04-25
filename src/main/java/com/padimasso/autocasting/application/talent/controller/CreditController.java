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
@Tag(name = "Credits", description = "Endpoints for managing the authenticated talent's credit entries.")
@SuppressWarnings("unused")
public class CreditController {

    private final CreditService creditsService;

    @Operation(
        summary = "Create credit entry",
        description = "Creates a new credit entry for the authenticated talent.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(AppConstants.CREDIT_API_URL)
    public ResponseEntity<CreditResponse> createNewCredit(@Valid @RequestBody CreditRequest request) {
        return ResponseEntity.ok().body(creditsService.createCredit(request));
    }

    @Operation(
        summary = "List my credit entries",
        description = "Returns all credit entries that belong to the authenticated talent.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(AppConstants.CREDIT_API_URL)
    public ResponseEntity<List<CreditResponse>> listMine() {
        return ResponseEntity.ok(creditsService.listMyCredits());
    }

    @Operation(
        summary = "Get credit entry by ID",
        description = "Returns one credit entry by ID if it belongs to the authenticated talent.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(AppConstants.CREDIT_API_URL + "/{id}")
    public ResponseEntity<CreditResponse> getOne(@Parameter(description = "Credit entry ID.") @PathVariable UUID id) {
        return ResponseEntity.ok(creditsService.getMyCredit(id));
    }

    @Operation(
        summary = "Patch credit entry",
        description = "Partially updates a credit entry by ID for the authenticated talent.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping(AppConstants.CREDIT_API_URL + "/{id}")
    public ResponseEntity<CreditResponse> patch(@Parameter(description = "Credit entry ID.") @PathVariable UUID id,
                                                @Valid @RequestBody CreditRequest request) {
        return ResponseEntity.ok(creditsService.patchMyCredit(id, request));
    }

    @Operation(
        summary = "Soft-delete credit entry",
        description = "Soft-deletes a credit entry by ID for the authenticated talent.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping(AppConstants.CREDIT_API_URL + "/{id}")
    public ResponseEntity<LastModifiedResponse> delete(@Parameter(description = "Credit entry ID.") @PathVariable UUID id) {
        return ResponseEntity.ok(creditsService.deleteMyCredit(id));
    }

}
