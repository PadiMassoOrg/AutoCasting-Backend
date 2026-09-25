package com.padimasso.autocasting.application.castings.service.internal;

import com.padimasso.autocasting.application.castings.dto.request.CastingRoleRequest;
import com.padimasso.autocasting.application.castings.dto.request.CastingUpsertRequest;
import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.shared.util.PayRateTypeSupport;
import com.padimasso.autocasting.application.shared.util.TextNormalizer;
import com.padimasso.autocasting.application.sitemetadata.model.GenderOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.PayRateTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.*;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.*;

// Maps casting / role request payloads onto entities. Owner-agnostic, so it's shared by the employer
// editor services and by flows that write castings on someone else's behalf (Casting Proposals).
@Component
@RequiredArgsConstructor
public class CastingDataApplier {

    private final SiteMetadataResolver siteMetadataResolver;

    public void applyCastingData(CastingEntity casting, CastingUpsertRequest request) {
        casting.setTitle(TextNormalizer.normalizeNullable(request.title()));
        casting.setProjectType(request.projectTypeId() != null
            ? siteMetadataResolver.resolveProjectTypeOrThrow(request.projectTypeId())
            : null);
        casting.setCastingModality(request.castingModalityId() != null
            ? siteMetadataResolver.resolveCastingModalityOrThrow(request.castingModalityId())
            : null);
        casting.setLocationText(TextNormalizer.normalizeNullable(request.locationText()));
        casting.setApplicationDeadline(request.applicationDeadline());
        casting.setHasWardrobeFitting(request.hasWardrobeFitting());
        casting.setWardrobeFittingText(Boolean.FALSE.equals(request.hasWardrobeFitting())
            ? null
            : TextNormalizer.normalizeNullable(request.wardrobeFittingText()));
        casting.setShootingStartDate(request.shootingStartDate());
        casting.setShootingEndDate(request.shootingEndDate());
        casting.setDescription(TextNormalizer.normalizeNullable(request.description()));
    }

    public void applyRoleData(CastingRoleEntity role, CastingRoleRequest request) {
        role.setRoleName(TextNormalizer.normalizeNullable(request.roleName()));
        role.setRoleType(siteMetadataResolver.resolveRoleTypeOrThrow(request.roleTypeId()));
        role.setGender(resolveGenderOrDefault(request.genderId()));
        role.setAgeMin(request.ageMin());
        role.setAgeMax(request.ageMax());
        role.setDescription(TextNormalizer.normalizeNullable(request.description()));

        Set<UUID> professionIds = request.professionIds() == null ? Set.of() : request.professionIds();
        role.setProfessions(new HashSet<>(siteMetadataResolver.resolveProfessionsOrThrow(professionIds)));

        Set<UUID> skillIds = request.skillIds() == null ? Set.of() : request.skillIds();
        role.setSkills(new HashSet<>(siteMetadataResolver.resolveSkillsOrThrow(skillIds)));

        role.setPayRateType(resolvePayRateTypeOrDefault(request.payRateTypeId()));
        role.setCurrency(request.currencyId() != null ? siteMetadataResolver.resolveCurrencyOrThrow(request.currencyId()) : null);
        role.setAmount(request.amount());
        role.setRemunerationNotes(TextNormalizer.normalizeNullable(request.remunerationNotes()));

        role.setRequiresAudio(Boolean.TRUE.equals(request.requiresAudio()));
        role.setRequiresVideo(Boolean.TRUE.equals(request.requiresVideo()));
        role.setRequirementDescription(TextNormalizer.normalizeNullable(request.requirementDescription()));

        role.setEthnicity(request.ethnicityId() != null ? siteMetadataResolver.resolveEthnicityOrThrow(request.ethnicityId()) : null);
        role.setTattoo(request.tattoo());
        role.setPassport(request.passport());
        role.setDrivingLicense(request.drivingLicense());
        role.setReferencePhotoUrl(TextNormalizer.normalizeNullable(request.referencePhotoUrl()));

        validateRole(role);
    }

    public void validateRole(CastingRoleEntity role) {
        if (role.getAgeMin() != null && role.getAgeMax() != null && role.getAgeMin() > role.getAgeMax()) {
            throw new IllegalArgumentException(CASTING_AGE_RANGE_INVALID);
        }

        if (role.getAmount() != null && role.getAmount().signum() < 0) {
            throw new IllegalArgumentException(CASTING_AMOUNT_NEGATIVE);
        }

        String payRateCode = role.getPayRateType() != null ? role.getPayRateType().getStringCode() : null;
        boolean isUnpaidLike = PayRateTypeSupport.isUnpaidLike(payRateCode);

        if (isUnpaidLike) {
            role.setAmount(null);
            if (role.getCurrency() == null) {
                role.setCurrency(siteMetadataResolver.resolveCurrencyByCodeOrThrow(CURRENCY_ARS));
            }
            return;
        }

        if (role.getAmount() == null) {
            throw new IllegalArgumentException(CASTING_AMOUNT_REQUIRED);
        }

        if (role.getCurrency() == null) {
            role.setCurrency(siteMetadataResolver.resolveCurrencyByCodeOrThrow(CURRENCY_ARS));
        }
    }

    private GenderOptionEntity resolveGenderOrDefault(UUID genderId) {
        return genderId != null
            ? siteMetadataResolver.resolveGenderOrThrow(genderId)
            : siteMetadataResolver.resolveGenderByCodeOrThrow(GENDER_OPTION_INDISTINCT);
    }

    private PayRateTypeOptionEntity resolvePayRateTypeOrDefault(UUID payRateTypeId) {
        return payRateTypeId != null
            ? siteMetadataResolver.resolvePayRateTypeOrThrow(payRateTypeId)
            : siteMetadataResolver.resolvePayRateTypeByCodeOrThrow(PAY_RATE_TYPE_UNPAID);
    }
}
