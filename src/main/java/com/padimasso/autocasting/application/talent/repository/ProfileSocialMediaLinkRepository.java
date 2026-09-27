package com.padimasso.autocasting.application.talent.repository;

import com.padimasso.autocasting.application.talent.model.ProfileSocialMediaLinkEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfileSocialMediaLinkRepository
    extends SoftDeleteRepository<ProfileSocialMediaLinkEntity, UUID> {

    @EntityGraph(attributePaths = {"option"})
    @Query("""
        select l
        from ProfileSocialMediaLinkEntity l
        where l.talentProfile.id = :talentProfileId
          and l.deleted = false
        """)
    List<ProfileSocialMediaLinkEntity> findAllByTalentProfileId(UUID talentProfileId);

    @EntityGraph(attributePaths = {"option"})
    @Query("""
        select l
        from ProfileSocialMediaLinkEntity l
        where l.employerBasicInfo.id = :employerBasicInfoId
          and l.deleted = false
        """)
    List<ProfileSocialMediaLinkEntity> findAllByEmployerBasicInfoId(UUID employerBasicInfoId);

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

    default Optional<ProfileSocialMediaLinkEntity> findIncludingDeletedByEmployerBasicInfoIdAndOptionId(
        UUID employerBasicInfoId,
        UUID optionId
    ) {
        Specification<ProfileSocialMediaLinkEntity> spec = (root, q, cb) -> cb.and(
            cb.equal(root.get("employerBasicInfo").get("id"), employerBasicInfoId),
            cb.equal(root.get("option").get("id"), optionId)
        );
        List<ProfileSocialMediaLinkEntity> all = findAllIncludingDeleted(spec);
        return all.stream().findFirst();
    }
}
