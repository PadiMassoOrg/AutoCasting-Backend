package com.padimasso.autocasting.application.admin.dto.response;

import java.util.List;

public record AdminCastingsPageResponse(
    List<AdminCastingRowResponse> items,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean hasNext
) {
}
