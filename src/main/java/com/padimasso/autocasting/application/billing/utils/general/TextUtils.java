package com.padimasso.autocasting.application.billing.utils.general;

public final class TextUtils {

    private TextUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
