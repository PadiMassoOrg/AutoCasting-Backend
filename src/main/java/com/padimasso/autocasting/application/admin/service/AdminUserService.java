package com.padimasso.autocasting.application.admin.service;

import com.padimasso.autocasting.application.admin.dto.response.AdminUsersPageResponse;
import com.padimasso.autocasting.application.employer.dto.response.EmployerProfileResponse;
import com.padimasso.autocasting.application.talent.dto.response.PublicProfileResponse;

import java.util.UUID;

public interface AdminUserService {
    AdminUsersPageResponse listUsers(int page, int size);

    PublicProfileResponse getTalentProfileForAdmin(UUID userId);

    EmployerProfileResponse getEmployerProfileForAdmin(UUID userId);
}
