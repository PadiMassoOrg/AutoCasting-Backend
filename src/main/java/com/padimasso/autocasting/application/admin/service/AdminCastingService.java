package com.padimasso.autocasting.application.admin.service;

import com.padimasso.autocasting.application.admin.dto.response.AdminCastingDetailsResponse;
import com.padimasso.autocasting.application.admin.dto.response.AdminCastingRowResponse;
import com.padimasso.autocasting.application.common.dto.PageResponse;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleResponse;

import java.util.List;
import java.util.UUID;

public interface AdminCastingService {
    PageResponse<AdminCastingRowResponse> listCastings(int page, int size, String q, List<String> statusIdTokens);
    AdminCastingDetailsResponse getCastingDetailsBySlug(String slug);
    CastingRoleResponse getCastingRoleById(UUID roleId);
}
