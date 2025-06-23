package com.padimasso.autocasting.auth.service;

import com.padimasso.autocasting.auth.model.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateToken(UserEntity user);

    String extractEmail(String jwt);

    boolean isTokenValid(String token, UserDetails userDetails);
}
