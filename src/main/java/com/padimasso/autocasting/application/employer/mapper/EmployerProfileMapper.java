package com.padimasso.autocasting.application.employer.mapper;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.employer.dto.response.EmployerBasicInfoResponse;
import com.padimasso.autocasting.application.employer.dto.response.EmployerProfileResponse;
import com.padimasso.autocasting.application.employer.model.EmployerBasicInfoEntity;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.talent.repository.ProfileSocialMediaLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.padimasso.autocasting.application.talent.mapper.TalentProfileMapper.mapToSiteMetadataObject;
import static com.padimasso.autocasting.application.talent.mapper.TalentProfileMapper.toSocialMediaResponse;

@Component
@RequiredArgsConstructor
public class EmployerProfileMapper {

    private final ProfileSocialMediaLinkRepository socialMediaLinkRepository;

    public EmployerProfileResponse toProfileResponse(EmployerProfileEntity profile, UserEntity user) {
        return new EmployerProfileResponse(
            profile.getId(),
            user.getUserAccountProvider().toString(),
            profile.getPublicSlug(),
            profile.getPlan().getNameStringCode(),
            toBasicInfoResponse(profile.getBasicInfo())
        );
    }

    public EmployerBasicInfoResponse toBasicInfoResponse(EmployerBasicInfoEntity entity) {
        if (entity == null) return null;

        var links = socialMediaLinkRepository.findAllByEmployerBasicInfoId(entity.getId())
            .stream()
            .toList();

        return new EmployerBasicInfoResponse(
            entity.getId(),
            entity.getCompanyName(),
            entity.getTaxNumber(),
            mapToSiteMetadataObject(entity.getCompanyType()),
            entity.getCompanyEmail(),
            entity.getImageUrl(),
            entity.getAddress(),
            entity.getWebsiteUrl(),
            entity.getAbout(),
            toSocialMediaResponse(links));
    }
}
