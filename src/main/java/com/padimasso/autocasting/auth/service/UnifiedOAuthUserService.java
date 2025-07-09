package com.padimasso.autocasting.auth.service;

import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

@SuppressWarnings("unused")
public interface UnifiedOAuthUserService {
    OAuth2User loadUser(String provider, Map<String, Object> attributes, Map<String, Object> additionalParameters);
}

