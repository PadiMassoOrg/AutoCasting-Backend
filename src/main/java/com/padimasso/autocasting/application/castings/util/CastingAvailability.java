package com.padimasso.autocasting.application.castings.util;

import java.time.LocalDate;

import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_PUBLISHED;

// Whether a casting is currently offered to talents: published and still within its deadline.
// Governs the public casting/role detail pages and applications. The public catalog query
// (CastingRoleSpecs.fromFilter) applies the same condition in SQL — keep both in sync.
public final class CastingAvailability {

    private CastingAvailability() {
    }

    public static boolean isOpenToTalents(String castingStatusCode, LocalDate deadline, LocalDate today) {
        return CASTING_STATUS_PUBLISHED.equals(castingStatusCode) && CastingDeadlines.isOpen(deadline, today);
    }
}
