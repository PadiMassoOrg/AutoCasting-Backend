package com.padimasso.autocasting.application.castings.service;

import com.padimasso.autocasting.application.castings.dto.EmployerCastingsFilter;
import com.padimasso.autocasting.application.castings.dto.response.CastingResponse;
import com.padimasso.autocasting.application.castings.dto.response.EmployerCastingResponse;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingCardResponse;

import java.util.List;
import java.util.UUID;

public interface CastingService {
    String createEmptyCasting();

    CastingResponse getDetailsBySlug(String slug);

    List<CastingCardResponse> getMyCastings(EmployerCastingsFilter filter, int page, int size);

    EmployerCastingResponse getDetailsForEmployerBySlug(String slug);

    void deleteCasting(UUID castingId);

    //    Status
    EmployerCastingResponse publishCasting(UUID castingId);

    EmployerCastingResponse setDraftCasting(UUID castingId);

    EmployerCastingResponse pauseCasting(UUID castingId);

    EmployerCastingResponse closeCasting(UUID castingId);

    EmployerCastingResponse archiveCasting(UUID castingId);
}
