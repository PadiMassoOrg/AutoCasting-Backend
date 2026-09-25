package com.padimasso.autocasting.application.castings.service.internal;

import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingModalityOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CurrencyOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.GenderOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.PayRateTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.ProjectTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.RoleTypeOptionEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;

import static com.padimasso.autocasting.config.AppConstants.CASTING_MODALITY_AUTOCASTING;
import static com.padimasso.autocasting.config.AppConstants.CASTING_MODALITY_ON_SITE;
import static com.padimasso.autocasting.config.AppConstants.PAY_RATE_TYPE_UNPAID;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CastingPublishabilityTest {

    private static CastingModalityOptionEntity modality(String code) {
        CastingModalityOptionEntity modality = new CastingModalityOptionEntity();
        modality.setStringCode(code);
        return modality;
    }

    private static PayRateTypeOptionEntity payRate(String code) {
        PayRateTypeOptionEntity payRate = new PayRateTypeOptionEntity();
        payRate.setStringCode(code);
        return payRate;
    }

    private static CastingEntity completeCasting() {
        CastingEntity casting = CastingEntity.builder()
            .title("Casting")
            .projectType(new ProjectTypeOptionEntity())
            .castingModality(modality(CASTING_MODALITY_AUTOCASTING))
            .applicationDeadline(LocalDate.now().plusDays(10))
            .hasWardrobeFitting(false)
            .shootingStartDate(LocalDate.now().plusDays(20))
            .shootingEndDate(LocalDate.now().plusDays(21))
            .roles(new HashSet<>())
            .build();
        casting.getRoles().add(completeRole(casting));
        return casting;
    }

    private static CastingRoleEntity completeRole(CastingEntity casting) {
        return CastingRoleEntity.builder()
            .casting(casting)
            .roleName("Lead")
            .roleType(new RoleTypeOptionEntity())
            .gender(new GenderOptionEntity())
            .ageMin((short) 20)
            .ageMax((short) 30)
            .payRateType(payRate(PAY_RATE_TYPE_UNPAID))
            .build();
    }

    @Test
    void isPublishable_completeCastingWithCompleteRole_returnsTrue() {
        assertTrue(CastingPublishability.isPublishable(completeCasting()));
    }

    @Test
    void isPublishable_missingTitle_returnsFalse() {
        CastingEntity casting = completeCasting();
        casting.setTitle(null);

        assertFalse(CastingPublishability.isPublishable(casting));
    }

    @Test
    void isPublishable_missingDeadline_returnsFalse() {
        CastingEntity casting = completeCasting();
        casting.setApplicationDeadline(null);

        assertFalse(CastingPublishability.isPublishable(casting));
    }

    @Test
    void isPublishable_onSiteWithoutLocation_returnsFalse() {
        CastingEntity casting = completeCasting();
        casting.setCastingModality(modality(CASTING_MODALITY_ON_SITE));

        assertFalse(CastingPublishability.isPublishable(casting));
    }

    @Test
    void isPublishable_wardrobeFittingWithoutText_returnsFalse() {
        CastingEntity casting = completeCasting();
        casting.setHasWardrobeFitting(true);

        assertFalse(CastingPublishability.isPublishable(casting));
    }

    @Test
    void isPublishable_noRoles_returnsFalse() {
        CastingEntity casting = completeCasting();
        casting.getRoles().clear();

        assertFalse(CastingPublishability.isPublishable(casting));
    }

    @Test
    void isPublishable_onlyDeletedRoles_returnsFalse() {
        CastingEntity casting = completeCasting();
        casting.getRoles().forEach(role -> role.setDeleted(true));

        assertFalse(CastingPublishability.isPublishable(casting));
    }

    @Test
    void isPublishable_paidRoleWithoutAmount_returnsFalse() {
        CastingEntity casting = completeCasting();
        CastingRoleEntity role = casting.getRoles().iterator().next();
        role.setPayRateType(payRate("sitemetadata.pay_rate_type.per_day"));
        role.setCurrency(new CurrencyOptionEntity());

        assertFalse(CastingPublishability.isPublishable(casting));
    }

    @Test
    void isPublishable_paidRoleWithCurrencyAndPositiveAmount_returnsTrue() {
        CastingEntity casting = completeCasting();
        CastingRoleEntity role = casting.getRoles().iterator().next();
        role.setPayRateType(payRate("sitemetadata.pay_rate_type.per_day"));
        role.setCurrency(new CurrencyOptionEntity());
        role.setAmount(BigDecimal.TEN);

        assertTrue(CastingPublishability.isPublishable(casting));
    }
}
