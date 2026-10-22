package com.padimasso.autocasting.application.email.service.impl;

import com.padimasso.autocasting.application.email.model.EmailDeliveryEntity;
import com.padimasso.autocasting.application.email.model.EmailType;
import com.padimasso.autocasting.application.email.repository.EmailDeliveryRepository;
import com.padimasso.autocasting.application.email.repository.projection.EmailLastSentProjection;
import com.padimasso.autocasting.application.email.service.EmailDeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmailDeliveryServiceImpl implements EmailDeliveryService {

    private final EmailDeliveryRepository emailDeliveryRepository;

    @Override
    public void recordAll(Collection<UUID> userIds, EmailType emailType) {
        if (userIds.isEmpty()) return;

        LocalDateTime sentAt = LocalDateTime.now();
        var deliveries = userIds.stream()
            .map(userId -> EmailDeliveryEntity.builder()
                .userId(userId)
                .emailType(emailType)
                .sentAt(sentAt)
                .build())
            .toList();
        emailDeliveryRepository.saveAll(deliveries);
    }

    @Override
    public Map<UUID, List<LastDelivery>> findLastDeliveries(Collection<UUID> userIds) {
        if (userIds.isEmpty()) return Map.of();

        return emailDeliveryRepository.findLastSentAtPerTypeByUserIds(userIds).stream()
            .sorted(Comparator.comparing(EmailLastSentProjection::getLastSentAt).reversed())
            .collect(Collectors.groupingBy(
                EmailLastSentProjection::getUserId,
                Collectors.mapping(row -> new LastDelivery(row.getEmailType(), row.getLastSentAt()), Collectors.toList())
            ));
    }

    @Override
    public Map<UUID, LocalDateTime> findLastSentAt(Collection<UUID> userIds, EmailType emailType) {
        Map<UUID, LocalDateTime> lastSentAt = new HashMap<>();
        findLastDeliveries(userIds).forEach((userId, deliveries) -> deliveries.stream()
            .filter(delivery -> delivery.type() == emailType)
            .findFirst()
            .ifPresent(delivery -> lastSentAt.put(userId, delivery.lastSentAt())));
        return lastSentAt;
    }
}
