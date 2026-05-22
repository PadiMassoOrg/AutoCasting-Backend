package com.padimasso.autocasting.application.billing.utils;

import java.util.Locale;

public final class BillingCurrencyMetadataUtils {

    private static final String CURRENCY_PREFIX = "sitemetadata.currency.";

    private BillingCurrencyMetadataUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String toSiteMetadataStringCode(String currencyCode) {
        return CURRENCY_PREFIX + currencyCode.toLowerCase(Locale.ROOT);
    }
}
