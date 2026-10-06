package com.padimasso.autocasting.application.talent.service;

import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.dto.request.MediaPatchRequest;
import com.padimasso.autocasting.application.talent.dto.response.MediaResponse;

public interface MediaService {
    MediaResponse patchMyMedia(MediaPatchRequest request);

    MediaResponse patchMedia(TalentProfileEntity profile, MediaPatchRequest request);
}
