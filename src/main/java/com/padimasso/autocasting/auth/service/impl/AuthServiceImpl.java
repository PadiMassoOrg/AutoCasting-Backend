package com.padimasso.autocasting.auth.service.impl;

import com.padimasso.autocasting.auth.dto.request.LoginRequest;
import com.padimasso.autocasting.auth.dto.request.RegisterRequest;
import com.padimasso.autocasting.auth.dto.response.AuthResponse;
import com.padimasso.autocasting.auth.model.UserEntity;
import com.padimasso.autocasting.auth.repository.UserRepository;
import com.padimasso.autocasting.auth.service.AuthService;
import com.padimasso.autocasting.auth.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    public AuthResponse register(RegisterRequest request) {
        var user = new UserEntity();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role()); // ACTOR o CASTINERA

        userRepository.save(user);

        String jwt = jwtService.generateToken(user);
        return new AuthResponse(jwt);
    }

    public AuthResponse login(LoginRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        var user = userRepository.findByEmail(request.email())
                .orElseThrow();

        String jwt = jwtService.generateToken(user);
        return new AuthResponse(jwt);
    }
}

