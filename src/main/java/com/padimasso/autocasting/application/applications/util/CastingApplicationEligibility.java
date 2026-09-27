package com.padimasso.autocasting.application.applications.util;

import com.padimasso.autocasting.application.castings.util.CastingAvailability;

import java.time.LocalDate;
import java.util.Optional;

import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_ARCHIVED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_CLOSED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_PUBLISHED;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.CASTINGS_DEADLINE_PASSED;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.CASTINGS_NOT_FOUND;

// Whether a talent may apply to a casting, based on its status and deadline. The deadline is checked
// even for published castings, so applications stay blocked if the auto-close job has not run yet.
public final class CastingApplicationEligibility {

    private CastingApplicationEligibility() {
    }

    public static Optional<String> rejectionReason(String castingStatusCode, LocalDate deadline, LocalDate today) {
        if (CASTING_STATUS_CLOSED.equals(castingStatusCode) || CASTING_STATUS_ARCHIVED.equals(castingStatusCode)) {
            return Optional.of(CASTINGS_DEADLINE_PASSED);
        }
        if (!CASTING_STATUS_PUBLISHED.equals(castingStatusCode)) {
            return Optional.of(CASTINGS_NOT_FOUND);
        }
        if (!CastingAvailability.isOpenToTalents(castingStatusCode, deadline, today)) {
            return Optional.of(CASTINGS_DEADLINE_PASSED);
        }
        return Optional.empty();
    }
}
