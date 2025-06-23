package com.padimasso.autocasting.auth.service.impl;

import com.padimasso.autocasting.auth.model.UserEntity;
import com.padimasso.autocasting.auth.service.JwtService;
import io.jsonwebtoken.Claims;
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

@Service
@SuppressWarnings("unused")
public class JwtServiceImpl implements JwtService {

    private static final String SECRET = "mysupersecurekeyforhs256mustbe32chars";

    private Key getSignInKey() {
        byte[] keyBytes = SECRET.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserEntity user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());

        return Jwts.builder()
                .claims(claims)
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(86400)))
                .signWith(getSignInKey())
                .compact();
    }

    public String extractEmail(String token) {
        return getClaims(token).getPayload().getSubject();
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

