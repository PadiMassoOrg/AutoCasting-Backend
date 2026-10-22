package com.padimasso.autocasting.application.email.util;

import com.padimasso.autocasting.application.auth.model.OnboardingStatus;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.email.model.EmailAudience;

/**
 * Every user holds both the TALENT and EMPLOYER roles and profiles, so a user counts as a talent or an
 * employer once they started that side's onboarding. A user can be both.
 */
public final class EmailAudienceEligibility {

    private EmailAudienceEligibility() {
        throw new IllegalStateException("Utility class");
    }

    private static boolean isTalent(UserEntity user) {
        return user.getTalentOnboardingStatus() != null && user.getTalentOnboardingStatus() != OnboardingStatus.NOT_STARTED;
    }

    private static boolean isEmployer(UserEntity user) {
        return user.getEmployerOnboardingStatus() != null && user.getEmployerOnboardingStatus() != OnboardingStatus.NOT_STARTED;
    }

    public static boolean isEligible(EmailAudience audience, UserEntity user) {
        return switch (audience) {
            case TALENT -> isTalent(user);
            case EMPLOYER -> isEmployer(user);
            case ALL -> true;
        };
    }
}
