package com.padimasso.autocasting.application.email.service.impl;

import com.padimasso.autocasting.application.email.model.EmailType;
import com.padimasso.autocasting.application.email.repository.EmailDeliveryRepository;
import com.padimasso.autocasting.application.email.repository.projection.EmailLastSentProjection;
import com.padimasso.autocasting.application.email.service.EmailDeliveryService.LastDelivery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailDeliveryServiceImplTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDateTime SENT_AT = LocalDateTime.of(2026, 10, 1, 9, 30);

    @Mock
    private EmailDeliveryRepository emailDeliveryRepository;

    private EmailDeliveryServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new EmailDeliveryServiceImpl(emailDeliveryRepository);
    }

    private EmailLastSentProjection row(UUID userId, EmailType type, LocalDateTime lastSentAt) {
        return new EmailLastSentProjection() {
            public UUID getUserId() { return userId; }
            public EmailType getEmailType() { return type; }
            public LocalDateTime getLastSentAt() { return lastSentAt; }
        };
    }

    @Test
    void findLastDeliveries_groupsByUser() {
        when(emailDeliveryRepository.findLastSentAtPerTypeByUserIds(List.of(USER_ID)))
            .thenReturn(List.of(row(USER_ID, EmailType.TALENT_WELCOME, SENT_AT)));

        assertEquals(
            Map.of(USER_ID, List.of(new LastDelivery(EmailType.TALENT_WELCOME, SENT_AT))),
            service.findLastDeliveries(List.of(USER_ID))
        );
    }

    @Test
    void findLastSentAt_returnsTheDateOfTheRequestedType() {
        when(emailDeliveryRepository.findLastSentAtPerTypeByUserIds(List.of(USER_ID)))
            .thenReturn(List.of(row(USER_ID, EmailType.TALENT_WELCOME, SENT_AT)));

        assertEquals(Map.of(USER_ID, SENT_AT), service.findLastSentAt(List.of(USER_ID), EmailType.TALENT_WELCOME));
    }

    @Test
    void noUsers_skipsTheQuery() {
        assertEquals(Map.of(), service.findLastDeliveries(List.of()));
        verifyNoInteractions(emailDeliveryRepository);
    }
}
