package com.padimasso.autocasting.application.talent.repository;

import com.padimasso.autocasting.application.talent.model.ProfileSocialMediaLinkEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ProfileSocialMediaLinkRepository
    extends SoftDeleteRepository<ProfileSocialMediaLinkEntity, UUID> {

    default Set<ProfileSocialMediaLinkEntity> findAllByTalentProfileId(UUID talentProfileId) {
        return Set.copyOf(
            findAllByPropertyEquals("talentProfile.id", talentProfileId)
        );
    }

    default Set<ProfileSocialMediaLinkEntity> findAllByEmployerBasicInfoId(UUID employerBasicInfoId) {
        return Set.copyOf(
            findAllByPropertyEquals("employerBasicInfo.id", employerBasicInfoId)
        );
    }

    default Optional<ProfileSocialMediaLinkEntity> findIncludingDeletedByTalentProfileIdAndOptionId(
        UUID talentProfileId,
        UUID optionId
    ) {
        Specification<ProfileSocialMediaLinkEntity> spec = (root, q, cb) -> cb.and(
            cb.equal(root.get("talentProfile").get("id"), talentProfileId),
            cb.equal(root.get("option").get("id"), optionId)
        );
        List<ProfileSocialMediaLinkEntity> all = findAllIncludingDeleted(spec);
        return all.stream().findFirst();
    }
}
