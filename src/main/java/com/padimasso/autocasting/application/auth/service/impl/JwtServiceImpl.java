package com.padimasso.autocasting.application.auth.service.impl;

import com.padimasso.autocasting.application.auth.model.RoleEntity;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.padimasso.autocasting.config.AppConstants.ISSUER;
import static com.padimasso.autocasting.config.AppConstants.SECRET;

@Service
@SuppressWarnings("unused")
public class JwtServiceImpl implements JwtService {


    private Key getSignInKey() {
        byte[] keyBytes = SECRET.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateTokenWithCustomExpirationTime(
        UserEntity user,
        Long customExpirationTime,
        String talentProfileSlug,
        String employerProfileSlug
    ) {
        Map<String, Object> claims = new HashMap<>();

        var roleCodes = user.getRoles().stream()
            .map(RoleEntity::getCode)
            .toList();

        claims.put("roles", roleCodes);

        if (!roleCodes.isEmpty()) {
            claims.put("role", roleCodes.getFirst());
        }

        if (talentProfileSlug != null && !talentProfileSlug.isBlank()) {
            claims.put("talentProfileSlug", talentProfileSlug);
        }

        if (employerProfileSlug != null && !employerProfileSlug.isBlank()) {
            claims.put("employerProfileSlug", employerProfileSlug);
        }

        return Jwts.builder()
            .claims(claims)
            .subject(user.getEmail())
            .issuer(ISSUER)
            .issuedAt(new Date())
            .expiration(Date.from(Instant.now().plusSeconds(customExpirationTime)))
            .signWith(getSignInKey())
            .compact();
    }


    public String extractEmail(String token) {
        try {
            return getClaims(token).getPayload().getSubject();
        } catch (ExpiredJwtException ex) {
            throw new IllegalArgumentException("auth.token_expired");
        } catch (JwtException ex) {
            throw new IllegalArgumentException("auth.invalid_token");
        }
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return getClaims(token).getPayload().getExpiration().before(new Date());
    }

    private Jws<Claims> getClaims(String token) {
        return Jwts.parser()
            .verifyWith((SecretKey) getSignInKey())
            .build()
            .parseSignedClaims(token);
    }
}

