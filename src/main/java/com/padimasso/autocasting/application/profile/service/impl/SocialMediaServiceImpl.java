package com.padimasso.autocasting.application.profile.service.impl;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.service.AuthContext;
import com.padimasso.autocasting.application.profile.dto.request.SocialMediaPatchRequest;
import com.padimasso.autocasting.application.profile.dto.response.SocialMediaResponse;
import com.padimasso.autocasting.application.profile.mapper.ProfileMapper;
import com.padimasso.autocasting.application.profile.model.ProfileEntity;
import com.padimasso.autocasting.application.profile.model.SocialMediaEntity;
import com.padimasso.autocasting.application.profile.repository.ProfileRepository;
import com.padimasso.autocasting.application.profile.repository.SocialMediaRepository;
import com.padimasso.autocasting.application.profile.service.SocialMediaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class SocialMediaServiceImpl implements SocialMediaService {

    private final AuthContext authContext;
    private final ProfileRepository profileRepository;
    private final SocialMediaRepository socialMediaRepository;
    private final ProfileMapper profileMapper;

    @Transactional
    @Override
    public SocialMediaResponse patchMySocialMedia(SocialMediaPatchRequest request) {
        UserEntity user = authContext.getCurrentUserOrThrow();
        ProfileEntity profile = profileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("profile.not_found"));
        SocialMediaEntity socialMedia = socialMediaRepository.findByProfileId(profile.getId())
            .orElseGet(() -> socialMediaRepository.save(SocialMediaEntity.builder().profile(profile).build()));

        if (request.instagramUrl() != null) {
            socialMedia.setInstagramUrl(request.instagramUrl());
        }
        if (request.tikTokUrl() != null) {
            socialMedia.setTikTokUrl(request.tikTokUrl());
        }

        return profileMapper.toSocialMediaResponse(socialMediaRepository.save(socialMedia));
    }
}
