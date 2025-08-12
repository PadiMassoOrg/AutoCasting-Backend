package com.padimasso.autocasting.application.profile.service.impl;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.service.AuthContext;
import com.padimasso.autocasting.application.profile.dto.request.MediaPatchRequest;
import com.padimasso.autocasting.application.profile.dto.response.MediaResponse;
import com.padimasso.autocasting.application.profile.mapper.ProfileMapper;
import com.padimasso.autocasting.application.profile.model.MediaEntity;
import com.padimasso.autocasting.application.profile.model.ProfileEntity;
import com.padimasso.autocasting.application.profile.repository.MediaRepository;
import com.padimasso.autocasting.application.profile.repository.ProfileRepository;
import com.padimasso.autocasting.application.profile.service.MediaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class MediaServiceImpl implements MediaService {

    private final AuthContext authContext;
    private final ProfileRepository profileRepository;
    private final MediaRepository mediaRepository;
    private final ProfileMapper profileMapper;

    @Transactional
    @Override
    public MediaResponse patchMyMedia(MediaPatchRequest request) {
        UserEntity user = authContext.getCurrentUserOrThrow();
        ProfileEntity profile = profileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("profile.not_found"));
        MediaEntity media = mediaRepository.findByProfileId(profile.getId())
            .orElseGet(() -> mediaRepository.save(MediaEntity.builder().profile(profile).build()));

        if (request.headshotImageUrl() != null){
            media.setHeadshotImageUrl(request.headshotImageUrl());
        }
        if (request.fullBodyImageUrl() != null){
            media.setFullBodyImageUrl(request.fullBodyImageUrl());
        }
        if (request.otherPicturesUrl() != null) {
            Set<String> urls = request.otherPicturesUrl();
            if (urls.isEmpty()) {
                media.getOtherPicturesUrl().clear();
            } else {
                media.setOtherPicturesUrl(urls);
            }
        }
        if (request.introductionVideoUrl() != null){
            media.setIntroductionVideoUrl(request.introductionVideoUrl());
        }
        if (request.showReelVideoUrl() != null){
            media.setShowReelVideoUrl(request.showReelVideoUrl());
        }

        return profileMapper.toMediaResponse(mediaRepository.save(media));
    }
}
