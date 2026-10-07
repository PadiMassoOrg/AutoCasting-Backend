package com.padimasso.autocasting.application.admin.util;

import com.padimasso.autocasting.application.castings.model.CastingEntity;

import java.util.Set;

import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_DRAFT;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_PAUSED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_PUBLISHED;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.CASTINGS_ADMIN_NOT_EDITABLE;

/** Admins edit the castings still in play; closed and archived castings are a read-only record. */
public final class AdminCastingEditability {

    private static final Set<String> EDITABLE_STATUS_CODES = Set.of(
        CASTING_STATUS_DRAFT, CASTING_STATUS_PUBLISHED, CASTING_STATUS_PAUSED
    );

    private AdminCastingEditability() {
    }

    public static boolean isEditable(String statusCode) {
        return statusCode != null && EDITABLE_STATUS_CODES.contains(statusCode);
    }

    public static void assertEditable(CastingEntity casting) {
        String statusCode = casting.getStatus() != null ? casting.getStatus().getStringCode() : null;
        if (!isEditable(statusCode)) {
            throw new IllegalStateException(CASTINGS_ADMIN_NOT_EDITABLE);
        }
    }
}
