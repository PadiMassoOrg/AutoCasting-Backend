package com.padimasso.autocasting.application.auth.service;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateTokenWithCustomExpirationTime(UserEntity user, Long expirationTime);

    String extractEmail(String jwt);

    boolean isTokenValid(String token, UserDetails userDetails);
}
