package com.padimasso.autocasting.application.admin.controller;

import com.padimasso.autocasting.application.admin.dto.response.AdminCastingsPageResponse;
import com.padimasso.autocasting.application.admin.dto.response.AdminCastingDetailsResponse;
import com.padimasso.autocasting.application.admin.service.AdminCastingService;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.ADMIN_CASTINGS_API_URL;
import static com.padimasso.autocasting.config.AppConstants.ADMIN_CASTING_DETAILS_API_URL;
import static com.padimasso.autocasting.config.AppConstants.ADMIN_CASTING_ROLE_API_URL;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Administrative casting management endpoints.")
public class AdminCastingController {

    private final AdminCastingService adminCastingService;

    @Operation(
        summary = "List castings (paginated)",
        description = "Returns castings for administrative support review.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(ADMIN_CASTINGS_API_URL)
    public AdminCastingsPageResponse listCastings(
        @Parameter(description = "Page index, starting from 0.") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size.") @RequestParam(defaultValue = "20") int size,
        @Parameter(description = "Free text search over casting title.") @RequestParam(required = false) String q,
        @Parameter(description = "Casting status IDs or codes.") @RequestParam(required = false, name = "statusId")
        List<String> statusIdTokens
    ) {
        return adminCastingService.listCastings(page, size, q, statusIdTokens);
    }

    @Operation(
        summary = "Get casting details for admin",
        description = "Returns the casting basic info and a minimal role list for admin review.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(ADMIN_CASTING_DETAILS_API_URL)
    public AdminCastingDetailsResponse getCastingDetails(@Parameter(description = "Casting slug.") @PathVariable String slug) {
        return adminCastingService.getCastingDetailsBySlug(slug);
    }

    @Operation(
        summary = "Get casting role details for admin",
        description = "Returns the full casting role payload for admin review and editing.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(ADMIN_CASTING_ROLE_API_URL)
    public ResponseEntity<CastingRoleResponse> getCastingRole(@Parameter(description = "Role ID.") @PathVariable UUID roleId) {
        return ResponseEntity.ok(adminCastingService.getCastingRoleById(roleId));
    }
}
