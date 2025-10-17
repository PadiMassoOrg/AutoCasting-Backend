package com.padimasso.autocasting.application.talent.service.impl;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.service.AuthContext;
import com.padimasso.autocasting.application.talent.dto.request.MediaPatchRequest;
import com.padimasso.autocasting.application.talent.dto.request.OtherPicturePatch;
import com.padimasso.autocasting.application.talent.dto.response.MediaResponse;
import com.padimasso.autocasting.application.talent.mapper.TalentProfileMapper;
import com.padimasso.autocasting.application.talent.model.MediaEntity;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.repository.MediaRepository;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import com.padimasso.autocasting.application.talent.service.MediaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class MediaServiceImpl implements MediaService {

    private final AuthContext authContext;
    private final TalentProfileRepository talentProfileRepository;
    private final MediaRepository mediaRepository;
    private final TalentProfileMapper talentProfileMapper;

    @Transactional
    @Override
    public MediaResponse patchMyMedia(MediaPatchRequest request) {
        UserEntity user = authContext.getCurrentUserOrThrow();
        TalentProfileEntity profile = talentProfileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("profile.not_found"));
        MediaEntity media = mediaRepository.findByTalentProfileId(profile.getId())
            .orElseGet(() -> mediaRepository.save(MediaEntity.builder().talentProfile(profile).build()));

        if (request.headshotImageUrl().isPresent()) {
            media.setHeadshotImageUrl(request.headshotImageUrl().orElse(null));
        }
        if (request.fullBodyImageUrl().isPresent()) {
            media.setFullBodyImageUrl(request.fullBodyImageUrl().orElse(null));
        }
        if (request.otherPictures() != null) {
            List<String> list = media.getOtherPicturesUrl();
            if (list == null) list = new ArrayList<>();
            for (OtherPicturePatch op : request.otherPictures()) {
                int idx = op.index();
                // expandir con nulls si hace falta
                while (list.size() <= idx) list.add(null);
                list.set(idx, op.url());
            }
            media.setOtherPicturesUrl(list);
        }
        if (request.introductionVideoUrl() != null){
            media.setIntroductionVideoUrl(request.introductionVideoUrl());
        }
        if (request.showReelVideoUrl() != null){
            media.setShowReelVideoUrl(request.showReelVideoUrl());
        }

        return talentProfileMapper.toMediaResponse(mediaRepository.save(media));
    }
}
