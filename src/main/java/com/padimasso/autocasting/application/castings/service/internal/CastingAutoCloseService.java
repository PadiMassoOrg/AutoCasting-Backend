package com.padimasso.autocasting.application.castings.service.internal;

import com.padimasso.autocasting.application.castings.repository.CastingRepository;
import com.padimasso.autocasting.application.castings.repository.projection.CastingCloseTargetProjection;
import com.padimasso.autocasting.application.castings.service.CastingMediaCleanupService;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_CLOSED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_DRAFT;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_PAUSED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_PUBLISHED;
import static com.padimasso.autocasting.config.AppConstants.PROPOSALS_SYSTEM_EMPLOYER_PROFILE_ID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CastingAutoCloseService {

    private final CastingRepository castingRepository;
    private final SiteMetadataResolver siteMetadataResolver;
    private final CastingMediaCleanupService castingMediaCleanupService;

    @Transactional
    public int closeExpiredCastings(LocalDate today) {
        var closed = siteMetadataResolver.resolveCastingStatusByCodeOrThrow(CASTING_STATUS_CLOSED);
        var draft = siteMetadataResolver.resolveCastingStatusByCodeOrThrow(CASTING_STATUS_DRAFT);
        var published = siteMetadataResolver.resolveCastingStatusByCodeOrThrow(CASTING_STATUS_PUBLISHED);
        var paused = siteMetadataResolver.resolveCastingStatusByCodeOrThrow(CASTING_STATUS_PAUSED);
        List<UUID> allowedStatusIds = List.of(draft.getId(), published.getId(), paused.getId());

        // Fetched before the bulk UPDATE below so we know exactly which castings are about to
        // close (the UPDATE itself is a native bulk statement — no entities loaded, no per-row
        // hook to trigger Supabase cleanup from). Same WHERE clause as the UPDATE, so the two
        // queries always agree on which rows match.
        List<CastingCloseTargetProjection> closeTargets =
            castingRepository.findExpiredCastingCloseTargets(today, allowedStatusIds, PROPOSALS_SYSTEM_EMPLOYER_PROFILE_ID);

        int closedCount = castingRepository.closeExpiredCastings(
            today,
            closed.getId(),
            allowedStatusIds,
            PROPOSALS_SYSTEM_EMPLOYER_PROFILE_ID
        );

        for (CastingCloseTargetProjection target : closeTargets) {
            try {
                castingMediaCleanupService.deleteCastingFolder(target.getEmployerProfileId(), target.getCastingId());
            } catch (Exception ex) {
                log.warn("Could not clean up Supabase folder for auto-closed casting {}", target.getCastingId(), ex);
            }
        }

        return closedCount;
    }
}
