package com.padimasso.autocasting.application.email.service;

public interface PasswordResetEmailService {
    /**
     * Renders and sends the reset password email with a link carrying the given reset token.
     * Send failures propagate as {@code ApiException} so the forgot-password request fails visibly.
     */
    void sendResetPasswordEmail(String to, String resetToken);
}
