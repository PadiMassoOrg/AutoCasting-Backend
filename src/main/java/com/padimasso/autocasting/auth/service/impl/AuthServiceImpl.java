package com.padimasso.autocasting.auth.service.impl;

import com.padimasso.autocasting.auth.dto.request.ForgotPasswordRequest;
import com.padimasso.autocasting.auth.dto.request.LoginRequest;
import com.padimasso.autocasting.auth.dto.request.RegisterRequest;
import com.padimasso.autocasting.auth.dto.request.ResetPasswordRequest;
import com.padimasso.autocasting.auth.dto.response.AuthResponse;
import com.padimasso.autocasting.auth.dto.response.ForgotPasswordResponse;
import com.padimasso.autocasting.auth.model.RoleEntity;
import com.padimasso.autocasting.auth.model.UserAccountProvider;
import com.padimasso.autocasting.auth.model.UserEntity;
import com.padimasso.autocasting.auth.repository.RoleRepository;
import com.padimasso.autocasting.auth.repository.UserRepository;
import com.padimasso.autocasting.auth.service.AuthService;
import com.padimasso.autocasting.auth.service.EmailService;
import com.padimasso.autocasting.auth.service.JwtService;
import com.padimasso.autocasting.config.AppConstants;
import com.padimasso.autocasting.config.AppProperties;
import com.padimasso.autocasting.model.PlanEntity;
import com.padimasso.autocasting.model.ProfileEntity;
import com.padimasso.autocasting.repository.PlanRepository;
import com.padimasso.autocasting.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PlanRepository planRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final AppProperties appProperties;

    @Override
    public AuthResponse register(RegisterRequest request) {
        boolean exists = userRepository.existsByEmail(request.email());
        if (exists) {
            throw new IllegalArgumentException("auth.user_exists");
        }

        final RoleEntity foundRole = roleRepository.findByCode(request.role())
            .orElseThrow(() -> new IllegalArgumentException("auth.invalid_role"));
        final PlanEntity freePlan = planRepository.findByCode("FREE")
            .orElseThrow(() -> new IllegalStateException("auth.invalid_plan"));

        UserEntity user = UserEntity.builder()
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .userAccountProvider(UserAccountProvider.LOCAL)
            .role(foundRole)
            .build();
        userRepository.save(user);

        var profile = ProfileEntity.builder()
            .name(request.name())
            .user(user)
            .plan(freePlan)
            .build();
        profileRepository.save(profile);

        String jwt = jwtService.generateTokenWithCustomExpirationTime(user, AppConstants.EXPIRATION_TIME);
        return new AuthResponse(jwt);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new IllegalArgumentException("auth.invalid_credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("auth.invalid_credentials");
        }

        String jwt = jwtService.generateTokenWithCustomExpirationTime(user, AppConstants.EXPIRATION_TIME);
        return new AuthResponse(jwt);
    }

    @Override
    public ForgotPasswordResponse sendResetPasswordEmail(ForgotPasswordRequest request) {
        var user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new IllegalArgumentException("auth.user_not_found"));

        if (user.getUserAccountProvider() != UserAccountProvider.LOCAL) {
            throw new IllegalArgumentException("auth.password_reset_external");
        }

        String token = jwtService.generateTokenWithCustomExpirationTime(user, AppConstants.RESET_PASSWORD_EXPIRATION_TIME); // Expires in 15min
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

