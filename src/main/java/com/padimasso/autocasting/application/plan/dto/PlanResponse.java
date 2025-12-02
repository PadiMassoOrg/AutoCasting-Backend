package com.padimasso.autocasting.application.plan.dto;

import java.util.UUID;

public record PlanResponse(
    UUID id,
    String code,
    String nameStringCode,
    String description,
    boolean allowsCustomSlug
) {
}
