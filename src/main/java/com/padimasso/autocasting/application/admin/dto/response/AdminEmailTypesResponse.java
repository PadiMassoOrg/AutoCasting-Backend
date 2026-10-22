package com.padimasso.autocasting.application.admin.dto.response;

import com.padimasso.autocasting.application.email.model.EmailAudience;
import com.padimasso.autocasting.application.email.model.EmailType;

import java.util.List;

public record AdminEmailTypesResponse(
    List<Item> types,
    int resendCooldownDays
) {
    public record Item(
        EmailType type,
        EmailAudience audience
    ) {
    }
}
