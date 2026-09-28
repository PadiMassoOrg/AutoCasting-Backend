package com.padimasso.autocasting.application.admin.mapper;

import com.padimasso.autocasting.application.admin.dto.response.AdminEmployerProfileResponse;
import com.padimasso.autocasting.application.admin.dto.response.AdminTalentProfileResponse;
import com.padimasso.autocasting.application.employer.mapper.EmployerProfileMapper;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.talent.mapper.TalentProfileMapper;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.util.TalentCatalogVisibility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminProfileMapper {

    private final TalentProfileMapper talentProfileMapper;
    private final EmployerProfileMapper employerProfileMapper;

    public AdminTalentProfileResponse toTalentProfileResponse(TalentProfileEntity profile) {
        var sections = talentProfileMapper.toProfileResponse(profile, profile.getUser());

        return new AdminTalentProfileResponse(
            profile.getId(),
            sections.publicSlug(),
            TalentCatalogVisibility.isVisibleInCatalog(profile),
            profile.getUser().getTalentOnboardingStatus(),
            sections.basicInfo(),
            sections.contact(),
            sections.socialMedia(),
            sections.media(),
            sections.characteristics(),
            sections.skills(),
            sections.credits(),
            sections.education(),
            sections.modifiedAt(),
            profile.getCreatedAt(),
            profile.getCreatedBy(),
            profile.getModifiedAt(),
            profile.getModifiedBy()
        );
    }

    public AdminEmployerProfileResponse toEmployerProfileResponse(EmployerProfileEntity profile) {
        return new AdminEmployerProfileResponse(
            profile.getId(),
            profile.getPublicSlug(),
            profile.getUser().getEmployerOnboardingStatus(),
            employerProfileMapper.toBasicInfoResponse(profile.getBasicInfo()),
            profile.getCreatedAt(),
            profile.getCreatedBy(),
            profile.getModifiedAt(),
            profile.getModifiedBy()
        );
    }
}
