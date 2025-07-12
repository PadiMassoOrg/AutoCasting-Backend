package com.padimasso.autocasting.auth.service;

import com.padimasso.autocasting.auth.dto.request.ForgotPasswordRequest;
import com.padimasso.autocasting.auth.dto.request.LoginRequest;
import com.padimasso.autocasting.auth.dto.request.RegisterRequest;
import com.padimasso.autocasting.auth.dto.request.ResetPasswordRequest;
import com.padimasso.autocasting.auth.dto.response.AuthResponse;
import com.padimasso.autocasting.auth.dto.response.ForgotPasswordResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    ForgotPasswordResponse sendResetPasswordEmail(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);
}
