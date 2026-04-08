package com.padimasso.autocasting.application.talent.service.impl;

import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.employer.model.EmployerBasicInfoEntity;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.employer.repository.EmployerProfileRepository;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import com.padimasso.autocasting.application.shared.util.TextNormalizer;
import com.padimasso.autocasting.application.talent.dto.request.SocialMediaLinkDto;
import com.padimasso.autocasting.application.talent.dto.request.SocialMediaPatchRequest;
import com.padimasso.autocasting.application.talent.dto.response.SocialMediaResponse;
import com.padimasso.autocasting.application.talent.mapper.TalentProfileMapper;
import com.padimasso.autocasting.application.talent.model.ProfileSocialMediaLinkEntity;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.repository.ProfileSocialMediaLinkRepository;
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
    private final EmployerProfileRepository employerProfileRepository;
    private final ProfileSocialMediaLinkRepository linkRepository;
    private final SiteMetadataResolver siteMetadataResolver;

    @Transactional
    @Override
    public SocialMediaResponse patchMySocialMedia(SocialMediaPatchRequest request) {
        UserEntity user = authContext.getCurrentUserOrThrow();

        TalentProfileEntity profile = talentProfileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("profile.not_found"));

        if (request.links() != null) {
            for (SocialMediaLinkDto rawDto : request.links()) {
                if (rawDto == null || rawDto.optionId() == null) {
                    continue;
                }

                String normalizedUrl = normalizeUrl(rawDto.url());

                var existingIncl = linkRepository.findIncludingDeletedByTalentProfileIdAndOptionId(
                    profile.getId(),
                    rawDto.optionId()
                );

                if (normalizedUrl == null) {
                    // "delete": marcamos deleted=true si existe y está activa
                    existingIncl.ifPresent(linkRepository::softDelete);
                } else {
                    // "upsert": reusamos si existe, incluso si estaba deleted
                    ProfileSocialMediaLinkEntity entity = existingIncl.orElseGet(() -> {
                        var option = siteMetadataResolver.resolveSocialMediaOptionOrThrow(rawDto.optionId());
                        return ProfileSocialMediaLinkEntity.builder()
                            .talentProfile(profile)
                            .option(option)
                            .build();
                    });

                    // Si estaba borrada, la restauramos
                    entity.setDeleted(false);
                    entity.setUrl(normalizedUrl);
                    linkRepository.save(entity);
                }
            }
        }

        // 👇 aquí usamos SOLO activos (deleted = false)
        var allLinks = linkRepository.findAllByTalentProfileId(profile.getId());
        return TalentProfileMapper.toSocialMediaResponse(allLinks.stream().toList());
    }

    @Transactional
    @Override
    public SocialMediaResponse patchMyEmployerSocialMedia(SocialMediaPatchRequest request) {
        UserEntity user = authContext.getCurrentUserOrThrow();

        EmployerProfileEntity profile = employerProfileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("profile.not_found"));

        EmployerBasicInfoEntity basicInfo = profile.getBasicInfo();

        if (request.links() != null) {
            for (SocialMediaLinkDto rawDto : request.links()) {
                if (rawDto == null || rawDto.optionId() == null) {
                    continue;
                }

                String normalizedUrl = normalizeUrl(rawDto.url());

                var existingIncl = linkRepository.findIncludingDeletedByEmployerBasicInfoIdAndOptionId(
                    basicInfo.getId(),
                    rawDto.optionId()
                );

                if (normalizedUrl == null) {
                    // "delete": marcamos deleted=true si existe y está activa
                    existingIncl.ifPresent(linkRepository::softDelete);
                } else {
                    // "upsert": reusamos si existe, incluso si estaba deleted
                    ProfileSocialMediaLinkEntity entity = existingIncl.orElseGet(() -> {
                        var option = siteMetadataResolver.resolveSocialMediaOptionOrThrow(rawDto.optionId());
                        return ProfileSocialMediaLinkEntity.builder()
                            .employerBasicInfo(basicInfo)
                            .option(option)
                            .build();
                    });

                    // Si estaba borrada, la restauramos
                    entity.setDeleted(false);
                    entity.setUrl(normalizedUrl);
                    linkRepository.save(entity);
                }
            }
        }

        // 👇 aquí usamos SOLO activos (deleted = false)
        var allLinks = linkRepository.findAllByEmployerBasicInfoId(basicInfo.getId());
        return TalentProfileMapper.toSocialMediaResponse(allLinks.stream().toList());
    }

    private String normalizeUrl(String value) {
        return TextNormalizer.normalizeNullable(value);
    }
}
