package com.padimasso.autocasting.application.billing.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public final class BillingMoneyFormatUtils {

    private BillingMoneyFormatUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String formatAmountDisplay(long amountMinor, String currencyCode, Locale locale) {
        BigDecimal major = BigDecimal.valueOf(amountMinor, 2).setScale(2, RoundingMode.HALF_UP);

        NumberFormat formatter = NumberFormat.getNumberInstance(locale);
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        return currencyCode + " " + formatter.format(major);
    }
}
