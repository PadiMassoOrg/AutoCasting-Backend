package com.padimasso.autocasting.application.castings.controller;

import com.padimasso.autocasting.application.castings.dto.EmployerCastingRoleFilter;
import com.padimasso.autocasting.application.castings.dto.request.CastingRoleRequest;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleResponse;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingRoleEmployerCardResponse;
import com.padimasso.autocasting.application.castings.service.CastingRoleService;
import com.padimasso.autocasting.application.common.dto.LastModifiedResponse;
import com.padimasso.autocasting.config.AppConstants;
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
@Tag(name = "Casting Roles", description = "Endpoints for managing casting roles.")
@SuppressWarnings("unused")
public class CastingRoleController {

    private final CastingRoleService castingRoleService;

    @GetMapping(AppConstants.CASTING_ROLE_ROLES_URL + "/{castingId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<CastingRoleEmployerCardResponse>> getRolesByCastingId(
        @PathVariable UUID castingId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "50") int size
    ) {
        return ResponseEntity.ok(castingRoleService.getCastingRolesByCastingId(new EmployerCastingRoleFilter(castingId), page, size));
    }

    @GetMapping(AppConstants.CASTING_ROLE_URL + "/details/{roleId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CastingRoleResponse> getRoleById(@PathVariable UUID roleId) {
        return ResponseEntity.ok(castingRoleService.getById(roleId));
    }

    @PostMapping(AppConstants.CASTING_ROLE_URL)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CastingRoleResponse> createNewCastingRole(@Valid @RequestBody CastingRoleRequest request) {
        return ResponseEntity.ok(castingRoleService.createCastingRole(request));
    }

    @PutMapping(AppConstants.CASTING_ROLE_URL + "/{roleId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CastingRoleResponse> updateCastingRole(
        @PathVariable UUID roleId,
        @Valid @RequestBody CastingRoleRequest request
    ) {
        return ResponseEntity.ok(castingRoleService.updateCastingRole(roleId, request));
    }

    @DeleteMapping(AppConstants.CASTING_ROLE_URL + "/{roleId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<LastModifiedResponse> deleteCastingRole(@PathVariable UUID roleId) {
        return ResponseEntity.ok(castingRoleService.deleteCastingRole(roleId));
    }

    @PostMapping(AppConstants.CASTING_ROLE_URL + "/{roleId}/duplicate")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CastingRoleResponse> duplicateCastingRole(@PathVariable UUID roleId) {
        return ResponseEntity.ok(castingRoleService.duplicateCastingRole(roleId));
    }
}
