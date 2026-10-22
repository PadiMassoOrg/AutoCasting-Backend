package com.padimasso.autocasting.application.email.service.impl;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.shared.util.SecureTokenGenerator;
import com.padimasso.autocasting.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailPreferencesServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private SecureTokenGenerator secureTokenGenerator;

    private EmailPreferencesServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new EmailPreferencesServiceImpl(userRepository, secureTokenGenerator);
    }

    @Test
    void unsubscribe_knownToken_marksUserUnsubscribed() {
        when(userRepository.existsByEmailUnsubscribeToken("token")).thenReturn(true);

        service.unsubscribe("token");

        verify(userRepository).markMarketingEmailsUnsubscribed(eq("token"), any(LocalDateTime.class));
    }

    @Test
    void unsubscribe_unknownToken_throwsNotFound() {
        when(userRepository.existsByEmailUnsubscribeToken("nope")).thenReturn(false);

        assertThrows(ApiException.class, () -> service.unsubscribe("nope"));
        verify(userRepository, never()).markMarketingEmailsUnsubscribed(anyString(), any());
    }

    @Test
    void unsubscribe_blankToken_throwsWithoutQuerying() {
        assertThrows(ApiException.class, () -> service.unsubscribe(" "));
        verifyNoInteractions(userRepository);
    }

    @Test
    void ensureUnsubscribeToken_existingToken_isReused() {
        UserEntity user = UserEntity.builder().id(UUID.randomUUID()).emailUnsubscribeToken("existing").build();

        assertEquals("existing", service.ensureUnsubscribeToken(user));
        verifyNoInteractions(secureTokenGenerator, userRepository);
    }

    @Test
    void ensureUnsubscribeToken_missingToken_generatesAndStoresIt() {
        UserEntity user = UserEntity.builder().id(UUID.randomUUID()).build();
        when(secureTokenGenerator.generate()).thenReturn("generated");
        when(userRepository.assignEmailUnsubscribeTokenIfMissing(user.getId(), "generated")).thenReturn(1);

        assertEquals("generated", service.ensureUnsubscribeToken(user));
        assertEquals("generated", user.getEmailUnsubscribeToken());
    }

    @Test
    void ensureUnsubscribeToken_tokenStoredConcurrently_returnsTheStoredOne() {
        UserEntity user = UserEntity.builder().id(UUID.randomUUID()).build();
        when(secureTokenGenerator.generate()).thenReturn("generated");
        when(userRepository.assignEmailUnsubscribeTokenIfMissing(user.getId(), "generated")).thenReturn(0);
        when(userRepository.findEmailUnsubscribeTokenById(user.getId())).thenReturn(Optional.of("stored"));

        assertEquals("stored", service.ensureUnsubscribeToken(user));
        assertEquals("stored", user.getEmailUnsubscribeToken());
    }
}
