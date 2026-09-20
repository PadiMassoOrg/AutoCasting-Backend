package com.padimasso.autocasting.application.talent.util;

import com.padimasso.autocasting.application.talent.TalentMediaRequirements;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;

// Plain-Java mirror of TalentProfileSpecs.visibleInCatalogPredicate — the actual predicate the
// public talent catalog query applies — for call sites that already have a loaded
// TalentProfileEntity and don't want to run a query just to check visibility. The media check
// reuses TalentMediaRequirements.hasRequiredPhotos, the single source of truth for that rule.
// Kept in sync with TalentProfileSpecs (SQL-level) and AdminUserSpecs.notVisibleInTalentCatalog()
// (SQL-level, used to filter the admin users list) — update all three together.
public final class TalentCatalogVisibility {

    private TalentCatalogVisibility() {
    }

    public static boolean isVisibleInCatalog(TalentProfileEntity profile) {
        if (profile.isDeleted() || profile.getUser().isSuspended()) {
            return false;
        }

        return TalentMediaRequirements.hasRequiredPhotos(profile.getMedia());
    }
}
