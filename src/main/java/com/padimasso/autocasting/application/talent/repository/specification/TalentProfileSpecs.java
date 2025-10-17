package com.padimasso.autocasting.application.talent.repository.specification;

import com.padimasso.autocasting.application.common.dto.MatchMode;
import com.padimasso.autocasting.application.talent.dto.TalentFilter;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

@Component
public final class TalentProfileSpecs {

    public static Specification<TalentProfileEntity> hasHeadshot() {
        return (root, q, cb) -> {
            var m = root.join("media", JoinType.LEFT);
            var notNull = cb.isNotNull(m.get("headshotImageUrl"));
            var notBlank = cb.notEqual(cb.trim(m.get("headshotImageUrl")), "");
            return cb.and(notNull, notBlank);
        };
    }

    public static Specification<TalentProfileEntity> stageNameContains(String text) {
        if (text == null || text.isBlank()) return null;
        return (root, q, cb) -> {
            var bi = root.join("basicInfo");
            return cb.like(cb.lower(bi.get("stageName")), "%" + text.toLowerCase(Locale.ROOT) + "%");
        };
    }

    public static Specification<TalentProfileEntity> genderInTokens(List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) return null;
        boolean all = tokens.stream().anyMatch("NULL"::equalsIgnoreCase);
        if (all) return null;
        var ids = tokens.stream().filter(Objects::nonNull).map(UUID::fromString).toList();
        if (ids.isEmpty()) return null;
        return (root, q, cb) -> {
            var bi = root.join("basicInfo");
            return bi.get("gender").get("id").in(ids);
        };
    }

    public static Specification<TalentProfileEntity> ageBetween(Integer min, Integer max) {
        if (min == null && max == null) return null;
        return (root, q, cb) -> {
            var bi = root.join("basicInfo");
            var today = LocalDate.now(ZoneOffset.UTC);
            LocalDate minBirth = (min != null) ? today.minusYears(min) : null;
            LocalDate maxBirth = (max != null) ? today.minusYears(max + 1L).plusDays(1) : null;

            if (minBirth != null && maxBirth != null) {
                return cb.between(bi.get("birthDate"), maxBirth, minBirth);
            } else if (minBirth != null) {
                return cb.lessThanOrEqualTo(bi.get("birthDate"), minBirth);
            } else {
                return cb.greaterThanOrEqualTo(bi.get("birthDate"), maxBirth);
            }
        };
    }

    // Characteristics
    public static Specification<TalentProfileEntity> characteristics(TalentFilter f) {
        boolean any = f.heightMinCm() != null || f.heightMaxCm() != null
            || (f.hairColorIds() != null && !f.hairColorIds().isEmpty())
            || (f.eyeColorIds() != null && !f.eyeColorIds().isEmpty())
            || f.tattoo() != null || f.passport() != null || f.drivingLicense() != null;

        if (!any) return null;

        return (root, q, cb) -> {
            var ch = root.join("characteristics", JoinType.LEFT);
            List<Predicate> ps = new ArrayList<>();
            if (f.heightMinCm() != null) ps.add(cb.greaterThanOrEqualTo(ch.get("heightCm"), f.heightMinCm()));
            if (f.heightMaxCm() != null) ps.add(cb.lessThanOrEqualTo(ch.get("heightCm"), f.heightMaxCm()));

            if (f.hairColorIds() != null && !f.hairColorIds().isEmpty()) {
                ps.add(ch.get("hairColor").get("id").in(f.hairColorIds()));
            }
            if (f.eyeColorIds() != null && !f.eyeColorIds().isEmpty()) {
                ps.add(ch.get("eyeColor").get("id").in(f.eyeColorIds()));
            }

            if (f.tattoo() != null) ps.add(cb.equal(ch.get("tattoo"), f.tattoo()));
            if (f.passport() != null) ps.add(cb.equal(ch.get("passport"), f.passport()));
            if (f.drivingLicense() != null) ps.add(cb.equal(ch.get("drivingLicense"), f.drivingLicense()));
            return cb.and(ps.toArray(Predicate[]::new));
        };
    }


    // ManyToMany: ANY
    public static Specification<TalentProfileEntity> anyOf(String relationPath, List<UUID> ids) {
        if (ids == null || ids.isEmpty()) return null;
        return (root, q, cb) -> {
            q.distinct(true);
            From<?, ?> from = root;
            for (var part : relationPath.split("\\.")) {
                from = from.join(part, JoinType.INNER);
            }
            return from.get("id").in(ids);
        };
    }

    // ManyToMany: ALL
    public static Specification<TalentProfileEntity> allOf(String relationPath, List<UUID> ids) {
        if (ids == null || ids.isEmpty()) return null;
        return (root, q, cb) -> {
            var sq = q.subquery(Long.class);
            var p2 = sq.from(TalentProfileEntity.class);
            From<?, ?> from = p2;
            for (var part : relationPath.split("\\.")) {
                from = from.join(part, JoinType.INNER);
            }
            sq.select(cb.countDistinct(from.get("id")))
                .where(cb.equal(p2.get("id"), root.get("id")), from.get("id").in(ids));
            return cb.equal(sq, (long) ids.size());
        };
    }

    public static Specification<TalentProfileEntity> fromFilter(TalentFilter f) {
        Specification<TalentProfileEntity> spec = Specification
            .where(f.includeNoHeadshot() != Boolean.TRUE ? hasHeadshot() : null)
            .and(stageNameContains(f.stageName()))
            .and(ageBetween(f.ageMin(), f.ageMax()))
            .and(genderInTokens(f.genderIdTokens()))
            .and(characteristics(f));

        if (f.professionIds() != null && !f.professionIds().isEmpty()) {
            var ids = f.professionIds();
            spec = spec.and(
                f.professionsMode() == MatchMode.ALL
                    ? allOf("basicInfo.professions", ids)
                    : anyOf("basicInfo.professions", ids)
            );
        }

        if (f.skillIds() != null && !f.skillIds().isEmpty()) {
            var ids = f.skillIds();
            spec = spec.and(
                f.skillsMode() == MatchMode.ALL
                    ? allOf("skills", ids)
                    : anyOf("skills", ids)
            );
        }

        return spec;
    }
}
