package com.padimasso.autocasting.application.castings.service;

import com.padimasso.autocasting.application.castings.dto.CastingRoleFilter;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingRolePublicCardResponse;
import com.padimasso.autocasting.application.shared.web.SliceResponse;

public interface CastingRoleSearchService {
    SliceResponse<CastingRolePublicCardResponse> search(CastingRoleFilter filter, int page, int size);
}
