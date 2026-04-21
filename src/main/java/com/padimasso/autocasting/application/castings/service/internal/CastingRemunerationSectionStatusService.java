package com.padimasso.autocasting.application.castings.service.internal;

import com.padimasso.autocasting.application.castings.model.CastingRemunerationEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRemunerationsSectionRepository;
import com.padimasso.autocasting.application.castings.repository.CastingRoleRepository;
import com.padimasso.autocasting.application.sitemetadata.model.CastingSectionStatusOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.*;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.CASTINGS_SECTION_NOT_FOUND;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class CastingRemunerationSectionStatusService {

    private final CastingRemunerationsSectionRepository remunerationsSectionRepository;
    private final CastingRoleRepository castingRoleRepository;
    private final SiteMetadataResolver siteMetadataResolver;

    /**
     * Recalcula el sectionStatus de la sección Remuneración del casting.
     * <p>
     * Regla propuesta (robusta y alineada a tu UI):
     * - Si no hay roles => NOT_STARTED
     * - Si hay roles:
     * - Si compensationType = COLLABORATIVE => COMPLETED (notes es opcional/nullable)
     * - Si compensationType = PAID => COMPLETED si NO hay remuneraciones incompletas, si no IN_PROGRESS
     * <p>
     * Nota: Para tu caso actual (rol creado con remuneración por defecto), esto hará COMPLETED.
     */
    @Transactional
    public void recomputeForCasting(UUID castingId) {
        CastingRemunerationEntity section = remunerationsSectionRepository
            .findByCastingIdAndDeletedFalse(castingId)
            .orElseThrow(() -> new IllegalArgumentException(CASTINGS_SECTION_NOT_FOUND));

        long activeRoles = castingRoleRepository.countByRolesSection_Casting_IdAndDeletedFalse(castingId);

        String nextStatusCode;
        if (activeRoles <= 0) {
            nextStatusCode = CASTING_SECTION_STATUS_NOT_STARTED;
        } else {
            String compensationCode = section.getCompensationType() != null
                ? section.getCompensationType().getStringCode()
                : null;

            if (CASTING_COMPENSATION_TYPE_COLLABORATIVE.equals(compensationCode)) {
                nextStatusCode = CASTING_SECTION_STATUS_COMPLETED;
            } else {
                // Default: PAID (o cualquier otro) => depende de remuneraciones por rol
                boolean hasIncomplete = castingRoleRepository.existsIncompleteRemunerationByCastingId(
                    castingId,
                    PAY_RATE_TYPE_UNPAID
                );
                nextStatusCode = hasIncomplete ? CASTING_SECTION_STATUS_IN_PROGRESS : CASTING_SECTION_STATUS_COMPLETED;
            }
        }

        CastingSectionStatusOptionEntity next =
            siteMetadataResolver.resolveCastingSectionStatusByCodeOrThrow(nextStatusCode);

        section.setSectionStatus(next);
        remunerationsSectionRepository.save(section);
    }

}
