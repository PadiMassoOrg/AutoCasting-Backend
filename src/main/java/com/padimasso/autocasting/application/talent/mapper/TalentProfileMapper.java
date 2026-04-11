package com.padimasso.autocasting.application.talent.mapper;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import com.padimasso.autocasting.application.sitemetadata.model.SiteMetadataBase;
import com.padimasso.autocasting.application.talent.dto.response.*;
import com.padimasso.autocasting.application.talent.model.*;
import com.padimasso.autocasting.application.talent.repository.ProfileSocialMediaLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TalentProfileMapper {

    private final ProfileSocialMediaLinkRepository socialMediaLinkRepository;

    public TalentProfileResponse toProfileResponse(TalentProfileEntity profile, UserEntity user) {
        var links = socialMediaLinkRepository.findAllByTalentProfileId(profile.getId())
            .stream()
            .toList();

        return new TalentProfileResponse(
            profile.getId(),
            user.getUserAccountProvider().toString(),
            profile.getPlan().getNameStringCode(),
            profile.getPublicSlug(),
            toBasicInfoResponse(profile.getBasicInfo()),
            toContactResponse(profile.getContact()),
            toSocialMediaResponse(links, maxModifiedAt(profile.getModifiedAt(), maxLinksModifiedAt(links))),
            toMediaResponse(profile.getMedia()),
            toCharacteristicsResponse(profile.getCharacteristics()),
            mapToSiteMetadataObjectList(profile.getSkills()),
            profile.getCredits().stream().map(this::toCreditResponse).collect(Collectors.toSet()),
            profile.getEducation().stream().map(this::toEducationResponse).collect(Collectors.toSet()),
            resolveProfileModifiedAt(profile, links)
        );
    }

    public PublicProfileResponse toPublicProfileResponse(TalentProfileEntity profile) {
        var links = socialMediaLinkRepository.findAllByTalentProfileId(profile.getId())
            .stream()
            .toList();

        return new PublicProfileResponse(
            profile.getId(),
            profile.getPlan().getNameStringCode(),
            profile.getPublicSlug(),
            toBasicInfoResponse(profile.getBasicInfo()),
            toContactResponse(profile.getContact()),
            toSocialMediaResponse(links, maxModifiedAt(profile.getModifiedAt(), maxLinksModifiedAt(links))),
            toMediaResponse(profile.getMedia()),
            toCharacteristicsResponse(profile.getCharacteristics()),
            mapToSiteMetadataObjectList(profile.getSkills()),
            profile.getCredits().stream().map(this::toCreditResponse).collect(Collectors.toSet()),
            profile.getEducation().stream().map(this::toEducationResponse).collect(Collectors.toSet())
        );
    }

    public static <T extends SiteMetadataBase> SiteMetadataObject mapToSiteMetadataObject(T entity) {
        if (entity == null) {
            return null;
        }
        return new SiteMetadataObject(entity.getId(), entity.getStringCode(), entity.getCategoryStringCode());
    }

    public ContactResponse toContactResponse(ContactEntity entity) {
        if (entity == null) return null;
        return new ContactResponse(
            entity.getId(),
            entity.getEmail(),
            entity.getPhoneNumber(),
            entity.getModifiedAt()
        );
    }

    public MediaResponse toMediaResponse(MediaEntity entity) {
        if (entity == null) return null;
        return new MediaResponse(
            entity.getId(),
            entity.getHeadshotImageUrl(),
            entity.getFullBodyImageUrl(),
            entity.getOtherPicturesUrl(),
            entity.getIntroductionVideoUrl(),
            entity.getShowReelVideoUrl(),
            entity.getModifiedAt()
        );
    }

    public CharacteristicsResponse toCharacteristicsResponse(CharacteristicsEntity entity) {
        if (entity == null) return null;
        return new CharacteristicsResponse(
            entity.getId(),
            entity.getHeightCm(),
            mapToSiteMetadataObject(entity.getEthnicity()),
            entity.getWeightKg(),
            mapToSiteMetadataObject(entity.getHairColor()),
            mapToSiteMetadataObject(entity.getEyeColor()),
            entity.getChestCm(),
            entity.getWaistCm(),
            entity.getHipCm(),
            entity.getShirtSize(),
            entity.getPantSize(),
            entity.getDressSize(),
            entity.getShoeSize(),
            entity.isTattoo(),
            entity.isPassport(),
            entity.isDrivingLicense(),
            mapToSiteMetadataObject(entity.getDietOption()),
            entity.getModifiedAt()
        );
    }

    public CreditResponse toCreditResponse(CreditEntity entity) {
        if (entity == null) return null;
        return new CreditResponse(
            entity.getId(),
            mapToSiteMetadataObject(entity.getProductionType()),
            entity.getProjectName(),
            entity.getProducerName(),
            entity.getRole(),
            entity.getYear(),
            entity.getModifiedAt()
        );
    }

    public EducationResponse toEducationResponse(EducationEntity entity) {
        if (entity == null) return null;
        return new EducationResponse(
            entity.getId(),
            entity.getInstitution(),
            entity.getCourseName(),
            entity.getGraduationYear(),
            entity.getModifiedAt()
        );
    }

    //    STATIC METHODS
    public static <T extends SiteMetadataBase> Set<SiteMetadataObject> mapToSiteMetadataObjectList(Set<T> entities) {
        return entities.stream()
            .map(TalentProfileMapper::mapToSiteMetadataObject)
            .collect(Collectors.toSet());
    }

    public static SocialMediaResponse toSocialMediaResponse(List<ProfileSocialMediaLinkEntity> links) {
        return toSocialMediaResponse(links, maxLinksModifiedAt(links));
    }

    public static SocialMediaResponse toSocialMediaResponse(
        List<ProfileSocialMediaLinkEntity> links,
        LocalDateTime modifiedAt
    ) {
        var items = links.stream()
            .map(l -> new SocialMediaLinkResponse(
                l.getOption().getId(),
                l.getOption().getStringCode(),
                l.getUrl()
            ))
            .toList();
        return new SocialMediaResponse(items, modifiedAt);
    }

    public BasicInfoResponse toBasicInfoResponse(BasicInfoEntity entity) {
        if (entity == null) return null;
        return new BasicInfoResponse(
            entity.getId(),
            entity.getStageName(),
            mapToSiteMetadataObject(entity.getGender()),
            entity.getBirthDate(),
            entity.getProfessions().stream().map(TalentProfileMapper::mapToSiteMetadataObject).toList(),
            entity.getModifiedAt()
        );
    }

    private LocalDateTime resolveProfileModifiedAt(
        TalentProfileEntity profile,
        List<ProfileSocialMediaLinkEntity> links
    ) {
        LocalDateTime modifiedAt = maxModifiedAt(
            profile.getModifiedAt(),
            profile.getBasicInfo() != null ? profile.getBasicInfo().getModifiedAt() : null,
            profile.getContact() != null ? profile.getContact().getModifiedAt() : null,
            profile.getMedia() != null ? profile.getMedia().getModifiedAt() : null,
            profile.getCharacteristics() != null ? profile.getCharacteristics().getModifiedAt() : null
        );

        if (profile.getCredits() != null) {
            for (CreditEntity credit : profile.getCredits()) {
                modifiedAt = maxModifiedAt(modifiedAt, credit.getModifiedAt());
            }
        }

        if (profile.getEducation() != null) {
            for (EducationEntity education : profile.getEducation()) {
                modifiedAt = maxModifiedAt(modifiedAt, education.getModifiedAt());
            }
        }

        if (links != null) {
            for (ProfileSocialMediaLinkEntity link : links) {
                modifiedAt = maxModifiedAt(modifiedAt, link.getModifiedAt());
            }
        }

        return modifiedAt;
    }

    private LocalDateTime maxModifiedAt(LocalDateTime... values) {
        return maxModifiedAtValues(values);
    }

    private static LocalDateTime maxLinksModifiedAt(List<ProfileSocialMediaLinkEntity> links) {
        if (links == null) return null;
        LocalDateTime max = null;
        for (ProfileSocialMediaLinkEntity link : links) {
            if (link != null) {
                max = maxModifiedAtValues(max, link.getModifiedAt());
            }
        }
        return max;
    }

    private static LocalDateTime maxModifiedAtValues(LocalDateTime... values) {
        LocalDateTime max = null;
        for (LocalDateTime value : values) {
            if (value != null && (max == null || value.isAfter(max))) {
                max = value;
            }
        }
        return max;
    }
}
