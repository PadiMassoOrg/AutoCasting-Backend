package com.padimasso.autocasting.application.profile.service;

import com.padimasso.autocasting.application.profile.dto.request.CharacteristicsPatchRequest;
import com.padimasso.autocasting.application.profile.dto.response.CharacteristicsResponse;

public interface CharacteristicsService {
    CharacteristicsResponse patchMyCharacteristics(CharacteristicsPatchRequest request);
}
