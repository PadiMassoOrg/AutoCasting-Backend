package com.padimasso.autocasting.application.email.service;

import com.padimasso.autocasting.application.email.model.EmailType;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface EmailDeliveryService {
    record LastDelivery(EmailType type, LocalDateTime lastSentAt) {
    }

    void recordAll(Collection<UUID> userIds, EmailType emailType);

    /**
     * Returns, for each given user that ever received a tracked email, the date of the last delivery of
     * each email type, most recent first. Users that never received one are absent from the map.
     */
    Map<UUID, List<LastDelivery>> findLastDeliveries(Collection<UUID> userIds);

    /**
     * Same as above, narrowed to one email type: the date of its last delivery per user.
     */
    Map<UUID, LocalDateTime> findLastSentAt(Collection<UUID> userIds, EmailType emailType);
}
