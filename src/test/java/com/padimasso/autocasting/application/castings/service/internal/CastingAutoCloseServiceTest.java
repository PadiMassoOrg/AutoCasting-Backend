package com.padimasso.autocasting.application.castings.service.internal;

import com.padimasso.autocasting.application.castings.repository.CastingRepository;
import com.padimasso.autocasting.application.castings.repository.projection.CastingCloseTargetProjection;
import com.padimasso.autocasting.application.castings.service.CastingMediaCleanupService;
import com.padimasso.autocasting.application.sitemetadata.model.CastingStatusOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.PROPOSALS_SYSTEM_EMPLOYER_PROFILE_ID;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_CLOSED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_DRAFT;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_PAUSED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_PUBLISHED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CastingAutoCloseServiceTest {

    @Mock
    private CastingRepository castingRepository;
    @Mock
    private SiteMetadataResolver siteMetadataResolver;
    @Mock
    private CastingMediaCleanupService castingMediaCleanupService;

    private CastingAutoCloseService service;

    private final LocalDate today = LocalDate.of(2026, 9, 23);
    private final UUID closedStatusId = UUID.randomUUID();

    private static CastingStatusOptionEntity status(UUID id) {
        CastingStatusOptionEntity option = new CastingStatusOptionEntity();
        option.setId(id);
        return option;
    }

    private static CastingCloseTargetProjection target(UUID castingId, UUID employerProfileId) {
        return new CastingCloseTargetProjection() {
            @Override
            public UUID getCastingId() {
                return castingId;
            }

            @Override
            public UUID getEmployerProfileId() {
                return employerProfileId;
            }
        };
    }

    @BeforeEach
    void setUp() {
        service = new CastingAutoCloseService(castingRepository, siteMetadataResolver, castingMediaCleanupService);

        when(siteMetadataResolver.resolveCastingStatusByCodeOrThrow(CASTING_STATUS_CLOSED)).thenReturn(status(closedStatusId));
        when(siteMetadataResolver.resolveCastingStatusByCodeOrThrow(CASTING_STATUS_DRAFT)).thenReturn(status(UUID.randomUUID()));
        when(siteMetadataResolver.resolveCastingStatusByCodeOrThrow(CASTING_STATUS_PUBLISHED)).thenReturn(status(UUID.randomUUID()));
        when(siteMetadataResolver.resolveCastingStatusByCodeOrThrow(CASTING_STATUS_PAUSED)).thenReturn(status(UUID.randomUUID()));
    }

    @Test
    void closeExpiredCastings_cleansUpSupabaseFolderForEachClosedCasting() {
        UUID castingId1 = UUID.randomUUID();
        UUID employerId1 = UUID.randomUUID();
        UUID castingId2 = UUID.randomUUID();
        UUID employerId2 = UUID.randomUUID();

        when(castingRepository.findExpiredCastingCloseTargets(eq(today), anyList(), eq(PROPOSALS_SYSTEM_EMPLOYER_PROFILE_ID)))
            .thenReturn(List.of(target(castingId1, employerId1), target(castingId2, employerId2)));
        when(castingRepository.closeExpiredCastings(eq(today), eq(closedStatusId), anyList(), eq(PROPOSALS_SYSTEM_EMPLOYER_PROFILE_ID))).thenReturn(2);

        int closedCount = service.closeExpiredCastings(today);

        assertEquals(2, closedCount);
        verify(castingMediaCleanupService).deleteCastingFolder(employerId1, castingId1);
        verify(castingMediaCleanupService).deleteCastingFolder(employerId2, castingId2);
    }

    @Test
    void closeExpiredCastings_noExpiredCastings_doesNotCallCleanup() {
        when(castingRepository.findExpiredCastingCloseTargets(eq(today), anyList(), eq(PROPOSALS_SYSTEM_EMPLOYER_PROFILE_ID))).thenReturn(List.of());
        when(castingRepository.closeExpiredCastings(eq(today), eq(closedStatusId), anyList(), eq(PROPOSALS_SYSTEM_EMPLOYER_PROFILE_ID))).thenReturn(0);

        int closedCount = service.closeExpiredCastings(today);

        assertEquals(0, closedCount);
        verify(castingMediaCleanupService, never()).deleteCastingFolder(any(), any());
    }

    @Test
    void closeExpiredCastings_cleanupFailureForOneCasting_doesNotPreventOthersOrFailTheJob() {
        UUID castingId1 = UUID.randomUUID();
        UUID employerId1 = UUID.randomUUID();
        UUID castingId2 = UUID.randomUUID();
        UUID employerId2 = UUID.randomUUID();

        when(castingRepository.findExpiredCastingCloseTargets(eq(today), anyList(), eq(PROPOSALS_SYSTEM_EMPLOYER_PROFILE_ID)))
            .thenReturn(List.of(target(castingId1, employerId1), target(castingId2, employerId2)));
        when(castingRepository.closeExpiredCastings(eq(today), eq(closedStatusId), anyList(), eq(PROPOSALS_SYSTEM_EMPLOYER_PROFILE_ID))).thenReturn(2);
        doThrow(new RuntimeException("supabase down"))
            .when(castingMediaCleanupService).deleteCastingFolder(employerId1, castingId1);

        int closedCount = service.closeExpiredCastings(today);

        assertEquals(2, closedCount);
        verify(castingMediaCleanupService).deleteCastingFolder(employerId2, castingId2);
    }
}
