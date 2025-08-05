package com.padimasso.autocasting.application.auth.service;

import com.padimasso.autocasting.application.auth.model.RoleEntity;
import com.padimasso.autocasting.application.auth.model.UserAccountProvider;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.repository.RoleRepository;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.plan.model.PlanEntity;
import com.padimasso.autocasting.application.plan.repository.PlanRepository;
import com.padimasso.autocasting.application.profile.model.ProfileEntity;
import com.padimasso.autocasting.application.profile.repository.ProfileRepository;
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
