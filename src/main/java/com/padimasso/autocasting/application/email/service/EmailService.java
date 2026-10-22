package com.padimasso.autocasting.application.email.service;

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
    default void sendHtmlEmail(String to, String subject, String htmlBody, Map<String, Resource> inlineResources) {
        sendHtmlEmail(to, subject, htmlBody, inlineResources, Map.of());
    }

    /**
     * Same as above, also setting the given extra MIME headers (e.g. {@code List-Unsubscribe}).
     */
    void sendHtmlEmail(String to, String subject, String htmlBody, Map<String, Resource> inlineResources, Map<String, String> headers);
}
