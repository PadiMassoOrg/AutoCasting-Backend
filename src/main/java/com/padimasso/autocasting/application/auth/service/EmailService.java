package com.padimasso.autocasting.application.auth.service;

import org.springframework.core.io.Resource;

import java.util.Map;

public interface EmailService {
    default void sendHtmlEmail(String to, String subject, String htmlBody) {
        sendHtmlEmail(to, subject, htmlBody, Map.of());
    }

    /**
     * Sends an HTML email. Each {@code inlineResources} entry is attached inline and can be
     * referenced from the body as {@code cid:<key>}, next to the always-attached logo and icons.
     */
    void sendHtmlEmail(String to, String subject, String htmlBody, Map<String, Resource> inlineResources);
}
