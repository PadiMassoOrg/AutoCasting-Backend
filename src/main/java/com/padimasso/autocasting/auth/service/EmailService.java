package com.padimasso.autocasting.auth.service;

public interface EmailService {
    void sendHtmlEmail(String to, String subject, String htmlBody);
}
