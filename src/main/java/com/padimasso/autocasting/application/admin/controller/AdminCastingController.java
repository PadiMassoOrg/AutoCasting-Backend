package com.padimasso.autocasting.application.admin.controller;

import com.padimasso.autocasting.application.admin.dto.request.AdminCastingRoleCreateRequest;
import com.padimasso.autocasting.application.admin.dto.request.AdminCastingRoleDeleteRequest;
import com.padimasso.autocasting.application.admin.dto.request.AdminCastingRoleDuplicateRequest;
import com.padimasso.autocasting.application.admin.dto.request.AdminCastingRoleUpdateRequest;
import com.padimasso.autocasting.application.admin.dto.request.AdminCastingUpdateRequest;
import com.padimasso.autocasting.application.admin.dto.response.AdminCastingDetailsResponse;
import com.padimasso.autocasting.application.admin.dto.response.AdminCastingRowResponse;
import com.padimasso.autocasting.application.admin.dto.response.AdminCloseExpiredCastingsResponse;
import com.padimasso.autocasting.application.admin.service.AdminCastingEditService;
import com.padimasso.autocasting.application.admin.service.AdminCastingService;
import com.padimasso.autocasting.application.common.dto.PageResponse;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.ADMIN_CASTING_API_URL;
import static com.padimasso.autocasting.config.AppConstants.ADMIN_CASTINGS_API_URL;
import static com.padimasso.autocasting.config.AppConstants.ADMIN_CASTINGS_CLOSE_EXPIRED_API_URL;
import static com.padimasso.autocasting.config.AppConstants.ADMIN_CASTING_DETAILS_API_URL;
import static com.padimasso.autocasting.config.AppConstants.ADMIN_CASTING_ROLE_API_URL;
import static com.padimasso.autocasting.config.AppConstants.ADMIN_CASTING_ROLE_DUPLICATE_API_URL;
import static com.padimasso.autocasting.config.AppConstants.ADMIN_CASTING_ROLES_API_URL;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Administrative casting management endpoints.")
public class AdminCastingController {

    private final AdminCastingService adminCastingService;
    private final AdminCastingEditService adminCastingEditService;

    @Operation(
        summary = "List castings (paginated)",
        description = "Returns castings for administrative support review.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(ADMIN_CASTINGS_API_URL)
    public PageResponse<AdminCastingRowResponse> listCastings(
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

    @Operation(
        summary = "Update casting basic info",
        description = "Replaces the casting's basic info on behalf of an admin and records the audit entry with the admin's reason. Only draft, published and paused castings can be edited.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping(ADMIN_CASTING_API_URL)
    public AdminCastingDetailsResponse updateCasting(
        @Parameter(description = "Casting ID.") @PathVariable UUID castingId,
        @Valid @RequestBody AdminCastingUpdateRequest request
    ) {
        return adminCastingEditService.updateCasting(castingId, request);
    }

    @Operation(
        summary = "Update casting role",
        description = "Replaces a casting role on behalf of an admin and records the audit entry with the admin's reason. The role's reference photo is left untouched. Only roles of draft, published and paused castings can be edited.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping(ADMIN_CASTING_ROLE_API_URL)
    public CastingRoleResponse updateCastingRole(
        @Parameter(description = "Role ID.") @PathVariable UUID roleId,
        @Valid @RequestBody AdminCastingRoleUpdateRequest request
    ) {
        return adminCastingEditService.updateCastingRole(roleId, request);
    }

    @Operation(
        summary = "Add a casting role",
        description = "Adds a role to a draft, published or paused casting on behalf of an admin and records the audit entry with the admin's reason. The role is created without a reference photo.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(ADMIN_CASTING_ROLES_API_URL)
    public CastingRoleResponse createCastingRole(@Valid @RequestBody AdminCastingRoleCreateRequest request) {
        return adminCastingEditService.createCastingRole(request);
    }

    @Operation(
        summary = "Duplicate a casting role",
        description = "Copies a role (without its reference photo) inside its casting and records the audit entry with the admin's reason.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(ADMIN_CASTING_ROLE_DUPLICATE_API_URL)
    public CastingRoleResponse duplicateCastingRole(
        @Parameter(description = "Role ID.") @PathVariable UUID roleId,
        @Valid @RequestBody AdminCastingRoleDuplicateRequest request
    ) {
        return adminCastingEditService.duplicateCastingRole(roleId, request);
    }

    @Operation(
        summary = "Delete a casting role",
        description = "Soft-deletes a role and records the audit entry with the admin's reason. The role's applications are kept: talents still see them, with the casting reported as closed. Rejected when the role has a selected application or is the last role of a published or paused casting.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping(ADMIN_CASTING_ROLE_API_URL)
    public void deleteCastingRole(
        @Parameter(description = "Role ID.") @PathVariable UUID roleId,
        @Valid @RequestBody AdminCastingRoleDeleteRequest request
    ) {
        adminCastingEditService.deleteCastingRole(roleId, request);
    }

    @Operation(
        summary = "Emergency close of expired castings",
        description = "Runs the nightly auto-close job on demand, for when it did not run: closes every published or paused casting whose application deadline was yesterday or earlier (Buenos Aires time). Drafts are left untouched. Safe to run repeatedly.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(ADMIN_CASTINGS_CLOSE_EXPIRED_API_URL)
    public AdminCloseExpiredCastingsResponse closeExpiredCastings() {
        return adminCastingService.closeExpiredCastings();
    }
}
