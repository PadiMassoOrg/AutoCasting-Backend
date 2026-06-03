package com.padimasso.autocasting.application.talent.service.impl;

import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.talent.dto.request.MediaPatchRequest;
import com.padimasso.autocasting.application.talent.dto.request.OtherPicturePatch;
import com.padimasso.autocasting.application.talent.dto.response.MediaResponse;
import com.padimasso.autocasting.application.talent.mapper.TalentProfileMapper;
import com.padimasso.autocasting.application.talent.model.MediaEntity;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.repository.MediaRepository;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import com.padimasso.autocasting.application.talent.service.MediaService;
import com.padimasso.autocasting.application.shared.util.TextNormalizer;
import com.padimasso.autocasting.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.padimasso.autocasting.exception.ErrorMessageKeys.PROFILE_NOT_FOUND;

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
            .orElseThrow(() -> new IllegalArgumentException(PROFILE_NOT_FOUND));
        MediaEntity media = mediaRepository.findByTalentProfileId(profile.getId())
            .orElseGet(() -> mediaRepository.save(MediaEntity.builder().talentProfile(profile).build()));

        if (wouldLeaveWithoutPhotos(media, request)) {
            throw ApiException.badRequest("profile.media.must_have_one_photo");
        }

        if (request.headshotImageUrl().isPresent()) {
            media.setHeadshotImageUrl(TextNormalizer.normalizeNullable(request.headshotImageUrl().orElse(null)));
        }
        if (request.fullBodyImageUrl().isPresent()) {
            media.setFullBodyImageUrl(TextNormalizer.normalizeNullable(request.fullBodyImageUrl().orElse(null)));
        }
        if (request.otherPictures() != null) {
            List<String> list = media.getOtherPicturesUrl();
            if (list == null) list = new ArrayList<>();
            for (OtherPicturePatch op : request.otherPictures()) {
                int idx = op.index();
                // expandir con nulls si hace falta
                while (list.size() <= idx) list.add(null);
                list.set(idx, TextNormalizer.normalizeNullable(op.url()));
            }
            media.setOtherPicturesUrl(list);
        }
        if (request.introductionVideoUrl().isPresent()) {
            media.setIntroductionVideoUrl(TextNormalizer.normalizeNullable(request.introductionVideoUrl().orElse(null)));
        }
        if (request.showReelVideoUrl().isPresent()) {
            media.setShowReelVideoUrl(TextNormalizer.normalizeNullable(request.showReelVideoUrl().orElse(null)));
        }

        return talentProfileMapper.toMediaResponse(mediaRepository.saveAndFlush(media));
    }

    private boolean wouldLeaveWithoutPhotos(MediaEntity media, MediaPatchRequest request) {
        boolean hasHeadshot = request.headshotImageUrl().isPresent()
            ? TextNormalizer.normalizeNullable(request.headshotImageUrl().orElse(null)) != null
            : TextNormalizer.normalizeNullable(media.getHeadshotImageUrl()) != null;
        boolean hasFullBody = request.fullBodyImageUrl().isPresent()
            ? TextNormalizer.normalizeNullable(request.fullBodyImageUrl().orElse(null)) != null
            : TextNormalizer.normalizeNullable(media.getFullBodyImageUrl()) != null;

        List<String> otherPictures = media.getOtherPicturesUrl() == null
            ? new ArrayList<>()
            : new ArrayList<>(media.getOtherPicturesUrl());
        if (request.otherPictures() != null) {
            for (OtherPicturePatch otherPicture : request.otherPictures()) {
                while (otherPictures.size() <= otherPicture.index()) {
                    otherPictures.add(null);
                }
                otherPictures.set(otherPicture.index(), TextNormalizer.normalizeNullable(otherPicture.url()));
            }
        }

        boolean hasOtherPicture = otherPictures.stream().anyMatch(url -> TextNormalizer.normalizeNullable(url) != null);
        return !hasHeadshot && !hasFullBody && !hasOtherPicture;
    }
}
