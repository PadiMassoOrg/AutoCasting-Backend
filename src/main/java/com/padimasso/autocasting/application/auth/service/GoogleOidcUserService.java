package com.padimasso.autocasting.application.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
public class GoogleOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final UserProvisioningService provision;

    @Override
    public OidcUser loadUser(OidcUserRequest req) throws OAuth2AuthenticationException {
        try {
            OidcUser oidc = new OidcUserService().loadUser(req);
            String state = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest()
                .getParameter(OAuth2ParameterNames.STATE);

            // Find in STATE:
            String role = (state != null && state.contains(":")) ? state.substring(state.indexOf(':') + 1) : null;

            // Perform:
            provision.ensureUser(
                oidc.getAttribute("email"),
                role,
                oidc.getGivenName() + " " + oidc.getFamilyName());

            return oidc;
        } catch (OAuth2AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new OAuth2AuthenticationException("oauth.google.failure");
        }
    }
}
