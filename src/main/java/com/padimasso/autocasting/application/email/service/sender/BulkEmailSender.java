package com.padimasso.autocasting.application.email.service.sender;

import com.padimasso.autocasting.application.email.model.EmailType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Renders and sends one {@link EmailType} to already-eligible recipients. Adding a bulk email means
 * adding an {@link EmailType} value and a sender for it.
 */
public interface BulkEmailSender {
    EmailType type();

    /**
     * Sends the email to each recipient without throwing, and returns the failure reason (an error
     * message key) of every recipient that was not sent, keyed by user id.
     */
    Map<UUID, String> send(List<BulkEmailRecipient> recipients);
}
