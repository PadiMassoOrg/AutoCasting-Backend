package com.padimasso.autocasting.application.talent.service;

import com.padimasso.autocasting.application.shared.web.SliceResponse;
import com.padimasso.autocasting.application.talent.dto.TalentFilter;
import com.padimasso.autocasting.application.talent.dto.response.TalentCardResponse;

public interface TalentSearchService {

    SliceResponse<TalentCardResponse> search(TalentFilter filter, int page, int size);
}
