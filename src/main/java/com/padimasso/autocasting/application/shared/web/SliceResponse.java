package com.padimasso.autocasting.application.shared.web;

import java.util.List;

public record SliceResponse<T>(
    List<T> items,
    boolean hasNext,
    int page,
    int size,
    Long totalCount
) {
    public SliceResponse(List<T> items, boolean hasNext, int page, int size) {
        this(items, hasNext, page, size, null);
    }
}
