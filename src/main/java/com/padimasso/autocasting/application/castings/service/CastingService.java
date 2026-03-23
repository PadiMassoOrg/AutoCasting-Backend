package com.padimasso.autocasting.application.castings.service;

import com.padimasso.autocasting.application.castings.dto.EmployerCastingsFilter;
import com.padimasso.autocasting.application.castings.dto.response.CastingResponse;
import com.padimasso.autocasting.application.castings.dto.response.EmployerCastingEditorResponse;
import com.padimasso.autocasting.application.castings.dto.response.PublicCastingDetailsResponse;
import com.padimasso.autocasting.application.castings.dto.response.PublicCastingOverviewResponse;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingCardResponse;

import java.util.List;
import java.util.UUID;

public interface CastingService {
    String createEmptyCasting();

    CastingResponse getEmployerCastingDetailsBySlug(String slug);

    List<CastingCardResponse> getMyCastings(EmployerCastingsFilter filter, int page, int size);

    EmployerCastingEditorResponse getCastingEditorBySlug(String slug);

    void deleteCasting(UUID castingId);

    EmployerCastingEditorResponse publishCasting(UUID castingId);

    EmployerCastingEditorResponse setDraftCasting(UUID castingId);

    EmployerCastingEditorResponse pauseCasting(UUID castingId);

    EmployerCastingEditorResponse closeCasting(UUID castingId);

    EmployerCastingEditorResponse archiveCasting(UUID castingId);

    PublicCastingDetailsResponse getPublicCastingDetailsBySlugAndRoleId(String slug, UUID roleId);

    PublicCastingOverviewResponse getPublicCastingDetailsBySlug(String slug);
}
