package com.padimasso.autocasting.application.email.service.sender;

import com.padimasso.autocasting.application.email.model.EmailType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class BulkEmailSenderRegistry {

    private final Map<EmailType, BulkEmailSender> sendersByType;

    public BulkEmailSenderRegistry(List<BulkEmailSender> senders) {
        this.sendersByType = senders.stream()
            .collect(Collectors.toUnmodifiableMap(BulkEmailSender::type, Function.identity()));
    }

    public BulkEmailSender forType(EmailType emailType) {
        BulkEmailSender sender = sendersByType.get(emailType);
        if (sender == null) {
            throw new IllegalStateException("No bulk email sender for " + emailType);
        }
        return sender;
    }
}
