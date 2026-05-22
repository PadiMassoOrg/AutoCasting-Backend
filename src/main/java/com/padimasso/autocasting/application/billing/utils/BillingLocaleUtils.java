package com.padimasso.autocasting.application.billing.utils;

import java.util.Locale;

public final class BillingLocaleUtils {

    public static final String DEFAULT_LOCALE_TAG = "es-AR";
    private static final Locale DEFAULT_LOCALE = Locale.forLanguageTag(DEFAULT_LOCALE_TAG);

    private BillingLocaleUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Locale resolveOrDefault(String localeTag) {
        if (localeTag == null || localeTag.isBlank()) {
            return DEFAULT_LOCALE;
        }

        Locale resolved = Locale.forLanguageTag(localeTag.trim());
        if (resolved.getLanguage() == null || resolved.getLanguage().isBlank()) {
            return DEFAULT_LOCALE;
        }

        return resolved;
    }
}
