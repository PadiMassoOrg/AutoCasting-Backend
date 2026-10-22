package com.padimasso.autocasting.application.email.util;

import com.padimasso.autocasting.application.auth.model.OnboardingStatus;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.email.model.EmailAudience;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailAudienceEligibilityTest {

    private UserEntity user(OnboardingStatus talent, OnboardingStatus employer) {
        return UserEntity.builder()
            .talentOnboardingStatus(talent)
            .employerOnboardingStatus(employer)
            .build();
    }

    @Test
    void talentAudience_includesUsersThatStartedTalentOnboarding() {
        assertTrue(EmailAudienceEligibility.isEligible(EmailAudience.TALENT, user(OnboardingStatus.IN_PROGRESS, OnboardingStatus.NOT_STARTED)));
        assertTrue(EmailAudienceEligibility.isEligible(EmailAudience.TALENT, user(OnboardingStatus.COMPLETED, OnboardingStatus.NOT_STARTED)));
    }

    @Test
    void talentAudience_excludesEmployerOnlyUsers() {
        assertFalse(EmailAudienceEligibility.isEligible(EmailAudience.TALENT, user(OnboardingStatus.NOT_STARTED, OnboardingStatus.COMPLETED)));
    }

    @Test
    void employerAudience_includesUsersThatStartedEmployerOnboarding() {
        assertTrue(EmailAudienceEligibility.isEligible(EmailAudience.EMPLOYER, user(OnboardingStatus.NOT_STARTED, OnboardingStatus.IN_PROGRESS)));
    }

    @Test
    void employerAudience_excludesTalentOnlyUsers() {
        assertFalse(EmailAudienceEligibility.isEligible(EmailAudience.EMPLOYER, user(OnboardingStatus.COMPLETED, OnboardingStatus.NOT_STARTED)));
    }

    @Test
    void userThatIsBoth_isEligibleForEitherAudience() {
        UserEntity both = user(OnboardingStatus.COMPLETED, OnboardingStatus.COMPLETED);

        assertTrue(EmailAudienceEligibility.isEligible(EmailAudience.TALENT, both));
        assertTrue(EmailAudienceEligibility.isEligible(EmailAudience.EMPLOYER, both));
    }

    @Test
    void allAudience_includesUsersWithoutAnyOnboarding() {
        UserEntity none = user(OnboardingStatus.NOT_STARTED, OnboardingStatus.NOT_STARTED);

        assertTrue(EmailAudienceEligibility.isEligible(EmailAudience.ALL, none));
        assertFalse(EmailAudienceEligibility.isEligible(EmailAudience.TALENT, none));
        assertFalse(EmailAudienceEligibility.isEligible(EmailAudience.EMPLOYER, none));
    }
}
