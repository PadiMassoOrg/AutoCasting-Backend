package com.padimasso.autocasting.application.talent.service;

import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;

public interface TalentPhotoRemovedEmailService {
    /**
     * Renders and sends the "photo removed" email (with tips for an approved photo) to the talent.
     * Returns false (without throwing) when the profile is missing its stage name or email, or the
     * send itself fails, so an admin moderation action is never rolled back by a mail problem.
     */
    boolean sendPhotoRemovedEmail(TalentProfileEntity profile);
}
