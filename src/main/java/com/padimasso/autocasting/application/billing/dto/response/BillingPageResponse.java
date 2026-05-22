package com.padimasso.autocasting.application.billing.dto.response;

import java.util.List;

public record BillingPageResponse<T>(
    List<T> items,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean hasNext
) {
}
