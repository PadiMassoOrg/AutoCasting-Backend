package com.padimasso.autocasting.application.email.repository.projection;

import com.padimasso.autocasting.application.email.model.EmailType;

import java.time.LocalDateTime;
import java.util.UUID;

public interface EmailLastSentProjection {
    UUID getUserId();

    EmailType getEmailType();

    LocalDateTime getLastSentAt();
}
