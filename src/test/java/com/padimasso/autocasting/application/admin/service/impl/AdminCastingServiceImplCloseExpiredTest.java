package com.padimasso.autocasting.application.admin.service.impl;

import com.padimasso.autocasting.application.admin.mapper.AdminCastingMapper;
import com.padimasso.autocasting.application.castings.repository.CastingRepository;
import com.padimasso.autocasting.application.castings.repository.CastingRoleRepository;
import com.padimasso.autocasting.application.castings.service.internal.CastingAutoCloseService;
import com.padimasso.autocasting.application.castings.util.CastingDeadlines;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminCastingServiceImplCloseExpiredTest {

    @Mock
    private CastingRepository castingRepository;
    @Mock
    private CastingRoleRepository castingRoleRepository;
    @Mock
    private AdminCastingMapper adminCastingMapper;
    @Mock
    private CastingAutoCloseService castingAutoCloseService;

    @InjectMocks
    private AdminCastingServiceImpl service;

    @Test
    void closeExpiredCastings_runsTheNightlyJobForTodayAndReportsTheCount() {
        when(castingAutoCloseService.closeExpiredCastings(CastingDeadlines.today())).thenReturn(4);

        var response = service.closeExpiredCastings();

        assertEquals(4, response.closedCount());
    }
}
