package com.padimasso.autocasting.application.castings.service;

import java.util.UUID;

public interface CastingMediaCleanupService {
    /**
     * Deletes every object under a closed casting's Supabase Storage folder
     * ({@code employer/{employerProfileId}/castings/{castingId}/}) — role reference photos and
     * anything else ever uploaded there. Best-effort; never throws, so a Supabase-side failure
     * never blocks the casting from closing.
     */
    void deleteCastingFolder(UUID employerProfileId, UUID castingId);
}
