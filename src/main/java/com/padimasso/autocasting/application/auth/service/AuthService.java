package com.padimasso.autocasting.application.auth.service;

import com.padimasso.autocasting.application.auth.dto.request.*;
import com.padimasso.autocasting.application.auth.dto.response.AuthResponse;
import com.padimasso.autocasting.application.auth.dto.response.ForgotPasswordResponse;
import com.padimasso.autocasting.application.auth.dto.response.MeResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse adminLogin(LoginRequest request);

    ForgotPasswordResponse sendResetPasswordEmail(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    void changePassword(ChangePasswordRequest request);

    MeResponse updateOnboarding(UserOnboardingRequest request);
}
