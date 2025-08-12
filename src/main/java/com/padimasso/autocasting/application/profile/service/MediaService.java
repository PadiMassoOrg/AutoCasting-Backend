package com.padimasso.autocasting.application.profile.service;

import com.padimasso.autocasting.application.profile.dto.request.MediaPatchRequest;
import com.padimasso.autocasting.application.profile.dto.response.MediaResponse;

public interface MediaService {
    MediaResponse patchMyMedia(MediaPatchRequest request);
}
