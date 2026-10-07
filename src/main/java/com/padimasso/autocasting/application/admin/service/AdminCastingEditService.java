package com.padimasso.autocasting.application.admin.service;

import com.padimasso.autocasting.application.admin.dto.request.AdminCastingRoleUpdateRequest;
import com.padimasso.autocasting.application.admin.dto.request.AdminCastingUpdateRequest;
import com.padimasso.autocasting.application.admin.dto.response.AdminCastingDetailsResponse;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleResponse;

import java.util.UUID;

public interface AdminCastingEditService {
    /**
     * Replaces the casting's basic info and records one history entry
     * (with the admin's reason) listing only what changed.
     * Only draft, published and paused castings are editable; closed and archived ones are rejected.
     */
    AdminCastingDetailsResponse updateCasting(UUID castingId, AdminCastingUpdateRequest request);

    /**
     * Replaces a role of a casting and records one history entry
     * (with the admin's reason) listing only what changed. The role's reference photo is left untouched.
     */
    CastingRoleResponse updateCastingRole(UUID roleId, AdminCastingRoleUpdateRequest request);
}
