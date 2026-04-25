package com.padimasso.autocasting.application.auth.controller;

import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.auth.dto.request.*;
import com.padimasso.autocasting.application.auth.dto.response.AuthResponse;
import com.padimasso.autocasting.application.auth.dto.response.ForgotPasswordResponse;
import com.padimasso.autocasting.application.auth.dto.response.MeResponse;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.padimasso.autocasting.config.AppConstants.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Registration, authentication, onboarding, and password recovery endpoints.")
@SuppressWarnings("unused")
public class AuthController {

    private final AuthService authService;
    private final AuthContext authContext;

    @Operation(
        summary = "Register user",
        description = "Registers a new user with email, password, and role."
    )
    @PostMapping(REGISTER_API_URL)
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(
        summary = "User login",
        description = "Authenticates a user with email and password and returns a JWT."
    )
    @PostMapping(LOGIN_API_URL)
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(
        summary = "Admin login",
        description = "Authenticates an admin user with email and password and returns a JWT."
    )
    @PostMapping(ADMIN_LOGIN_API_URL)
    public ResponseEntity<AuthResponse> adminLogin(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.adminLogin(request));
    }

    @Operation(
        summary = "Get authenticated user",
        description = "Returns basic user information and onboarding status.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(ME_API_URL)
    public ResponseEntity<MeResponse> me() {
        UserEntity user = authContext.getCurrentUserOrThrow();
        return ResponseEntity.ok(MeResponse.from(user));
    }

    @Operation(
        summary = "Update onboarding state",
        description = "Updates active mode (TALENT/EMPLOYER) and onboarding statuses.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping(ONBOARDING_API_URL)
    public ResponseEntity<MeResponse> updateOnboarding(
        @Valid @RequestBody UserOnboardingRequest request
    ) {
        MeResponse response = authService.updateOnboarding(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Request password reset",
        description = "Sends a password reset email when the address is eligible."
    )
    @PostMapping(FORGOT_PASS_URL)
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.sendResetPasswordEmail(request)); // No se expone si el email existe o no
    }

    @Operation(
        summary = "Reset password",
        description = "Sets a new password using the reset token."
    )
    @PostMapping(RESET_PASS_URL)
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Change password",
        description = "Changes the password for the authenticated user.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(CHANGE_PASS_URL)
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok().build();
    }
}
