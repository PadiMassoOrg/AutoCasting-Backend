package com.padimasso.autocasting.application.profile.dto.request;

import java.util.Set;
import java.util.UUID;

public record SkillsPatchRequest(
    Set<UUID> skillIds
) {
}
