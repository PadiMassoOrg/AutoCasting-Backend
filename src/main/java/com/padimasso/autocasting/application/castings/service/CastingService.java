package com.padimasso.autocasting.application.castings.service;

import com.padimasso.autocasting.application.castings.dto.response.CastingCardResponse;
import com.padimasso.autocasting.application.castings.dto.response.CastingResponse;

import java.util.List;

public interface CastingService {
    String createEmptyCasting();

    CastingResponse getDetailsBySlug(String slug);

    List<CastingCardResponse> getMyCastings();

    List<CastingCardResponse> getCastingsCards();
}
