package com.padimasso.autocasting.auth.model;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
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
        return user.getRole();
    }

    public UserEntity getUserEntity() {
        return this.user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getCode()));
    }

    @Override
    public String getName() {
        return user.getEmail();
    }
}
