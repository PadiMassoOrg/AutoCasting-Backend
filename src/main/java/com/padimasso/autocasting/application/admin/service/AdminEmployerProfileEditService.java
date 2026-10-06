package com.padimasso.autocasting.application.admin.service;

import com.padimasso.autocasting.application.admin.dto.request.AdminEmployerProfileUpdateRequest;
import com.padimasso.autocasting.application.admin.dto.response.AdminEmployerProfileResponse;

import java.util.UUID;

public interface AdminEmployerProfileEditService {
    /**
     * Applies every provided section atomically on behalf of an admin and records one history entry
     * (with the admin's reason) listing only what changed.
     */
    AdminEmployerProfileResponse updateEmployerProfile(UUID userId, AdminEmployerProfileUpdateRequest request);
}
