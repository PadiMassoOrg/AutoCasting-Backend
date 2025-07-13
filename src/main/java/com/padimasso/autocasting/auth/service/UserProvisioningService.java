package com.padimasso.autocasting.auth.service;

import com.padimasso.autocasting.auth.model.RoleEntity;
import com.padimasso.autocasting.auth.model.UserAccountProvider;
import com.padimasso.autocasting.auth.model.UserEntity;
import com.padimasso.autocasting.auth.repository.RoleRepository;
import com.padimasso.autocasting.auth.repository.UserRepository;
import com.padimasso.autocasting.model.PlanEntity;
import com.padimasso.autocasting.model.ProfileEntity;
import com.padimasso.autocasting.repository.PlanRepository;
import com.padimasso.autocasting.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class UserProvisioningService {

    public static final String PLAN_FREE = "FREE";
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PlanRepository planRepository;

    void ensureUser(String email, String roleCode, String name) {
        if (email == null) throw new OAuth2AuthenticationException("auth.user_not_found");
        if (roleCode == null) throw new OAuth2AuthenticationException("oauth.role_missing");

        final RoleEntity foundRole = roleRepository.findByCode(roleCode.toUpperCase())
            .orElseThrow(() -> new IllegalArgumentException("oauth.role_missing"));
        final PlanEntity freePlan = planRepository.findByCode(PLAN_FREE)
            .orElseThrow(() -> new IllegalStateException("auth.invalid_plan"));

        UserEntity user = userRepository.findByEmail(email).orElseGet(() -> {
            UserEntity newUser = UserEntity.builder()
                .email(email)
                .password(null)
                .userAccountProvider(UserAccountProvider.OTHER)
                .role(foundRole)
                .build();
            return userRepository.save(newUser);
        });

        boolean profileExists = profileRepository.existsByUserId(user.getId());
        if (!profileExists) {
            ProfileEntity profile = ProfileEntity.builder()
                .name(name)
                .user(user)
                .plan(freePlan)
                .build();
            profileRepository.save(profile);
        }
    }
}
