package com.padimasso.autocasting.auth.service.impl;

import com.padimasso.autocasting.auth.dto.request.ForgotPasswordRequest;
import com.padimasso.autocasting.auth.dto.request.LoginRequest;
import com.padimasso.autocasting.auth.dto.request.RegisterRequest;
import com.padimasso.autocasting.auth.dto.request.ResetPasswordRequest;
import com.padimasso.autocasting.auth.dto.response.AuthResponse;
import com.padimasso.autocasting.auth.dto.response.ForgotPasswordResponse;
import com.padimasso.autocasting.auth.model.UserEntity;
import com.padimasso.autocasting.auth.repository.UserRepository;
import com.padimasso.autocasting.auth.service.AuthService;
import com.padimasso.autocasting.auth.service.EmailService;
import com.padimasso.autocasting.auth.service.JwtService;
import com.padimasso.autocasting.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final AppProperties appProperties;

    @Override
    public AuthResponse register(RegisterRequest request) {
        boolean exists = userRepository.existsByEmail(request.email());

        if (exists) {
            var userExistsErr = "auth.user_exists";
            throw new IllegalArgumentException(userExistsErr + "|" + request.email());
        }

        var user = new UserEntity();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());

        userRepository.save(user);

        String jwt = jwtService.generateToken(user);
        return new AuthResponse(jwt);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new IllegalArgumentException("auth.invalid_credentials|" + request.email()));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("auth.invalid_credentials");
        }

        String jwt = jwtService.generateToken(user);
        return new AuthResponse(jwt);
    }

    @Override
    public ForgotPasswordResponse sendResetPasswordEmail(ForgotPasswordRequest request) {
        var user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new IllegalArgumentException("auth.user_not_found|" + request.email()));

        String token = jwtService.generateToken(user); // Expira en 15 min

        String resetUrl = appProperties.getFrontendUrl() + "/reset-password?token=" + token;

        String htmlBody = """
            <h2>Recuperación de Contraseña</h2>
            <p>Hacé clic en el siguiente botón para cambiar tu contraseña:</p>
            <a href="%s" style="background-color:#4CAF50;color:white;padding:10px 20px;text-decoration:none;border-radius:5px;">Restablecer contraseña</a>
            <p>Este link expira en 15 minutos.</p>
            """.formatted(resetUrl);

        emailService.sendHtmlEmail(user.getEmail(), "Restablece tu contraseña", htmlBody);

        return new ForgotPasswordResponse(true, "Enviado");
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        String email = jwtService.extractEmail(request.token());

        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("auth.user_not_found|" + email));

        if (!jwtService.isTokenValid(request.token(), user)) {
            throw new IllegalArgumentException("auth.invalid_token");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

}

