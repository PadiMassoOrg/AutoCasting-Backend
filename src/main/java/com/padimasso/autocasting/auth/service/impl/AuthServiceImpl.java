package com.padimasso.autocasting.auth.service.impl;

import com.padimasso.autocasting.auth.dto.request.LoginRequest;
import com.padimasso.autocasting.auth.dto.request.RegisterRequest;
import com.padimasso.autocasting.auth.dto.response.AuthResponse;
import com.padimasso.autocasting.auth.model.UserEntity;
import com.padimasso.autocasting.auth.repository.UserRepository;
import com.padimasso.autocasting.auth.service.AuthService;
import com.padimasso.autocasting.auth.service.JwtService;
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

    @Override
    public AuthResponse register(RegisterRequest request) {
        boolean exists = userRepository.existsByEmail(request.email());

        if (exists) {
            throw new IllegalArgumentException("auth.user_exists|" + request.email());
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
                .orElseThrow(() -> new IllegalArgumentException("auth.user_not_found|" + request.email()));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("auth.invalid_credentials");
        }

        String jwt = jwtService.generateToken(user);
        return new AuthResponse(jwt);
    }
}

