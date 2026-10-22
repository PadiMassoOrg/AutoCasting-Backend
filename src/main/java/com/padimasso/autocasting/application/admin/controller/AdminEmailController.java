package com.padimasso.autocasting.application.admin.controller;

import com.padimasso.autocasting.application.admin.dto.response.AdminEmailTypesResponse;
import com.padimasso.autocasting.application.admin.service.AdminBulkEmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.padimasso.autocasting.config.AppConstants.ADMIN_EMAIL_TYPES_API_URL;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Administrative email endpoints.")
public class AdminEmailController {

    private final AdminBulkEmailService adminBulkEmailService;

    @Operation(
        summary = "List bulk email types",
        description = "Returns the emails an admin can bulk send, each with its target audience, plus the resend cooldown in days.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(ADMIN_EMAIL_TYPES_API_URL)
    public AdminEmailTypesResponse listEmailTypes() {
        return adminBulkEmailService.listEmailTypes();
    }
}
