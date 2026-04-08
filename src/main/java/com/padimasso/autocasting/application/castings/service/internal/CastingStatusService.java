package com.padimasso.autocasting.application.castings.service.internal;

import com.padimasso.autocasting.application.castings.repository.CastingRepository;
import com.padimasso.autocasting.application.castings.repository.projection.CastingPublishGateProjection;
import com.padimasso.autocasting.application.sitemetadata.model.CastingStatusOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_DRAFT;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_PUBLISHED;

@Service
@RequiredArgsConstructor
public class CastingStatusService {

    private final CastingRepository castingRepository;
    private final SiteMetadataResolver siteMetadataResolver;
    private final CastingStatusTransitionPolicy transitionPolicy;

    /**
     * Regla:
     * - Si el casting está PUBLISHED
     * - y deja de ser publishable (secciones/deadline)
     * => forzar DRAFT.
     * Se llama luego de cualquier cambio en secciones.
     */
    @Transactional
    public void recomputeAfterSectionChange(UUID castingId) {
        if (castingId == null) return;

        CastingPublishGateProjection gate = castingRepository.findPublishGateByCastingId(castingId).orElse(null);
        if (gate == null) return;

        if (!CASTING_STATUS_PUBLISHED.equals(gate.getCastingStatusCode())) return;

        boolean publishAllowedNow = isPublishAllowedNow(gate);

        if (!publishAllowedNow) {
            CastingStatusOptionEntity draftStatus =
                siteMetadataResolver.resolveCastingStatusByCodeOrThrow(CASTING_STATUS_DRAFT);

            castingRepository.forceDraftIfPublished(
                castingId,
                draftStatus,
                CASTING_STATUS_PUBLISHED
            );
        }
    }

    private boolean isPublishAllowedNow(CastingPublishGateProjection gate) {
        try {
            transitionPolicy.assertCanPublish(gate);
            return true;
        } catch (RuntimeException ex) {
            return false;
        }
    }
}
