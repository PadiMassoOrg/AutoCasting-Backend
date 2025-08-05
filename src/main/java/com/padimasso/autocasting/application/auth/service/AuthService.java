package com.padimasso.autocasting.application.auth.service;

import com.padimasso.autocasting.application.auth.dto.request.ForgotPasswordRequest;
import com.padimasso.autocasting.application.auth.dto.request.LoginRequest;
import com.padimasso.autocasting.application.auth.dto.request.RegisterRequest;
import com.padimasso.autocasting.application.auth.dto.request.ResetPasswordRequest;
import com.padimasso.autocasting.application.auth.dto.response.AuthResponse;
import com.padimasso.autocasting.application.auth.dto.response.ForgotPasswordResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    ForgotPasswordResponse sendResetPasswordEmail(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);
}
