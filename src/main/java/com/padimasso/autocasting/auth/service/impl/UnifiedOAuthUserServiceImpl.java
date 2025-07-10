package com.padimasso.autocasting.auth.service.impl;

import com.padimasso.autocasting.auth.model.Role;
import com.padimasso.autocasting.auth.model.UserEntity;
import com.padimasso.autocasting.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class UnifiedOAuthUserServiceImpl {

    private final UserRepository userRepository;

    public OAuth2User loadUser(OAuth2UserRequest userRequest, OAuth2User user, String role) {
        String email = user.getAttribute("email");
        if (email == null || role == null) {
            throw new RuntimeException("Missing email or role from OAuth2 provider");
        }

        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            UserEntity newUser = new UserEntity();
            newUser.setEmail(email);
            newUser.setRole(Role.valueOf(role.toUpperCase()));
            newUser.setPassword(null); // password puede ser null si es OAuth
            userRepository.save(newUser);
        }

        return user;
    }

    public OidcUser loadUser(OidcUserRequest userRequest, OidcUser user, String role) {
        return (OidcUser) loadUser((OAuth2UserRequest) userRequest, user, role);
    }
}


