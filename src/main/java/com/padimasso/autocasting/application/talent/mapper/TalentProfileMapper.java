package com.padimasso.autocasting.application.talent.mapper;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import com.padimasso.autocasting.application.sitemetadata.model.SiteMetadataBase;
import com.padimasso.autocasting.application.talent.dto.response.*;
import com.padimasso.autocasting.application.talent.model.*;
import com.padimasso.autocasting.application.talent.repository.TalentSocialMediaLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TalentProfileMapper {

    private final TalentSocialMediaLinkRepository socialMediaLinkRepository; // 👈 INYECTAMOS EL REPO

    public TalentProfileResponse toProfileResponse(TalentProfileEntity profile, UserEntity user) {
        // 👇 CARGAMOS LOS LINKS DESDE EL REPO (respeta deleted=false)
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
            toSocialMediaResponse(links),         // 👈 YA NO profile.getSocialMedia()
            toMediaResponse(profile.getMedia()),
            toCharacteristicsResponse(profile.getCharacteristics()),
            mapToSiteMetadataObjectList(profile.getSkills()),
            profile.getCredits().stream().map(this::toCreditResponse).collect(Collectors.toSet()),
            profile.getEducation().stream().map(this::toEducationResponse).collect(Collectors.toSet())
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
            toSocialMediaResponse(links),        // 👈 idem
            toMediaResponse(profile.getMedia()),
            toCharacteristicsResponse(profile.getCharacteristics()),
            mapToSiteMetadataObjectList(profile.getSkills()),
            profile.getCredits().stream().map(this::toCreditResponse).collect(Collectors.toSet()),
            profile.getEducation().stream().map(this::toEducationResponse).collect(Collectors.toSet())
        );
    }

    public SocialMediaResponse toSocialMediaResponse(List<TalentSocialMediaLinkEntity> links) {
        var items = links.stream()
            .map(l -> new SocialMediaLinkResponse(
                l.getOption().getId(),
                l.getOption().getStringCode(),
                l.getUrl()
            ))
            .toList();
        return new SocialMediaResponse(items);
    }

    // ===== resto de métodos como ya los tenías =====

    public BasicInfoResponse toBasicInfoResponse(BasicInfoEntity entity) {
        if (entity == null) return null;
        return new BasicInfoResponse(
            entity.getId(),
            entity.getStageName(),
            mapToSiteMetadataObject(entity.getGender()),
            entity.getBirthDate(),
            entity.getProfessions().stream().map(this::mapToSiteMetadataObject).toList()
        );
    }

    public ContactResponse toContactResponse(ContactEntity entity) {
        if (entity == null) return null;
        return new ContactResponse(
            entity.getId(),
            entity.getEmail(),
            entity.getPhoneNumber()
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
            entity.getShowReelVideoUrl()
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
            mapToSiteMetadataObject(entity.getDietOption())
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
            entity.getYear()
        );
    }

    public EducationResponse toEducationResponse(EducationEntity entity) {
        if (entity == null) return null;
        return new EducationResponse(
            entity.getId(),
            entity.getInstitution(),
            entity.getCourseName(),
            entity.getGraduationYear()
        );
    }

    public <T extends SiteMetadataBase> SiteMetadataObject mapToSiteMetadataObject(T entity) {
        if (entity == null) {
            return null;
        }
        return new SiteMetadataObject(entity.getId(), entity.getStringCode(), entity.getCategoryStringCode());
    }

    public <T extends SiteMetadataBase> Set<SiteMetadataObject> mapToSiteMetadataObjectList(Set<T> entities) {
        return entities.stream()
            .map(this::mapToSiteMetadataObject)
            .collect(Collectors.toSet());
    }
}
