package com.padimasso.autocasting.application.auth.dto.response;

import java.util.UUID;

public record RoleResponse(
    UUID id,
    String code,
    String nameStringCode,
    String description
) {
}
