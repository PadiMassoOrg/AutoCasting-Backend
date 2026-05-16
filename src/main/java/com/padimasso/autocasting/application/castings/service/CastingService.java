package com.padimasso.autocasting.application.castings.service;

import com.padimasso.autocasting.application.castings.dto.EmployerCastingsFilter;
import com.padimasso.autocasting.application.castings.dto.request.CastingUpsertRequest;
import com.padimasso.autocasting.application.castings.dto.response.*;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingCardResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface CastingService {
    String createEmptyCasting();

    CastingResponse createCasting(@Valid CastingUpsertRequest request);

    CastingResponse updateCasting(UUID castingId, @Valid CastingUpsertRequest request);

    EmployerCastingEditorResponse getCastingEditorBySlug(String slug);

    CastingResponse getEmployerCastingDetailsBySlug(String slug);

    EmployerCastingCheckoutSummaryResponse getEmployerCastingCheckoutSummary(UUID castingId);

    List<CastingCardResponse> getMyCastings(EmployerCastingsFilter filter, int page, int size);

    void deleteCasting(UUID castingId);

    EmployerCastingEditorResponse publishCasting(UUID castingId);

    EmployerCastingEditorResponse setDraftCasting(UUID castingId);

    EmployerCastingEditorResponse pauseCasting(UUID castingId);

    EmployerCastingEditorResponse closeCasting(UUID castingId);

    EmployerCastingEditorResponse archiveCasting(UUID castingId);

    PublicCastingDetailsResponse getPublicCastingDetailsBySlugAndRoleId(String slug, UUID roleId);

    PublicCastingOverviewResponse getPublicCastingDetailsBySlug(String slug);
}
