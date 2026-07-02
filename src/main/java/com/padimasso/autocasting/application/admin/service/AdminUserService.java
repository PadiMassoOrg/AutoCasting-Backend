package com.padimasso.autocasting.application.admin.service;

import com.padimasso.autocasting.application.admin.dto.response.AdminUserDetailResponse;
import com.padimasso.autocasting.application.admin.dto.request.AdminUserUpdateRequest;
import com.padimasso.autocasting.application.admin.dto.request.AdminUserSuspensionRequest;
import com.padimasso.autocasting.application.admin.dto.response.AdminUserRowResponse;
import com.padimasso.autocasting.application.common.dto.PageResponse;
import com.padimasso.autocasting.application.employer.dto.response.EmployerProfileResponse;
import com.padimasso.autocasting.application.talent.dto.response.PublicProfileResponse;

import java.util.UUID;

public interface AdminUserService {
    PageResponse<AdminUserRowResponse> listUsers(int page, int size, String q);

    AdminUserDetailResponse getUserDetail(UUID userId);

    AdminUserDetailResponse updateUserDetail(UUID userId, AdminUserUpdateRequest request);

    void updateSuspension(UUID userId, AdminUserSuspensionRequest request);

    PublicProfileResponse getTalentProfileForAdmin(UUID userId);

    EmployerProfileResponse getEmployerProfileForAdmin(UUID userId);
}
