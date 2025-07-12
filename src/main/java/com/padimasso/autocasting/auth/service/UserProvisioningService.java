package com.padimasso.autocasting.auth.service;

import com.padimasso.autocasting.auth.model.Role;
import com.padimasso.autocasting.auth.model.UserAccountProvider;
import com.padimasso.autocasting.auth.model.UserEntity;
import com.padimasso.autocasting.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class UserProvisioningService {

    private final UserRepository userRepo;

    void ensureUser(String email, Object roleObj) {

        if (email == null) throw new OAuth2AuthenticationException("auth.user_not_found");
        if (roleObj == null) throw new OAuth2AuthenticationException("oauth.role_missing");

        Role role;
        try { role = Role.valueOf(roleObj.toString().toUpperCase()); }
        catch (IllegalArgumentException ex) {
            throw new OAuth2AuthenticationException("Invalid role: " + roleObj);
        }

        userRepo.findByEmail(email).orElseGet(() ->
            userRepo.save(UserEntity.builder()
                .email(email)
                .role(role)
                .password(null)          // login externo
                .userAccountProvider(UserAccountProvider.OTHER)
                .build()));
    }
}
