package com.padimasso.autocasting.application.castings.service;

import com.padimasso.autocasting.application.castings.dto.EmployerCastingsFilter;
import com.padimasso.autocasting.application.castings.dto.response.CastingResponse;
import com.padimasso.autocasting.application.castings.dto.response.EmployerCastingResponse;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingCardResponse;

import java.util.List;

public interface CastingService {
    String createEmptyCasting();

    CastingResponse getDetailsBySlug(String slug);

    List<CastingCardResponse> getMyCastings(EmployerCastingsFilter filter, int page, int size);

    EmployerCastingResponse getDetailsForEmployerBySlug(String slug);
}
