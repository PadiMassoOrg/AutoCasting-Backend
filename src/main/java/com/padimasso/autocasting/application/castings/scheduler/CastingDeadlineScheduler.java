package com.padimasso.autocasting.application.castings.scheduler;

import com.padimasso.autocasting.application.castings.service.internal.CastingAutoCloseService;
import com.padimasso.autocasting.application.castings.util.CastingDeadlines;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
    value = "app.jobs.close-expired-castings.enabled",
    havingValue = "true",
    matchIfMissing = true
)
public class CastingDeadlineScheduler {

    private final CastingAutoCloseService castingAutoCloseService;

    @Scheduled(
        cron = "${app.jobs.close-expired-castings.cron:0 0 0 * * *}",
        zone = "${app.jobs.close-expired-castings.zone:" + CastingDeadlines.DEFAULT_ZONE_ID + "}"
    )
    public void closeExpiredCastings() {
        run("scheduled");
    }

    // Spring does not replay scheduled runs missed while the backend was down, so catch up on start.
    @EventListener(ApplicationReadyEvent.class)
    public void closeExpiredCastingsOnStartup() {
        run("startup");
    }

    private void run(String trigger) {
        LocalDate today = CastingDeadlines.today();

        try {
            int closedCount = castingAutoCloseService.closeExpiredCastings(today);
            log.info("Auto-close job executed. trigger={}, date={}, closedCastings={}", trigger, today, closedCount);
        } catch (Exception exception) {
            log.error("Auto-close job failed. trigger={}, date={}", trigger, today, exception);
        }
    }
}
