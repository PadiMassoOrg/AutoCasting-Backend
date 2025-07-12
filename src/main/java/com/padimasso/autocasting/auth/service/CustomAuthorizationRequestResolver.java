package com.padimasso.autocasting.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.util.UUID;


public class CustomAuthorizationRequestResolver
    implements OAuth2AuthorizationRequestResolver {

    private final DefaultOAuth2AuthorizationRequestResolver delegate;

    public CustomAuthorizationRequestResolver(ClientRegistrationRepository repo) {
        this.delegate = new DefaultOAuth2AuthorizationRequestResolver(
            repo, "/oauth2/authorization");
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        return customize(delegate.resolve(request), request);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request,
                                              String clientId) {
        return customize(delegate.resolve(request, clientId), request);
    }

    private OAuth2AuthorizationRequest customize(OAuth2AuthorizationRequest original,
                                                 HttpServletRequest req) {
        if (original == null) return null;

        String role = req.getParameter("role");
        String nonce = UUID.randomUUID().toString();

        OAuth2AuthorizationRequest.Builder builder =
            OAuth2AuthorizationRequest.from(original)
                .additionalParameters(p -> p.put("prompt", "select_account"));

        if (role != null && !role.isBlank()) {
            builder.state(nonce + ":" + role);               // <nonce>:<ACTOR>
        }
        return builder.build();
    }
}
