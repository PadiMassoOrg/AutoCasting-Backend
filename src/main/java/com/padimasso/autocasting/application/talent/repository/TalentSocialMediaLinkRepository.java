package com.padimasso.autocasting.application.talent.repository;

import com.padimasso.autocasting.application.talent.model.TalentSocialMediaLinkEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface TalentSocialMediaLinkRepository
    extends SoftDeleteRepository<TalentSocialMediaLinkEntity, UUID> {

    default Set<TalentSocialMediaLinkEntity> findAllByTalentProfileId(UUID talentProfileId) {
        return Set.copyOf(
            findAllByPropertyEquals("talentProfile.id", talentProfileId)
        );
    }

    default Optional<TalentSocialMediaLinkEntity> findIncludingDeletedByTalentProfileIdAndOptionId(
        UUID talentProfileId,
        UUID optionId
    ) {
        Specification<TalentSocialMediaLinkEntity> spec = (root, q, cb) -> cb.and(
            cb.equal(root.get("talentProfile").get("id"), talentProfileId),
            cb.equal(root.get("option").get("id"), optionId)
        );
        List<TalentSocialMediaLinkEntity> all = findAllIncludingDeleted(spec);
        return all.stream().findFirst();
    }
}
