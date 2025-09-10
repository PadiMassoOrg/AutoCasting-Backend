package com.padimasso.autocasting.application.profile.repository;

import com.padimasso.autocasting.application.profile.model.ProfileEntity;
import com.padimasso.autocasting.application.profile.repository.projection.ProfessionRow;
import com.padimasso.autocasting.application.profile.repository.projection.ProfileCardRow;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository extends SoftDeleteRepository<ProfileEntity, UUID> {
    Optional<ProfileEntity> findByUserId(UUID id);

    Optional<ProfileEntity> findByDefaultSlugOrPremiumSlug(String slug, String slug1);

    @Query("""
        select
          p.id as id,
          p.defaultSlug as defaultSlug,
          p.premiumSlug as premiumSlug,
          p.plan.allowsCustomSlug as allowsCustomSlug,
          bi.stageName as stageName,
          c.email as email,
          c.phoneNumber as phoneNumber,
          m.headshotImageUrl as headshotImageUrl,
          p.modifiedAt as modifiedAt
        from #{#entityName} p
          join p.basicInfo bi
          join p.contact c
          left join p.media m
        order by p.modifiedAt desc, p.id desc
        """)
    Slice<ProfileCardRow> findCardRows(Pageable pageable);

    @Query("""
        select
            p.id as profileId,
            sm.id as id,
            sm.stringCode as stringCode
        from #{#entityName} p
          join p.basicInfo bi
          join bi.professions sm
        where p.id in :profileIds
        """)
    List<ProfessionRow> findProfessionsForProfiles(@Param("profileIds") Collection<UUID> profileIds);

    @Query("""
        select
          p.id as id,
          p.defaultSlug as defaultSlug,
          p.premiumSlug as premiumSlug,
          p.plan.allowsCustomSlug as allowsCustomSlug,
          bi.stageName as stageName,
          c.email as email,
          c.phoneNumber as phoneNumber,
          m.headshotImageUrl as headshotImageUrl,
          p.modifiedAt as modifiedAt
        from #{#entityName} p
          join p.basicInfo bi
          join p.contact c
          left join p.media m
        where p.id in :ids
        """)
    List<ProfileCardRow> findCardRowsByIds(@Param("ids") Collection<UUID> ids);

}
