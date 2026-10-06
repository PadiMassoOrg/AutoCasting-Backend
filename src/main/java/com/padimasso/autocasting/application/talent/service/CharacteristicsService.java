package com.padimasso.autocasting.application.talent.service;

import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.dto.request.CharacteristicsPatchRequest;
import com.padimasso.autocasting.application.talent.dto.response.CharacteristicsResponse;

public interface CharacteristicsService {
    CharacteristicsResponse patchMyCharacteristics(CharacteristicsPatchRequest request);

    CharacteristicsResponse patchCharacteristics(TalentProfileEntity profile, CharacteristicsPatchRequest request);
}
