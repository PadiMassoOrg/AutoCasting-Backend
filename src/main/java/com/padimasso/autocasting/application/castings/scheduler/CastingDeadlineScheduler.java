package com.padimasso.autocasting.application.castings.scheduler;

import com.padimasso.autocasting.application.castings.service.internal.CastingAutoCloseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

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

    @Value("${app.jobs.close-expired-castings.zone:UTC}")
    private String schedulerZone;

    @Scheduled(
        cron = "${app.jobs.close-expired-castings.cron:0 0 0 * * *}",
        zone = "${app.jobs.close-expired-castings.zone:UTC}"
    )
    public void closeExpiredCastings() {
        LocalDate todayInJobZone = LocalDate.now(ZoneId.of(schedulerZone));
        int closedCount = castingAutoCloseService.closeExpiredCastings(todayInJobZone);

        log.info("Auto-close job executed. date={}, closedCastings={}", todayInJobZone, closedCount);
    }
}
