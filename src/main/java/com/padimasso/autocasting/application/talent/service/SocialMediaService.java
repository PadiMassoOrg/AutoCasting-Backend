package com.padimasso.autocasting.application.talent.service;

import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.dto.request.SocialMediaPatchRequest;
import com.padimasso.autocasting.application.talent.dto.response.SocialMediaResponse;

public interface SocialMediaService {
    
    SocialMediaResponse patchMySocialMedia(SocialMediaPatchRequest request);

    SocialMediaResponse patchSocialMedia(TalentProfileEntity profile, SocialMediaPatchRequest request);

    SocialMediaResponse patchMyEmployerSocialMedia(SocialMediaPatchRequest request);

}
