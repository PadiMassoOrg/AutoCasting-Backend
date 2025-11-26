package com.padimasso.autocasting.application.auth.model;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
@SuppressWarnings("unused")
public class AuthenticatedOAuthUser implements OAuth2User {

    private final UserEntity user;
    private final Map<String, Object> attributes;

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public String getEmail() {
        return user.getEmail();
    }

    public RoleEntity getRole() {
        return user.getRoles().stream().findFirst().orElse(null);
    }

    public UserEntity getUserEntity() {
        return this.user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
            .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getCode()))
            .toList();
    }

    @Override
    public String getName() {
        return user.getEmail();
    }
}
