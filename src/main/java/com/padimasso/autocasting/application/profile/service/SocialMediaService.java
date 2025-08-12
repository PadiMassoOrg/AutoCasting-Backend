package com.padimasso.autocasting.application.profile.service;

import com.padimasso.autocasting.application.profile.dto.request.SocialMediaPatchRequest;
import com.padimasso.autocasting.application.profile.dto.response.SocialMediaResponse;

public interface SocialMediaService {
    SocialMediaResponse patchMySocialMedia(SocialMediaPatchRequest request);
}
