package com.padimasso.autocasting.auth.controller;

import com.padimasso.autocasting.auth.dto.request.LoginRequest;
import com.padimasso.autocasting.auth.dto.request.RegisterRequest;
import com.padimasso.autocasting.auth.dto.response.AuthResponse;
import com.padimasso.autocasting.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.padimasso.autocasting.config.AppConstants.LOGIN_API_URL;
import static com.padimasso.autocasting.config.AppConstants.REGISTER_API_URL;

@RestController
@RequestMapping
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class AuthController {

    private final AuthService authService;

    @PostMapping(REGISTER_API_URL)
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping(LOGIN_API_URL)
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
