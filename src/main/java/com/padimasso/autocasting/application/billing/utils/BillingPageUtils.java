package com.padimasso.autocasting.application.billing.utils;

import com.padimasso.autocasting.application.billing.dto.response.BillingPageResponse;
import org.springframework.data.domain.Page;

public final class BillingPageUtils {

    private BillingPageUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static <T> BillingPageResponse<T> toBillingPageResponse(Page<T> page) {
        return new BillingPageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.hasNext()
        );
    }
}
