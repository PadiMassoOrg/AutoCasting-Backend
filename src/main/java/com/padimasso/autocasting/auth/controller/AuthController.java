package com.padimasso.autocasting.auth.controller;

import com.padimasso.autocasting.auth.dto.request.ForgotPasswordRequest;
import com.padimasso.autocasting.auth.dto.request.LoginRequest;
import com.padimasso.autocasting.auth.dto.request.RegisterRequest;
import com.padimasso.autocasting.auth.dto.request.ResetPasswordRequest;
import com.padimasso.autocasting.auth.dto.response.AuthResponse;
import com.padimasso.autocasting.auth.dto.response.ForgotPasswordResponse;
import com.padimasso.autocasting.auth.service.AuthService;
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
@SuppressWarnings("unused")
public class AuthController {

    private final AuthService authService;

    @PostMapping(REGISTER_API_URL)
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping(LOGIN_API_URL)
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping(FORGOT_PASS_URL)
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.sendResetPasswordEmail(request)); // No se expone si el email existe o no
    }

    @PostMapping(RESET_PASS_URL)
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }
}
