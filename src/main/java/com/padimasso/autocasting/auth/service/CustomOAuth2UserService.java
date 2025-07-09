package com.padimasso.autocasting.auth.service;

import com.padimasso.autocasting.auth.service.impl.UnifiedOAuthUserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UnifiedOAuthUserServiceImpl unifiedOAuthUserService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = new DefaultOAuth2UserService().loadUser(userRequest);

        String role = userRequest.getAdditionalParameters().get("role") != null
                ? userRequest.getAdditionalParameters().get("role").toString()
                : userRequest.getClientRegistration().getClientName(); // fallback

        return unifiedOAuthUserService.loadUser(userRequest, user, role);
    }
}



