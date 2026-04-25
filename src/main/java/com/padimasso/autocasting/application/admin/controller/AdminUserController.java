package com.padimasso.autocasting.application.admin.controller;

import com.padimasso.autocasting.application.admin.dto.response.AdminUsersPageResponse;
import com.padimasso.autocasting.application.admin.service.AdminUserService;
import com.padimasso.autocasting.application.employer.dto.response.EmployerProfileResponse;
import com.padimasso.autocasting.application.talent.dto.response.PublicProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.ADMIN_USER_EMPLOYER_PROFILE_API_URL;
import static com.padimasso.autocasting.config.AppConstants.ADMIN_USER_TALENT_PROFILE_API_URL;
import static com.padimasso.autocasting.config.AppConstants.ADMIN_USERS_API_URL;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Operaciones administrativas")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @Operation(
        summary = "Listado paginado de usuarios",
        description = "Devuelve usuarios activos y borrados (deleted=true) para administración."
    )
    @GetMapping(ADMIN_USERS_API_URL)
    public AdminUsersPageResponse listUsers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return adminUserService.listUsers(page, size);
    }

    @Operation(
        summary = "Perfil talent de un usuario por ID",
        description = "Devuelve el detalle del perfil talent para administración."
    )
    @GetMapping(ADMIN_USER_TALENT_PROFILE_API_URL)
    public PublicProfileResponse getTalentProfileForAdmin(@PathVariable UUID userId) {
        return adminUserService.getTalentProfileForAdmin(userId);
    }

    @Operation(
        summary = "Perfil employer de un usuario por ID",
        description = "Devuelve el detalle del perfil employer para administración."
    )
    @GetMapping(ADMIN_USER_EMPLOYER_PROFILE_API_URL)
    public EmployerProfileResponse getEmployerProfileForAdmin(@PathVariable UUID userId) {
        return adminUserService.getEmployerProfileForAdmin(userId);
    }
}
