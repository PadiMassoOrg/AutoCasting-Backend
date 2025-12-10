package com.padimasso.autocasting.application.castings.service;

import com.padimasso.autocasting.application.castings.dto.response.CastingResponse;

public interface CastingService {
    String createEmptyCasting();

    CastingResponse getDetailsBySlug(String slug);
}
