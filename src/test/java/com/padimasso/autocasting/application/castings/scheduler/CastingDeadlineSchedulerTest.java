package com.padimasso.autocasting.application.castings.scheduler;

import com.padimasso.autocasting.application.castings.service.internal.CastingAutoCloseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CastingDeadlineSchedulerTest {

    @Mock
    private CastingAutoCloseService castingAutoCloseService;

    private CastingDeadlineScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new CastingDeadlineScheduler(castingAutoCloseService);
        ReflectionTestUtils.setField(scheduler, "schedulerZone", "UTC");
    }

    @Test
    void closeExpiredCastings_serviceSucceeds_doesNotThrow() {
        when(castingAutoCloseService.closeExpiredCastings(any(LocalDate.class))).thenReturn(3);

        scheduler.closeExpiredCastings();

        verify(castingAutoCloseService).closeExpiredCastings(any(LocalDate.class));
    }

    @Test
    void closeExpiredCastings_serviceThrows_isSwallowedAndDoesNotPropagate() {
        when(castingAutoCloseService.closeExpiredCastings(any(LocalDate.class)))
                .thenThrow(new IllegalStateException("missing casting status row"));

        scheduler.closeExpiredCastings();

        verify(castingAutoCloseService).closeExpiredCastings(any(LocalDate.class));
    }
}
