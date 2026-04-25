package com.padimasso.autocasting.application.admin.service;

import com.padimasso.autocasting.application.admin.dto.response.AdminUsersPageResponse;

public interface AdminUserService {
    AdminUsersPageResponse listUsers(int page, int size);
}
