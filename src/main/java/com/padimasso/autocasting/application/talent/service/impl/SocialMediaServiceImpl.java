package com.padimasso.autocasting.application.talent.service.impl;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.service.AuthContext;
import com.padimasso.autocasting.application.talent.dto.request.SocialMediaPatchRequest;
import com.padimasso.autocasting.application.talent.dto.response.SocialMediaResponse;
import com.padimasso.autocasting.application.talent.mapper.TalentProfileMapper;
import com.padimasso.autocasting.application.talent.model.SocialMediaEntity;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.repository.SocialMediaRepository;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import com.padimasso.autocasting.application.talent.service.SocialMediaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class SocialMediaServiceImpl implements SocialMediaService {

    private final AuthContext authContext;
    private final TalentProfileRepository talentProfileRepository;
    private final SocialMediaRepository socialMediaRepository;
    private final TalentProfileMapper talentProfileMapper;

    @Transactional
    @Override
    public SocialMediaResponse patchMySocialMedia(SocialMediaPatchRequest request) {
        UserEntity user = authContext.getCurrentUserOrThrow();

        TalentProfileEntity profile = talentProfileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("profile.not_found"));

        SocialMediaEntity socialMedia = socialMediaRepository.findByTalentProfileId(profile.getId())
            .orElseGet(() -> socialMediaRepository.save(
                SocialMediaEntity.builder().talentProfile(profile).build()
            ));

        // Instagram
        if (request.instagramUrl() != null) {
            socialMedia.setInstagramUrl(normalizeUrl(request.instagramUrl()));
        }

        // TikTok
        if (request.tikTokUrl() != null) {
            socialMedia.setTikTokUrl(normalizeUrl(request.tikTokUrl()));
        }

        // LinkedIn
        if (request.linkedinUrl() != null) {
            socialMedia.setLinkedinUrl(normalizeUrl(request.linkedinUrl()));
        }

        // X (Twitter)
        if (request.xUrl() != null) {
            socialMedia.setXUrl(normalizeUrl(request.xUrl()));
        }

        // Vimeo
        if (request.vimeoUrl() != null) {
            socialMedia.setVimeoUrl(normalizeUrl(request.vimeoUrl()));
        }

        // IMDb
        if (request.imdbUrl() != null) {
            socialMedia.setImdbUrl(normalizeUrl(request.imdbUrl()));
        }

        // Behance
        if (request.behanceUrl() != null) {
            socialMedia.setBehanceUrl(normalizeUrl(request.behanceUrl()));
        }

        return talentProfileMapper.toSocialMediaResponse(socialMediaRepository.save(socialMedia));
    }

    /**
     * Si el valor es null o vacío/espacios → devuelve null (borrar en DB).
     * Si tiene contenido → lo devuelve trimeado.
     */
    private String normalizeUrl(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
