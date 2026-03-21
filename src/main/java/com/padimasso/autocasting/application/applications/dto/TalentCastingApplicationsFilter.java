package com.padimasso.autocasting.application.applications.dto;

import com.padimasso.autocasting.application.applications.repository.order.TalentCastingApplicationsOrderBy;

import java.util.List;
import java.util.UUID;

public record TalentCastingApplicationsFilter(
    UUID talentProfileId,
    String search,
    List<String> castingStatusIdTokens,
    List<String> projectTypeIdTokens,
    List<String> modalityIdTokens,
    TalentCastingApplicationsOrderBy orderBy
) {
}
