package com.padimasso.autocasting.application.talent.util;

import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;

// Plain-Java mirror of TalentProfileSpecs.fromFilter / hasRequiredMedia — the actual predicate
// the public talent catalog query applies — for call sites that already have a loaded
// TalentProfileEntity and don't want to run a query just to check visibility.
// Kept in sync with TalentProfileSpecs (SQL-level) and AdminUserSpecs.notVisibleInTalentCatalog()
// (SQL-level, used to filter the admin users list) — update all three together.
public final class TalentCatalogVisibility {

    private TalentCatalogVisibility() {
    }

    public static boolean isVisibleInCatalog(TalentProfileEntity profile) {
        if (profile.isDeleted() || profile.getUser().isSuspended()) {
            return false;
        }

        var media = profile.getMedia();
        if (media == null) {
            return false;
        }

        return isNotBlank(media.getHeadshotImageUrl()) && isNotBlank(media.getFullBodyImageUrl());
    }

    private static boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}
