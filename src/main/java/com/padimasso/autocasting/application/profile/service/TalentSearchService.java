package com.padimasso.autocasting.application.profile.service;

import com.padimasso.autocasting.application.profile.dto.TalentFilter;
import com.padimasso.autocasting.application.profile.dto.response.ProfileCardResponse;
import com.padimasso.autocasting.application.shared.web.SliceResponse;

public interface TalentSearchService {

    SliceResponse<ProfileCardResponse> search(TalentFilter filter, int page, int size);
}
