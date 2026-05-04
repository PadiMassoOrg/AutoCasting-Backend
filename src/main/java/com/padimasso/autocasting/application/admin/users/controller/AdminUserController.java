package com.padimasso.autocasting.application.admin.users.controller;

import com.padimasso.autocasting.application.admin.users.dto.request.AdminUserAccountActionRequest;
import com.padimasso.autocasting.application.admin.users.dto.response.AdminUserRowResponse;
import com.padimasso.autocasting.application.admin.users.dto.response.AdminUsersPageResponse;
import com.padimasso.autocasting.application.admin.users.service.AdminUserService;
import com.padimasso.autocasting.application.employer.dto.response.EmployerProfileResponse;
import com.padimasso.autocasting.application.talent.dto.response.PublicProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.ADMIN_USER_BLOCK_API_URL;
import static com.padimasso.autocasting.config.AppConstants.ADMIN_USER_EMPLOYER_PROFILE_API_URL;
import static com.padimasso.autocasting.config.AppConstants.ADMIN_USER_RESTORE_API_URL;
import static com.padimasso.autocasting.config.AppConstants.ADMIN_USER_TALENT_PROFILE_API_URL;
import static com.padimasso.autocasting.config.AppConstants.ADMIN_USERS_API_URL;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Administrative user management endpoints.")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @Operation(
        summary = "List users (paginated)",
        description = "Returns active and soft-deleted users for administrative review.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(ADMIN_USERS_API_URL)
    public AdminUsersPageResponse listUsers(
        @Parameter(description = "Page index, starting from 0.") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size.") @RequestParam(defaultValue = "20") int size
    ) {
        return adminUserService.listUsers(page, size);
    }

    @Operation(
        summary = "Block user account",
        description = "Blocks a user account and creates an internal administrative note.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping(ADMIN_USER_BLOCK_API_URL)
    public ResponseEntity<AdminUserRowResponse> blockUser(
        @Parameter(description = "User ID.") @PathVariable UUID userId,
        @Valid @RequestBody AdminUserAccountActionRequest request
    ) {
        return ResponseEntity.ok(adminUserService.blockUser(userId, request));
    }

    @Operation(
        summary = "Restore blocked user account",
        description = "Restores a blocked user account and creates an internal administrative note.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping(ADMIN_USER_RESTORE_API_URL)
    public ResponseEntity<AdminUserRowResponse> restoreUser(
        @Parameter(description = "User ID.") @PathVariable UUID userId,
        @Valid @RequestBody AdminUserAccountActionRequest request
    ) {
        return ResponseEntity.ok(adminUserService.restoreUser(userId, request));
    }

    @Operation(
        summary = "Get user's talent profile by ID",
        description = "Returns the talent profile details for administrative usage.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(ADMIN_USER_TALENT_PROFILE_API_URL)
    public PublicProfileResponse getTalentProfileForAdmin(
        @Parameter(description = "User ID.") @PathVariable UUID userId
    ) {
        return adminUserService.getTalentProfileForAdmin(userId);
    }

    @Operation(
        summary = "Get user's employer profile by ID",
        description = "Returns the employer profile details for administrative usage.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(ADMIN_USER_EMPLOYER_PROFILE_API_URL)
    public EmployerProfileResponse getEmployerProfileForAdmin(
        @Parameter(description = "User ID.") @PathVariable UUID userId
    ) {
        return adminUserService.getEmployerProfileForAdmin(userId);
    }
}
