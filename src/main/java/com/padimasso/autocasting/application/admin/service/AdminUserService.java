package com.padimasso.autocasting.application.admin.service;

import com.padimasso.autocasting.application.admin.dto.response.AdminUserDetailResponse;
import com.padimasso.autocasting.application.admin.dto.request.AdminUserUpdateRequest;
import com.padimasso.autocasting.application.admin.dto.request.AdminUserSuspensionRequest;
import com.padimasso.autocasting.application.admin.dto.request.AdminBulkTalentWelcomeEmailRequest;
import com.padimasso.autocasting.application.admin.dto.request.AdminRemoveTalentMediaRequest;
import com.padimasso.autocasting.application.admin.dto.request.AdminTalentMediaSlot;
import com.padimasso.autocasting.application.admin.dto.response.AdminBulkTalentWelcomeEmailResultResponse;
import com.padimasso.autocasting.application.admin.dto.response.AdminEmployerProfileResponse;
import com.padimasso.autocasting.application.admin.dto.response.AdminTalentProfileResponse;
import com.padimasso.autocasting.application.admin.dto.response.AdminUserRowResponse;
import com.padimasso.autocasting.application.common.dto.PageResponse;

import java.util.UUID;

public interface AdminUserService {
    PageResponse<AdminUserRowResponse> listUsers(int page, int size, String q, boolean notVisibleInCatalog);

    AdminUserDetailResponse getUserDetail(UUID userId);

    AdminUserDetailResponse updateUserDetail(UUID userId, AdminUserUpdateRequest request);

    void updateSuspension(UUID userId, AdminUserSuspensionRequest request);

    AdminTalentProfileResponse getTalentProfileForAdmin(UUID userId);

    void removeTalentMedia(UUID userId, AdminTalentMediaSlot slot, Integer index, AdminRemoveTalentMediaRequest request);

    AdminEmployerProfileResponse getEmployerProfileForAdmin(UUID userId);

    AdminBulkTalentWelcomeEmailResultResponse sendBulkTalentWelcomeEmail(AdminBulkTalentWelcomeEmailRequest request);
}
