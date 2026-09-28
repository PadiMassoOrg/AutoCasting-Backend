package com.padimasso.autocasting.application.castings.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;

// Single rule for casting deadlines: the deadline day is the last day to apply, so a casting stays
// open through its deadline and has passed once that day ends in the business timezone
// (AUTO_CLOSE_CASTINGS_ZONE, default Buenos Aires), regardless of the server's timezone. Used by the
// auto-close job, the admin emergency close, the apply check, the public catalog and status transitions.
public final class CastingDeadlines {

    public static final String DEFAULT_ZONE_ID = "America/Argentina/Buenos_Aires";

    private static volatile ZoneId zone = ZoneId.of(DEFAULT_ZONE_ID);

    private CastingDeadlines() {
    }

    // Set once at startup from configuration (CastingDeadlinesConfig), before any job or request runs.
    public static void configureZone(ZoneId configuredZone) {
        zone = Objects.requireNonNull(configuredZone);
    }

    public static ZoneId zone() {
        return zone;
    }

    public static LocalDate today() {
        return LocalDate.now(zone);
    }

    public static boolean isOpen(LocalDate deadline, LocalDate today) {
        return deadline != null && !deadline.isBefore(today);
    }

    public static boolean hasPassed(LocalDate deadline, LocalDate today) {
        return deadline != null && deadline.isBefore(today);
    }
}
