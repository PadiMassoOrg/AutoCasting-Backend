package com.padimasso.autocasting.application.admin.service;

import com.padimasso.autocasting.application.admin.dto.request.AdminCastingRoleCreateRequest;
import com.padimasso.autocasting.application.admin.dto.request.AdminCastingRoleDeleteRequest;
import com.padimasso.autocasting.application.admin.dto.request.AdminCastingRoleDuplicateRequest;
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

    /**
     * Adds a role to a draft, published or paused casting and records one history entry. The role has no
     * reference photo: the Admin form does not manage photos.
     */
    CastingRoleResponse createCastingRole(AdminCastingRoleCreateRequest request);

    /** Copies a role (without its reference photo) inside its casting and records one history entry. */
    CastingRoleResponse duplicateCastingRole(UUID roleId, AdminCastingRoleDuplicateRequest request);

    /**
     * Soft-deletes a role and records one history entry with the applications it had. Those applications are
     * kept untouched: talents still see them, with the casting reported as closed. Rejected when the role has a
     * selected application, or when it is the last role of a published or paused casting.
     */
    void deleteCastingRole(UUID roleId, AdminCastingRoleDeleteRequest request);
}
