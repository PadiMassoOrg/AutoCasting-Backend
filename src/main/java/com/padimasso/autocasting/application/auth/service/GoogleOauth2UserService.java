package com.padimasso.autocasting.application.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserProvisioningService provision;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) throws OAuth2AuthenticationException {
        OAuth2User oauth = new DefaultOAuth2UserService().loadUser(req);

        provision.ensureUser(oauth.getAttribute("static/email"),
            req.getAdditionalParameters().get("role").toString(),
            oauth.getName());

        return oauth;
    }
}
