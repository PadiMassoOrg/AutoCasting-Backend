package com.padimasso.autocasting.auth.dto.request;

import com.padimasso.autocasting.auth.model.Role;

public record RegisterRequest(String email, String password, Role role) {
}

