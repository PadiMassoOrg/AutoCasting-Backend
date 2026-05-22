package com.padimasso.autocasting.application.billing.utils;

import com.padimasso.autocasting.application.billing.utils.general.TextUtils;

public final class BillingNormalizationUtils {

    private BillingNormalizationUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String normalizeCode(String code) {
        return code.trim().toUpperCase();
    }

    public static String normalizeStringCode(String code) {
        return code.trim();
    }

    public static String normalizeCurrency(String currency) {
        return currency.trim().toUpperCase();
    }

    public static String normalizeNullableCurrency(String currency) {
        String normalized = TextUtils.trimToNull(currency);
        return normalized == null ? null : normalized.toUpperCase();
    }
}
