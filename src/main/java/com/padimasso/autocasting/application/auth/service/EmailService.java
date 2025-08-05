package com.padimasso.autocasting.application.auth.service;

public interface EmailService {
    void sendHtmlEmail(String to, String subject, String htmlBody);
}
