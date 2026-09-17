package com.padimasso.autocasting.application.talent.service;

import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;

public interface TalentWelcomeEmailService {
    /**
     * Renders and sends the onboarding welcome email for the given talent profile.
     * Returns false (without throwing) when the profile is missing required data
     * (stage name, public slug or email) or the send itself fails, so callers can
     * decide how to handle a skipped/failed send without the caller's own flow breaking.
     */
    boolean sendWelcomeEmail(TalentProfileEntity profile);
}
