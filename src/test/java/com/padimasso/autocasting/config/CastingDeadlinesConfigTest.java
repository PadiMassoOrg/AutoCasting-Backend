package com.padimasso.autocasting.config;

import com.padimasso.autocasting.application.castings.util.CastingDeadlines;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.DateTimeException;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CastingDeadlinesConfigTest {

    @AfterEach
    void restoreDefaultZone() {
        CastingDeadlines.configureZone(ZoneId.of(CastingDeadlines.DEFAULT_ZONE_ID));
    }

    @Test
    void configuredZone_isAppliedToCastingDeadlines() {
        new CastingDeadlinesConfig(" America/Mexico_City ");

        assertEquals(ZoneId.of("America/Mexico_City"), CastingDeadlines.zone());
    }

    @Test
    void invalidZone_failsStartup() {
        assertThrows(DateTimeException.class, () -> new CastingDeadlinesConfig("Not/AZone"));
    }
}
