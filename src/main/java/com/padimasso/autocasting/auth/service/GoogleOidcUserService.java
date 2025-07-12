package com.padimasso.autocasting.auth.service;

import jakarta.servlet.http.HttpServletRequest;
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
public class GoogleOidcUserService
    implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final UserProvisioningService provision;

    @Override
    public OidcUser loadUser(OidcUserRequest req) throws OAuth2AuthenticationException {

        OidcUser oidc = new OidcUserService().loadUser(req);
        String email = oidc.getAttribute("email");

        // El 'state' viene en el request actual como query-param
        HttpServletRequest servletReq =
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String state = servletReq.getParameter(OAuth2ParameterNames.STATE);   // uuid:ACTOR

        String role = (state != null && state.contains(":")) ?
            state.substring(state.indexOf(':') + 1) : null;

        provision.ensureUser(email, role);
        return oidc;
    }
}
