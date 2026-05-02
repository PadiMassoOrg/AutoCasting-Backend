package com.padimasso.autocasting.application.admin.users.dto.response;

import java.util.List;

public record AdminUsersPageResponse(
    List<AdminUserRowResponse> items,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean hasNext
) {
}
