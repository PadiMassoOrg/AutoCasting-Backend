package com.padimasso.autocasting.application.talent.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

public record SkillsPatchRequest(
    Set<@NotNull(message = "talent.skills_required") UUID> skillIds
) {
}
