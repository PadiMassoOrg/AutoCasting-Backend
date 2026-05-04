package com.padimasso.autocasting.application.admin.users.service;

import com.padimasso.autocasting.application.admin.users.dto.request.AdminUserAccountActionRequest;
import com.padimasso.autocasting.application.admin.users.dto.response.AdminUserRowResponse;
import com.padimasso.autocasting.application.admin.users.dto.response.AdminUsersPageResponse;
import com.padimasso.autocasting.application.employer.dto.response.EmployerProfileResponse;
import com.padimasso.autocasting.application.talent.dto.response.PublicProfileResponse;

import java.util.UUID;

public interface AdminUserService {
    AdminUsersPageResponse listUsers(int page, int size);

    AdminUserRowResponse blockUser(UUID userId, AdminUserAccountActionRequest request);

    AdminUserRowResponse restoreUser(UUID userId, AdminUserAccountActionRequest request);

    PublicProfileResponse getTalentProfileForAdmin(UUID userId);

    EmployerProfileResponse getEmployerProfileForAdmin(UUID userId);
}
