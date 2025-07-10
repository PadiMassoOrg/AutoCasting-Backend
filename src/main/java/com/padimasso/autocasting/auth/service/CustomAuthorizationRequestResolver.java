package com.padimasso.autocasting.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@SuppressWarnings("unused")
public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final OAuth2AuthorizationRequestResolver defaultResolver;

    public CustomAuthorizationRequestResolver(ClientRegistrationRepository repo) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(repo, "/oauth2/authorization");
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest original = defaultResolver.resolve(request);
        return customizeRequest(request, original);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientId) {
        OAuth2AuthorizationRequest original = defaultResolver.resolve(request, clientId);
        return customizeRequest(request, original);
    }

    private OAuth2AuthorizationRequest customizeRequest(HttpServletRequest request, OAuth2AuthorizationRequest original) {
        if (original == null) return null;

        String role = request.getParameter("role");
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("oauth.role_missing");
        }

        Map<String, Object> additionalParams = new HashMap<>(original.getAdditionalParameters());
        additionalParams.put("role", role);

        return OAuth2AuthorizationRequest.from(original)
            .additionalParameters(additionalParams)
            .additionalParameters(params -> params.put("prompt", "select_account"))
            .state(role) // usamos `state` como transporte
            .build();
    }
}
