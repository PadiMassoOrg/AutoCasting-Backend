package com.padimasso.autocasting.auth.service;

import com.padimasso.autocasting.auth.model.RoleEntity;
import com.padimasso.autocasting.auth.model.UserAccountProvider;
import com.padimasso.autocasting.auth.model.UserEntity;
import com.padimasso.autocasting.auth.repository.RoleRepository;
import com.padimasso.autocasting.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class UserProvisioningService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepository;

    void ensureUser(String email, Object roleCode) {
        if (email == null) throw new OAuth2AuthenticationException("auth.user_not_found");
        if (roleCode == null) throw new OAuth2AuthenticationException("oauth.role_missing");

        final RoleEntity foundRole = roleRepository.findByCode(roleCode.toString().toUpperCase())
            .orElseThrow(() -> new IllegalArgumentException("oauth.role_missing"));

        userRepo.findByEmail(email).orElseGet(() ->
            userRepo.save(UserEntity.builder()
                .email(email)
                .password(null)          // login externo
                .userAccountProvider(UserAccountProvider.OTHER)
                .role(foundRole)
                .build()));
    }
}
