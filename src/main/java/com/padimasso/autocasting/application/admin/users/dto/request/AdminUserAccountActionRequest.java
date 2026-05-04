package com.padimasso.autocasting.application.admin.users.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AdminUserAccountActionRequest(
    @NotBlank String note
) {
}
