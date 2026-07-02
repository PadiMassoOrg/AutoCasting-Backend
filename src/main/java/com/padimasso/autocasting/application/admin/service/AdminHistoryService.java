package com.padimasso.autocasting.application.admin.service;

import com.padimasso.autocasting.application.admin.dto.response.AdminHistoryRowResponse;
import com.padimasso.autocasting.application.common.dto.PageResponse;
import com.padimasso.autocasting.application.common.model.EntityType;

import java.util.UUID;

public interface AdminHistoryService {

    PageResponse<AdminHistoryRowResponse> listHistory(EntityType entityType, UUID entityId, int page, int size);
}
