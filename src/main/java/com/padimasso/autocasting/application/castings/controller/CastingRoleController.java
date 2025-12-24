package com.padimasso.autocasting.application.castings.controller;

import com.padimasso.autocasting.application.castings.dto.request.CastingRoleRequest;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleResponse;
import com.padimasso.autocasting.application.castings.service.CastingRoleService;
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
@Tag(name = "CastingRole", description = "Operaciones relacionadas a los Roles de un casting.")
@SuppressWarnings("unused")
public class CastingRoleController {

    private final CastingRoleService castingRoleService;

    @Operation(summary = "CREATE Casting Role", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(AppConstants.CASTING_ROLE_URL)
    public ResponseEntity<CastingRoleResponse> createNewCastingRole(@Valid @RequestBody CastingRoleRequest request) {
        return ResponseEntity.ok().body(castingRoleService.createCastingRole(request));
    }

}
