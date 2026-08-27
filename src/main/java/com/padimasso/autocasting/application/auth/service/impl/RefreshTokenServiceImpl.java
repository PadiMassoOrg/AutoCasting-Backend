package com.padimasso.autocasting.application.auth.service.impl;

import com.padimasso.autocasting.application.auth.model.RefreshTokenEntity;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.repository.RefreshTokenRepository;
import com.padimasso.autocasting.application.auth.service.RefreshTokenService;
import com.padimasso.autocasting.config.AppConstants;
import com.padimasso.autocasting.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import static com.padimasso.autocasting.exception.ErrorMessageKeys.*;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int TOKEN_BYTE_LENGTH = 32;

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public String issue(UserEntity user) {
        String rawToken = generateRawToken();
        persist(user, rawToken);
        return rawToken;
    }

    @Override
    @Transactional
    public RotationResult rotate(String rawOldRefreshToken) {
        String oldHash = hash(rawOldRefreshToken);

        RefreshTokenEntity existing = refreshTokenRepository.findWithLockByTokenHash(oldHash)
            .orElseThrow(() -> ApiException.unauthorized(AUTH_REFRESH_TOKEN_INVALID));

        if (existing.getRevokedAt() != null) {
            throw ApiException.unauthorized(AUTH_REFRESH_TOKEN_REVOKED);
        }

        if (existing.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw ApiException.unauthorized(AUTH_REFRESH_TOKEN_EXPIRED);
        }

        String newRawToken = generateRawToken();
        RefreshTokenEntity newEntity = persist(existing.getUser(), newRawToken);

        existing.setRevokedAt(LocalDateTime.now());
        existing.setReplacedById(newEntity.getId());
        refreshTokenRepository.save(existing);

        return new RotationResult(existing.getUser(), newRawToken);
    }

    @Override
    @Transactional
    public void revoke(String rawRefreshToken) {
        String tokenHash = hash(rawRefreshToken);
        refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(entity -> {
            if (entity.getRevokedAt() == null) {
                entity.setRevokedAt(LocalDateTime.now());
                refreshTokenRepository.save(entity);
            }
        });
    }

    private RefreshTokenEntity persist(UserEntity user, String rawToken) {
        RefreshTokenEntity entity = RefreshTokenEntity.builder()
            .user(user)
            .tokenHash(hash(rawToken))
            .issuedAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusSeconds(AppConstants.REFRESH_TOKEN_EXPIRATION_TIME))
            .build();
        return refreshTokenRepository.save(entity);
    }

    private static String generateRawToken() {
        byte[] bytes = new byte[TOKEN_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
