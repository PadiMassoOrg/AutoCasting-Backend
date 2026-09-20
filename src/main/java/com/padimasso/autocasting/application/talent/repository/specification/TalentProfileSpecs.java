package com.padimasso.autocasting.application.talent.repository.specification;

import com.padimasso.autocasting.application.common.dto.MatchMode;
import com.padimasso.autocasting.application.talent.dto.TalentFilter;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
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

    private TalentProfileSpecs() {
    }

    /**
     * SQL-level source of truth for "has the photos required to be visible in the public talent
     * catalog" (headshot + full body, both non-blank). Takes any {@code From} rooted at or joined
     * to {@link TalentProfileEntity} — a query's own root, or a join from a different root entity
     * (e.g. {@code CastingApplicationEntity.talentProfile}, {@code UserEntity}'s talent subquery) —
     * so every Specification needing this check builds it the same way instead of re-typing the
     * headshot/full-body null/blank conditions by hand.
     * <p>
     * Kept in sync with the plain-Java mirror {@code TalentMediaRequirements.hasRequiredPhotos}
     * (for call sites that already have a loaded entity and don't want to run a query) and
     * {@code TalentCatalogVisibility.isVisibleInCatalog} (which composes this same rule with the
     * deleted/suspended checks below).
     */
    public static Predicate hasRequiredMediaPredicate(From<?, TalentProfileEntity> talentProfile, CriteriaBuilder cb) {
        var m = talentProfile.join("media", JoinType.LEFT);
        var hasHeadshot = cb.and(
            cb.isNotNull(m.get("headshotImageUrl")),
            cb.notEqual(cb.trim(m.get("headshotImageUrl")), "")
        );
        var hasFullBody = cb.and(
            cb.isNotNull(m.get("fullBodyImageUrl")),
            cb.notEqual(cb.trim(m.get("fullBodyImageUrl")), "")
        );
        return cb.and(hasHeadshot, hasFullBody);
    }

    /**
     * SQL-level source of truth for "is this talent profile (and its user) active" — not deleted,
     * user not suspended. Does not check media; combine with {@link #hasRequiredMediaPredicate}
     * for full catalog visibility (see {@link #visibleInCatalogPredicate}).
     */
    public static Predicate activeProfilePredicate(From<?, TalentProfileEntity> talentProfile, CriteriaBuilder cb) {
        var user = talentProfile.join("user", JoinType.INNER);
        return cb.and(
            cb.isFalse(talentProfile.get("deleted")),
            cb.isFalse(user.get("suspended"))
        );
    }

    /**
     * SQL-level source of truth for "is this talent profile visible in the public catalog":
     * {@link #activeProfilePredicate} + {@link #hasRequiredMediaPredicate}. Same {@code From}
     * flexibility as above — use this instead of hand-rolling the deleted/suspended/media
     * conditions at a new call site.
     */
    public static Predicate visibleInCatalogPredicate(From<?, TalentProfileEntity> talentProfile, CriteriaBuilder cb) {
        return cb.and(
            activeProfilePredicate(talentProfile, cb),
            hasRequiredMediaPredicate(talentProfile, cb)
        );
    }

    public static Specification<TalentProfileEntity> hasRequiredMedia() {
        return (root, q, cb) -> hasRequiredMediaPredicate(root, cb);
    }

    /** Root-level Specification form of {@link #visibleInCatalogPredicate}. */
    public static Specification<TalentProfileEntity> visibleInCatalog() {
        return (root, q, cb) -> visibleInCatalogPredicate(root, cb);
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

        var ids = tokens.stream()
            .filter(Objects::nonNull)
            .map(UUID::fromString)
            .toList();
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
                // edad >= min  => fecha_nac <= hoy - min
                return cb.lessThanOrEqualTo(bi.get("birthDate"), minBirth);
            } else {
                // edad <= max  => fecha_nac >= hoy - (max+1) + 1 día
                return cb.greaterThanOrEqualTo(bi.get("birthDate"), maxBirth);
            }
        };
    }

    // =========================
    // Characteristics (height, hair, eye, ETHNICITY, tattoo, passport, drivingLicense)
    // =========================
    public static Specification<TalentProfileEntity> characteristics(TalentFilter f) {
        boolean any =
            f.heightMinCm() != null ||
                f.heightMaxCm() != null ||
                (f.hairColorIds() != null && !f.hairColorIds().isEmpty()) ||
                (f.eyeColorIds() != null && !f.eyeColorIds().isEmpty()) ||
                (f.ethnicityIdTokens() != null && !f.ethnicityIdTokens().isEmpty()) ||  // 👈 IMPORTANTE
                f.tattoo() != null ||
                f.passport() != null ||
                f.drivingLicense() != null;

        if (!any) return null;

        return (root, q, cb) -> {
            var ch = root.join("characteristics", JoinType.LEFT);
            List<Predicate> ps = new ArrayList<>();

            // Height
            if (f.heightMinCm() != null) {
                ps.add(cb.greaterThanOrEqualTo(ch.get("heightCm"), f.heightMinCm()));
            }
            if (f.heightMaxCm() != null) {
                ps.add(cb.lessThanOrEqualTo(ch.get("heightCm"), f.heightMaxCm()));
            }

            // Hair color
            if (f.hairColorIds() != null && !f.hairColorIds().isEmpty()) {
                ps.add(ch.get("hairColor").get("id").in(f.hairColorIds()));
            }

            // Eye color
            if (f.eyeColorIds() != null && !f.eyeColorIds().isEmpty()) {
                ps.add(ch.get("eyeColor").get("id").in(f.eyeColorIds()));
            }

            // Ethnicity
            if (f.ethnicityIdTokens() != null && !f.ethnicityIdTokens().isEmpty()) {
                boolean all = f.ethnicityIdTokens().stream().anyMatch("NULL"::equalsIgnoreCase);
                if (!all) {
                    var ids = f.ethnicityIdTokens().stream()
                        .filter(Objects::nonNull)
                        .map(UUID::fromString)
                        .toList();
                    if (!ids.isEmpty()) {
                        ps.add(ch.get("ethnicity").get("id").in(ids));
                    }
                }
            }

            // Tattoo / passport / drivingLicense
            if (f.tattoo() != null) {
                ps.add(cb.equal(ch.get("tattoo"), f.tattoo()));
            }
            if (f.passport() != null) {
                ps.add(cb.equal(ch.get("passport"), f.passport()));
            }
            if (f.drivingLicense() != null) {
                ps.add(cb.equal(ch.get("drivingLicense"), f.drivingLicense()));
            }

            return ps.isEmpty() ? cb.conjunction() : cb.and(ps.toArray(Predicate[]::new));
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
                .where(
                    cb.equal(p2.get("id"), root.get("id")),
                    from.get("id").in(ids)
                );
            return cb.equal(sq, (long) ids.size());
        };
    }

    public static Specification<TalentProfileEntity> fromFilter(TalentFilter f) {
        Specification<TalentProfileEntity> spec = (root, query, cb) -> activeProfilePredicate(root, cb);

        spec = spec.and(f.includeNoHeadshot() != Boolean.TRUE ? hasRequiredMedia() : null);
        spec = spec.and(stageNameContains(f.stageName()));
        spec = spec.and(ageBetween(f.ageMin(), f.ageMax()));
        spec = spec.and(genderInTokens(f.genderIdTokens()));
        // 👇 Ethnicity ya viene dentro de `characteristics(f)`
        spec = spec.and(characteristics(f));

        // Professions
        if (f.professionIds() != null && !f.professionIds().isEmpty()) {
            var ids = f.professionIds();
            spec = spec.and(
                f.professionsMode() == MatchMode.ALL
                    ? allOf("basicInfo.professions", ids)
                    : anyOf("basicInfo.professions", ids)
            );
        }

        // Skills
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
