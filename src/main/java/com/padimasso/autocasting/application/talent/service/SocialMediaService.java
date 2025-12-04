package com.padimasso.autocasting.application.talent.service;

import com.padimasso.autocasting.application.talent.dto.request.SocialMediaPatchRequest;
import com.padimasso.autocasting.application.talent.dto.response.SocialMediaResponse;

public interface SocialMediaService {
    
    SocialMediaResponse patchMySocialMedia(SocialMediaPatchRequest request);

    SocialMediaResponse patchMyEmployerSocialMedia(SocialMediaPatchRequest request);

}
