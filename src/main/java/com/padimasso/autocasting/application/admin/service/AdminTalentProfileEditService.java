package com.padimasso.autocasting.application.admin.service;

import com.padimasso.autocasting.application.admin.dto.request.AdminTalentProfileUpdateRequest;
import com.padimasso.autocasting.application.admin.dto.response.AdminTalentProfileResponse;

import java.util.UUID;

public interface AdminTalentProfileEditService {
    /**
     * Applies every provided section atomically on behalf of an admin and records one history entry
     * (with the admin's reason) listing the sections changed.
     */
    AdminTalentProfileResponse updateTalentProfile(UUID userId, AdminTalentProfileUpdateRequest request);
}
