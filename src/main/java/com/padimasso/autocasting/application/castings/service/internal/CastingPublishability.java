package com.padimasso.autocasting.application.castings.service.internal;

import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.shared.util.PayRateTypeSupport;

import java.util.List;

import static com.padimasso.autocasting.config.AppConstants.CASTING_MODALITY_ON_SITE;

// Single source of truth for "is this casting complete enough to publish": basic info filled in and
// at least one role, every role complete. Used by the employer editor (`publishable` flag) and by
// Casting Proposals, which only accept complete castings.
public final class CastingPublishability {

    private CastingPublishability() {
    }

    public static boolean isPublishable(CastingEntity casting) {
        if (!hasCompleteBasicInfo(casting)) return false;
        List<CastingRoleEntity> activeRoles = casting.getRoles() == null
            ? List.of()
            : casting.getRoles().stream().filter(role -> role != null && !role.isDeleted()).toList();
        if (activeRoles.isEmpty()) return false;
        return activeRoles.stream().allMatch(CastingPublishability::hasCompleteRole);
    }

    static boolean hasCompleteBasicInfo(CastingEntity casting) {
        if (casting == null) return false;
        if (casting.getTitle() == null || casting.getProjectType() == null || casting.getCastingModality() == null)
            return false;
        if (casting.getApplicationDeadline() == null || casting.getHasWardrobeFitting() == null) return false;
        if (casting.getShootingStartDate() == null || casting.getShootingEndDate() == null) return false;
        if (CASTING_MODALITY_ON_SITE.equals(casting.getCastingModality().getStringCode()) && casting.getLocationText() == null)
            return false;
        return !casting.getHasWardrobeFitting() || casting.getWardrobeFittingText() != null;
    }

    static boolean hasCompleteRole(CastingRoleEntity role) {
        if (role == null) return false;
        if (role.getRoleName() == null || role.getRoleType() == null || role.getGender() == null) return false;
        if (role.getAgeMin() == null || role.getAgeMax() == null || role.getAgeMin() > role.getAgeMax()) return false;
        if (role.getPayRateType() == null) return false;

        boolean isUnpaidLike = PayRateTypeSupport.isUnpaidLike(role.getPayRateType().getStringCode());

        if (isUnpaidLike) {
            return true;
        }

        return role.getCurrency() != null && role.getAmount() != null && role.getAmount().signum() > 0;
    }
}
