package com.padimasso.autocasting.application.applications.controller;

import com.padimasso.autocasting.application.applications.dto.request.CastingApplicationRequest;
import com.padimasso.autocasting.application.applications.service.CastingApplicationService;
import com.padimasso.autocasting.config.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Casting Application", description = "Operaciones relacionadas a los Aplicantes de un Casting")
@SuppressWarnings("unused")
public class CastingApplicationController {

    private final CastingApplicationService castingApplicationService;

    @Operation(
        summary = "Apply to a casting role",
        description = "Crea una Application para un Role. Si el Role tiene requirements, requiere submissions.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(AppConstants.TALENT_CASTING_APPLICATION_URL + "/{roleId}")
    @ResponseStatus(HttpStatus.CREATED) // 201 sin body
    public void apply(
        @PathVariable UUID roleId,
        @RequestBody(required = false) CastingApplicationRequest request
    ) {
        castingApplicationService.apply(roleId, request);
    }
}
