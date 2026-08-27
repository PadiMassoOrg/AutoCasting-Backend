package com.padimasso.autocasting.application.auth.service;

import com.padimasso.autocasting.application.auth.model.UserEntity;

public interface RefreshTokenService {

    record RotationResult(UserEntity user, String newRefreshToken) {
    }

    String issue(UserEntity user);

    RotationResult rotate(String rawOldRefreshToken);

    void revoke(String rawRefreshToken);
}
