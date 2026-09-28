package com.padimasso.autocasting.application.castings.scheduler;

import com.padimasso.autocasting.application.castings.service.internal.CastingAutoCloseService;
import com.padimasso.autocasting.application.castings.util.CastingDeadlines;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CastingDeadlineSchedulerTest {

    @Mock
    private CastingAutoCloseService castingAutoCloseService;

    @InjectMocks
    private CastingDeadlineScheduler scheduler;

    @Test
    void scheduledRun_closesCastingsUpToTodayInArgentina() {
        scheduler.closeExpiredCastings();

        verify(castingAutoCloseService).closeExpiredCastings(CastingDeadlines.today());
    }

    @Test
    void startupRun_catchesUpCastingsMissedWhileTheBackendWasDown() {
        scheduler.closeExpiredCastingsOnStartup();

        verify(castingAutoCloseService).closeExpiredCastings(CastingDeadlines.today());
    }

    @Test
    void failures_areLoggedAndDoNotPropagate() {
        when(castingAutoCloseService.closeExpiredCastings(any())).thenThrow(new RuntimeException("db down"));

        assertDoesNotThrow(() -> scheduler.closeExpiredCastingsOnStartup());
    }
}
