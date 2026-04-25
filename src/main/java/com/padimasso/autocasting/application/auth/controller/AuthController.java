package com.padimasso.autocasting.application.auth.controller;

import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.auth.dto.request.*;
import com.padimasso.autocasting.application.auth.dto.response.AuthResponse;
import com.padimasso.autocasting.application.auth.dto.response.ForgotPasswordResponse;
import com.padimasso.autocasting.application.auth.dto.response.MeResponse;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.padimasso.autocasting.config.AppConstants.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Registro, inicio de sesión y recuperación de contraseña")
@SuppressWarnings("unused")
public class AuthController {

    private final AuthService authService;
    private final AuthContext authContext;

    @Operation(
        summary = "Registro de nuevo usuario",
        description = "Permite a un usuario registrarse con correo electrónico, contraseña y rol"
    )
    @PostMapping(REGISTER_API_URL)
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(
        summary = "Inicio de sesión",
        description = "Autentica al usuario con su correo y contraseña, devuelve un JWT"
    )
    @PostMapping(LOGIN_API_URL)
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(
        summary = "Inicio de sesión admin",
        description = "Autentica al usuario administrador con correo y contraseña, devuelve un JWT"
    )
    @PostMapping(ADMIN_LOGIN_API_URL)
    public ResponseEntity<AuthResponse> adminLogin(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.adminLogin(request));
    }

    @Operation(
        summary = "Datos del Usuario autenticado",
        description = "Devuelve información básica del usuario y el estado del onboarding"
    )
    @GetMapping(ME_API_URL)
    public ResponseEntity<MeResponse> me() {
        UserEntity user = authContext.getCurrentUserOrThrow();
        return ResponseEntity.ok(MeResponse.from(user));
    }

    @Operation(
        summary = "Actualiza el estado de onboarding del usuario autenticado",
        description = "Permite settear el modo activo (TALENT/EMPLOYER) y los estados de onboarding de talento y empleador"
    )
    @PatchMapping(ONBOARDING_API_URL)
    public ResponseEntity<MeResponse> updateOnboarding(
        @Valid @RequestBody UserOnboardingRequest request
    ) {
        MeResponse response = authService.updateOnboarding(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Solicitud de recuperación de contraseña",
        description = "Envía un email con un enlace para restablecer la contraseña"
    )
    @PostMapping(FORGOT_PASS_URL)
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.sendResetPasswordEmail(request)); // No se expone si el email existe o no
    }

    @Operation(
        summary = "Restablecer contraseña",
        description = "Permite al usuario establecer una nueva contraseña usando el token recibido por email"
    )
    @PostMapping(RESET_PASS_URL)
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Cambia contraseña",
        description = "Permite al usuario establecer una nueva contraseña usando el token recibido por email"
    )
    @PostMapping(CHANGE_PASS_URL)
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok().build();
    }
}
