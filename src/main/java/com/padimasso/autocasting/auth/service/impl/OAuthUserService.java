package com.padimasso.autocasting.auth.service.impl;

import com.padimasso.autocasting.auth.model.AuthenticatedOAuthUser;
import com.padimasso.autocasting.auth.model.Role;
import com.padimasso.autocasting.auth.model.UserEntity;
import com.padimasso.autocasting.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuthUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauthUser = new DefaultOAuth2UserService().loadUser(userRequest);

        // String provider = userRequest.getClientRegistration().getRegistrationId(); // "google", etc.
        String email = oauthUser.getAttribute("email");

        // Obtener "state" pasado desde frontend
        Map<String, Object> additionalParams = userRequest.getAdditionalParameters();
        String stateParam = (String) additionalParams.get("state"); // "ACTOR" o "CASTINERA"
        Role role = Role.valueOf(stateParam.toUpperCase());

        UserEntity user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(
                        UserEntity.builder()
                                .email(email)
                                .password(null) // OAuth user: no password
                                .role(role)
                                .build()
                ));

        return new AuthenticatedOAuthUser(user, oauthUser.getAttributes());
    }
}
