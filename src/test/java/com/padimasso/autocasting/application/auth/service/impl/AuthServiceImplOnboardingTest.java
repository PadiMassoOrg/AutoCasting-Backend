package com.padimasso.autocasting.application.auth.service.impl;

import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.auth.dto.request.UserOnboardingRequest;
import com.padimasso.autocasting.application.auth.event.EmployerOnboardingCompletedEvent;
import com.padimasso.autocasting.application.auth.model.OnboardingStatus;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.model.UserMode;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import com.padimasso.autocasting.application.talent.service.TalentWelcomeEmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplOnboardingTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthContext authContext;
    @Mock
    private TalentProfileRepository talentProfileRepository;
    @Mock
    private TalentWelcomeEmailService talentWelcomeEmailService;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @InjectMocks
    private AuthServiceImpl service;

    private UserEntity user(OnboardingStatus employerOnboardingStatus) {
        UserEntity user = UserEntity.builder()
            .id(UUID.randomUUID())
            .talentOnboardingStatus(OnboardingStatus.NOT_STARTED)
            .employerOnboardingStatus(employerOnboardingStatus)
            .build();
        when(authContext.getCurrentUserOrThrow()).thenReturn(user);
        return user;
    }

    private UserOnboardingRequest employerRequest(OnboardingStatus employerOnboardingStatus) {
        return new UserOnboardingRequest(UserMode.EMPLOYER, OnboardingStatus.NOT_STARTED, employerOnboardingStatus);
    }

    @Test
    void updateOnboarding_employerJustCompleted_publishesEvent() {
        UserEntity user = user(OnboardingStatus.IN_PROGRESS);

        service.updateOnboarding(employerRequest(OnboardingStatus.COMPLETED));

        ArgumentCaptor<EmployerOnboardingCompletedEvent> event = ArgumentCaptor.forClass(EmployerOnboardingCompletedEvent.class);
        verify(eventPublisher).publishEvent(event.capture());
        assertSame(user, event.getValue().user());
    }

    @Test
    void updateOnboarding_employerAlreadyCompleted_doesNotPublishEvent() {
        user(OnboardingStatus.COMPLETED);

        service.updateOnboarding(employerRequest(OnboardingStatus.COMPLETED));

        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void updateOnboarding_employerStillInProgress_doesNotPublishEvent() {
        user(OnboardingStatus.NOT_STARTED);

        service.updateOnboarding(employerRequest(OnboardingStatus.IN_PROGRESS));

        verify(eventPublisher, never()).publishEvent(any());
    }
}
