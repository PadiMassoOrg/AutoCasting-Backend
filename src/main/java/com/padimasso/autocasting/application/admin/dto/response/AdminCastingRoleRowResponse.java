package com.padimasso.autocasting.application.admin.dto.response;

import java.util.UUID;

public record AdminCastingRoleRowResponse(
    UUID id,
    String title
) {
}
