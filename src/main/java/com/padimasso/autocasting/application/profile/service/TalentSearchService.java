package com.padimasso.autocasting.application.profile.service;

import com.padimasso.autocasting.application.profile.dto.response.ProfileCardResponse;
import com.padimasso.autocasting.application.shared.web.SliceResponse;

public interface TalentSearchService {


    SliceResponse<ProfileCardResponse> listCards(int page, int size);
}
