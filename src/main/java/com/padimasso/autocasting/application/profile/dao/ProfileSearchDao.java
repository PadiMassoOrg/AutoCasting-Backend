package com.padimasso.autocasting.application.profile.dao;

import com.padimasso.autocasting.application.common.dto.MatchMode;
import com.padimasso.autocasting.application.profile.dto.TalentFilter;
import com.padimasso.autocasting.application.profile.model.BasicInfoEntity;
import com.padimasso.autocasting.application.profile.model.CharacteristicsEntity;
import com.padimasso.autocasting.application.profile.model.ProfileEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Repository
public class ProfileSearchDao {

    @PersistenceContext
    private EntityManager em;

    public record IdSlice(List<UUID> ids, boolean hasNext) {}

    public IdSlice searchIds(TalentFilter f, int page, int size) {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createTupleQuery();

        Root<ProfileEntity> p = cq.from(ProfileEntity.class);
        Join<ProfileEntity, BasicInfoEntity> bi = p.join("basicInfo", JoinType.INNER);

        List<Predicate> preds = new ArrayList<>();

        // stageName contains
        if (f.stageName() != null && !f.stageName().isBlank()) {
            preds.add(cb.like(cb.lower(bi.get("stageName")),
                "%" + f.stageName().toLowerCase(Locale.ROOT) + "%"));
        }

        // age range -> birthDate
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        LocalDate minBirth = (f.ageMin() != null) ? today.minusYears(f.ageMin()) : null;
        LocalDate maxBirth = (f.ageMax() != null) ? today.minusYears(f.ageMax() + 1).plusDays(1) : null;
        if (minBirth != null && maxBirth != null) {
            preds.add(cb.between(bi.get("birthDate"), maxBirth, minBirth));
        } else if (minBirth != null) {
            preds.add(cb.lessThanOrEqualTo(bi.get("birthDate"), minBirth));
        } else if (maxBirth != null) {
            preds.add(cb.greaterThanOrEqualTo(bi.get("birthDate"), maxBirth));
        }

        // gender (ManyToOne)
        if (f.genderId() != null) {
            preds.add(cb.equal(bi.get("gender").get("id"), f.genderId()));
        }

        // characteristics (opcionales)
        Join<ProfileEntity, CharacteristicsEntity> ch = null;
        if (needsCharacteristics(f)) ch = p.join("characteristics", JoinType.LEFT);

        if (f.heightMinCm() != null) preds.add(cb.greaterThanOrEqualTo(ch.get("heightCm"), f.heightMinCm()));
        if (f.heightMaxCm() != null) preds.add(cb.lessThanOrEqualTo(ch.get("heightCm"), f.heightMaxCm()));
        if (f.hairColorId() != null) preds.add(cb.equal(ch.get("hairColor").get("id"), f.hairColorId()));
        if (f.eyeColorId() != null) preds.add(cb.equal(ch.get("eyeColor").get("id"), f.eyeColorId()));
        if (f.tattoo() != null) preds.add(cb.equal(ch.get("tattoo"), f.tattoo()));
        if (f.passport() != null) preds.add(cb.equal(ch.get("passport"), f.passport()));
        if (f.drivingLicense() != null) preds.add(cb.equal(ch.get("drivingLicense"), f.drivingLicense()));

        // professions (ANY/ALL)
        if (f.professionIds() != null && !f.professionIds().isEmpty()) {
            if (f.professionsMode() == MatchMode.ALL) {
                preds.add(allOfManyToMany(cb, cq, p, "basicInfo.professions", "id", f.professionIds()));
            } else {
                preds.add(anyOfManyToMany(cb, cq, p, "basicInfo.professions", "id", f.professionIds()));
            }
        }

        // skills (ANY/ALL)  ← AQUÍ EL CAMBIO CLAVE
        if (f.skillIds() != null && !f.skillIds().isEmpty()) {
            if (f.skillsMode() == MatchMode.ALL) {
                preds.add(allOfManyToMany(cb, cq, p, "skills", "id", f.skillIds()));
            } else {
                preds.add(anyOfManyToMany(cb, cq, p, "skills", "id", f.skillIds()));
            }
        }

        // SELECT id + modifiedAt (Postgres DISTINCT+ORDER BY)
        cq.multiselect(
                p.get("id").alias("id"),
                p.get("modifiedAt").alias("modifiedAt")
            ).distinct(true)
            .where(cb.and(preds.toArray(Predicate[]::new)))
            .orderBy(cb.desc(p.get("modifiedAt")), cb.desc(p.get("id")));

        int pageIdx = Math.max(page, 0);
        int pageSize = Math.max(size, 1);

        var q = em.createQuery(cq);
        q.setFirstResult(pageIdx * pageSize);
        q.setMaxResults(pageSize + 1);

        List<Tuple> rows = q.getResultList();
        boolean hasNext = rows.size() > pageSize;

        List<UUID> ids = new ArrayList<>(Math.min(pageSize, rows.size()));
        for (int i = 0; i < Math.min(pageSize, rows.size()); i++) {
            ids.add(rows.get(i).get("id", UUID.class));
        }

        return new IdSlice(ids, hasNext);
    }

    private boolean needsCharacteristics(TalentFilter f) {
        return f.heightMinCm() != null || f.heightMaxCm() != null
            || (f.hairColorId() != null)
            || (f.eyeColorId() != null)
            || f.tattoo() != null || f.passport() != null || f.drivingLicense() != null;
    }

    /** ANY: el perfil tiene AL MENOS uno de los IDs en la colección indicada */
    private Predicate anyOfManyToMany(
        CriteriaBuilder cb,
        CriteriaQuery<?> outer,
        Root<ProfileEntity> root,
        String relationPath,   // "skills" o "basicInfo.professions"
        String idAttr,         // "id"
        List<UUID> targetIds
    ) {
        var sq = outer.subquery(UUID.class);
        Root<ProfileEntity> p2 = sq.from(ProfileEntity.class);

        From<?, ?> from = p2;
        for (String part : relationPath.split("\\.")) {
            from = from.join(part, JoinType.INNER);
        }
        Path<?> relId = from.get(idAttr);

        sq.select(p2.get("id"))
            .where(
                cb.equal(p2.get("id"), root.get("id")),
                relId.in(targetIds)
            )
            .distinct(true);

        return cb.exists(sq);
    }

    /** ALL: el perfil debe tener TODAS las IDs dadas en la colección indicada */
    private Predicate allOfManyToMany(
        CriteriaBuilder cb,
        CriteriaQuery<?> outer,
        Root<ProfileEntity> root,
        String relationPath,   // "skills" o "basicInfo.professions"
        String idAttr,         // "id"
        List<UUID> targetIds
    ) {
        var sq = outer.subquery(UUID.class);
        Root<ProfileEntity> p2 = sq.from(ProfileEntity.class);

        From<?, ?> from = p2;
        for (String part : relationPath.split("\\.")) {
            from = from.join(part, JoinType.INNER);
        }
        Path<?> relId = from.get(idAttr);

        sq.select(p2.get("id"))
            .where(
                cb.equal(p2.get("id"), root.get("id")),
                relId.in(targetIds)
            )
            .groupBy(p2.get("id"))
            .having(cb.equal(cb.countDistinct(relId), (long) targetIds.size()));

        return cb.exists(sq);
    }
}
