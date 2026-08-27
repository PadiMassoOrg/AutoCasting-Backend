package com.padimasso.autocasting.application.auth.controller;

import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.auth.dto.request.*;
import com.padimasso.autocasting.application.auth.dto.response.AuthResponse;
import com.padimasso.autocasting.application.auth.dto.response.ForgotPasswordResponse;
import com.padimasso.autocasting.application.auth.dto.response.MeResponse;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.service.AuthService;
import com.padimasso.autocasting.exception.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.padimasso.autocasting.config.AppConstants.*;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.AUTH_REFRESH_TOKEN_INVALID;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Registration, authentication, onboarding, and password recovery endpoints.")
@SuppressWarnings("unused")
public class AuthController {

    private final AuthService authService;
    private final AuthContext authContext;

    @Operation(
        summary = "Register user",
        description = "Registers a new user with email, password, and role."
    )
    @PostMapping(REGISTER_API_URL)
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(
        summary = "User login",
        description = "Authenticates a user with email and password and returns a JWT."
    )
    @PostMapping(LOGIN_API_URL)
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(
        summary = "Admin login",
        description = "Authenticates an admin user with email and password and returns a JWT."
    )
    @PostMapping(ADMIN_LOGIN_API_URL)
    public ResponseEntity<AuthResponse> adminLogin(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.adminLogin(request));
    }

    @Operation(
        summary = "Google Sign-In (mobile)",
        description = "Authenticates or registers a user using a Google-issued ID token from a native mobile Google Sign-In flow, and returns a JWT."
    )
    @PostMapping(GOOGLE_MOBILE_LOGIN_API_URL)
    public ResponseEntity<AuthResponse> googleMobileLogin(@Valid @RequestBody GoogleMobileLoginRequest request) {
        return ResponseEntity.ok(authService.loginOrRegisterWithGoogleMobile(request));
    }

    @Operation(
        summary = "Refresh access token",
        description = "Exchanges a valid, non-expired, non-revoked refresh token for a new access token and a rotated " +
            "refresh token. The token may be supplied in the request body (mobile/password-login web clients) or via " +
            "the 'refreshToken' HttpOnly cookie (web Google OAuth2 sessions, where the token is never exposed to JS)."
    )
    @PostMapping(REFRESH_API_URL)
    public ResponseEntity<AuthResponse> refresh(
        @RequestBody(required = false) RefreshTokenRequest request,
        @CookieValue(name = "refreshToken", required = false) String refreshTokenCookie,
        HttpServletResponse response
    ) {
        boolean cameFromCookie = request == null || request.refreshToken() == null || request.refreshToken().isBlank();
        String refreshToken = resolveRefreshToken(request, refreshTokenCookie);
        AuthResponse authResponse = authService.refresh(new RefreshTokenRequest(refreshToken));

        // If this session's refresh token arrived via the HttpOnly cookie (the web Google
        // OAuth2 flow), the rotated token must be re-delivered the same way — otherwise the
        // next refresh would have nothing to fall back to (the old cookie's token was just
        // revoked by rotation), and returning it only in the JSON body would downgrade this
        // session to a JS-readable cookie, defeating the point of HttpOnly delivery.
        if (cameFromCookie) {
            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", authResponse.refreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path(BASE_API_URL + "/auth")
                .maxAge(REFRESH_TOKEN_EXPIRATION_TIME)
                .build();
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        }

        return ResponseEntity.ok(authResponse);
    }

    @Operation(
        summary = "Logout",
        description = "Revokes the given refresh token (body or cookie, same resolution as refresh). Always succeeds " +
            "from the caller's perspective."
    )
    @PostMapping(LOGOUT_API_URL)
    public ResponseEntity<Void> logout(
        @RequestBody(required = false) RefreshTokenRequest request,
        @CookieValue(name = "refreshToken", required = false) String refreshTokenCookie
    ) {
        String refreshToken = resolveRefreshToken(request, refreshTokenCookie);
        authService.logout(new RefreshTokenRequest(refreshToken));
        return ResponseEntity.ok().build();
    }

    private String resolveRefreshToken(RefreshTokenRequest request, String refreshTokenCookie) {
        String fromBody = request != null ? request.refreshToken() : null;
        String resolved = (fromBody != null && !fromBody.isBlank()) ? fromBody : refreshTokenCookie;
        if (resolved == null || resolved.isBlank()) {
            throw ApiException.unauthorized(AUTH_REFRESH_TOKEN_INVALID);
        }
        return resolved;
    }

    @Operation(
        summary = "Get authenticated user",
        description = "Returns basic user information and onboarding status.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(ME_API_URL)
    public ResponseEntity<MeResponse> me() {
        UserEntity user = authContext.getCurrentUserOrThrow();
        return ResponseEntity.ok(MeResponse.from(user));
    }

    @Operation(
        summary = "Update onboarding state",
        description = "Updates active mode (TALENT/EMPLOYER) and onboarding statuses.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping(ONBOARDING_API_URL)
    public ResponseEntity<MeResponse> updateOnboarding(
        @Valid @RequestBody UserOnboardingRequest request
    ) {
        MeResponse response = authService.updateOnboarding(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Request password reset",
        description = "Sends a password reset email when the address is eligible."
    )
    @PostMapping(FORGOT_PASS_URL)
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.sendResetPasswordEmail(request)); // No se expone si el email existe o no
    }

    @Operation(
        summary = "Reset password",
        description = "Sets a new password using the reset token."
    )
    @PostMapping(RESET_PASS_URL)
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Change password",
        description = "Changes the password for the authenticated user.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(CHANGE_PASS_URL)
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok().build();
    }
}
