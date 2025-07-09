package com.padimasso.autocasting.auth.service;

import com.padimasso.autocasting.auth.service.impl.UnifiedOAuthUserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final UnifiedOAuthUserServiceImpl unifiedOAuthUserService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = new OidcUserService().loadUser(userRequest);

        String role = userRequest.getAdditionalParameters().get("role") != null
                ? userRequest.getAdditionalParameters().get("role").toString()
                : userRequest.getClientRegistration().getClientName(); // fallback

        return unifiedOAuthUserService.loadUser(userRequest, oidcUser, role);
    }
}
