package com.padimasso.autocasting.application.castings.util;

import java.time.LocalDate;
import java.time.ZoneId;

// Single rule for casting deadlines: the deadline day is the last day to apply, so a casting stays
// open through its deadline and has passed once that day ends in Buenos Aires, regardless of the
// server's timezone. Used by the auto-close job, the admin emergency close, the apply check, the
// public catalog and status transitions.
public final class CastingDeadlines {

    public static final String ZONE_ID = "America/Argentina/Buenos_Aires";
    public static final ZoneId ZONE = ZoneId.of(ZONE_ID);

    private CastingDeadlines() {
    }

    public static LocalDate today() {
        return LocalDate.now(ZONE);
    }

    public static boolean isOpen(LocalDate deadline, LocalDate today) {
        return deadline != null && !deadline.isBefore(today);
    }

    public static boolean hasPassed(LocalDate deadline, LocalDate today) {
        return deadline != null && deadline.isBefore(today);
    }
}
