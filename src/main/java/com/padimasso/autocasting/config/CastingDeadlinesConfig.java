package com.padimasso.autocasting.config;

import com.padimasso.autocasting.application.castings.util.CastingDeadlines;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;

// Applies the business timezone (AUTO_CLOSE_CASTINGS_ZONE) to every casting deadline check. The same
// property drives the auto-close cron zone in CastingDeadlineScheduler. An invalid zone fails startup.
@Slf4j
@Configuration
public class CastingDeadlinesConfig {

    public CastingDeadlinesConfig(
        @Value("${app.jobs.close-expired-castings.zone:" + CastingDeadlines.DEFAULT_ZONE_ID + "}") String zoneId
    ) {
        ZoneId zone = ZoneId.of(zoneId.trim());
        CastingDeadlines.configureZone(zone);
        log.info("Casting deadlines use timezone {}", zone);
    }
}
