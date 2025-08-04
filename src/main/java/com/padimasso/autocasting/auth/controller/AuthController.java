package com.padimasso.autocasting.auth.controller;

import com.padimasso.autocasting.auth.dto.request.ForgotPasswordRequest;
import com.padimasso.autocasting.auth.dto.request.LoginRequest;
import com.padimasso.autocasting.auth.dto.request.RegisterRequest;
import com.padimasso.autocasting.auth.dto.request.ResetPasswordRequest;
import com.padimasso.autocasting.auth.dto.response.AuthResponse;
import com.padimasso.autocasting.auth.dto.response.ForgotPasswordResponse;
import com.padimasso.autocasting.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.padimasso.autocasting.config.AppConstants.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "1. Autenticación", description = "Registro, inicio de sesión y recuperación de contraseña")
@SuppressWarnings("unused")
public class AuthController {

    private final AuthService authService;

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
}
