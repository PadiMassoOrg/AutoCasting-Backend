package com.padimasso.autocasting.application.admin.service;

import com.padimasso.autocasting.application.admin.dto.response.AdminCastingHistoryRowResponse;
import com.padimasso.autocasting.application.common.dto.PageResponse;

import java.util.UUID;

public interface AdminCastingHistoryService {
    /** The casting's history together with the history of all its roles, deleted ones included, newest first. */
    PageResponse<AdminCastingHistoryRowResponse> listCastingHistory(UUID castingId, int page, int size);
}
