package com.padimasso.autocasting.application.billing.utils.general;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public final class PaginationUtils {

    private PaginationUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static PageRequest pageRequest(int page, int size, int maxPageSize, Sort sort) {
        int normalizedSize = Math.min(Math.max(size, 1), maxPageSize);
        int normalizedPage = Math.max(page, 0);
        return PageRequest.of(normalizedPage, normalizedSize, sort);
    }
}
