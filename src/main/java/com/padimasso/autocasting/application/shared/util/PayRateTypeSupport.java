package com.padimasso.autocasting.application.shared.util;

public final class PayRateTypeSupport {

    private PayRateTypeSupport() {
    }

    // A role's pay-rate type has no fixed amount (unpaid, "to be agreed", collaborative,
    // cooperative) when its string code ends with one of these suffixes. Used everywhere a
    // role's remuneration completeness/requirement is decided, so a new non-paying pay-rate
    // type only needs to be added here once.
    public static boolean isUnpaidLike(String payRateTypeStringCode) {
        if (payRateTypeStringCode == null) return false;
        return payRateTypeStringCode.endsWith(".unpaid")
            || payRateTypeStringCode.endsWith(".to_be_agreed")
            || payRateTypeStringCode.endsWith(".cooperative")
            || payRateTypeStringCode.endsWith(".collaborative");
    }
}
