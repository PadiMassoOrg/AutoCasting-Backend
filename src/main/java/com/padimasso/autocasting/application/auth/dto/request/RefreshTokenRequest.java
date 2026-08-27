package com.padimasso.autocasting.application.auth.dto.request;

// No @NotBlank here: for the web Google OAuth2 flow, the refresh token arrives only as an
// HttpOnly cookie (never JS-readable, by design), so the request body legitimately may not
// carry a value — AuthController resolves body-or-cookie and validates the resolved value
// itself before calling into AuthService.
public record RefreshTokenRequest(String refreshToken) {
}
