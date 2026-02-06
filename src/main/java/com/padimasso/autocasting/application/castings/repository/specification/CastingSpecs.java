package com.padimasso.autocasting.application.castings.repository.specification;

import com.padimasso.autocasting.application.castings.dto.EmployerCastingsFilter;
import com.padimasso.autocasting.application.castings.model.CastingBasicInfoEntity;
import com.padimasso.autocasting.application.castings.model.CastingEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class CastingSpecs {

    private CastingSpecs() {
    }

    public static Specification<CastingEntity> fromFilter(EmployerCastingsFilter f) {
        Specification<CastingEntity> spec = null;

        spec = and(spec, forEmployer(f.employerProfileId()));
        spec = and(spec, statusInTokens(f.statusIdTokens()));              // ✅ MULTI
        spec = and(spec, projectTypeInTokens(f.projectTypeIdTokens()));

        return spec;
    }

    // ======================
    // Filters
    // ======================
    public static Specification<CastingEntity> forEmployer(UUID employerProfileId) {
        if (employerProfileId == null) return null;
        return (root, query, cb) ->
            cb.equal(root.join("employerProfile").get("id"), employerProfileId);
    }

    public static Specification<CastingEntity> statusInTokens(List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) return null;
        if (containsNullToken(tokens)) return null;

        ParsedTokens parsed = parseUuidOrStringCodes(tokens);
        if (parsed.ids.isEmpty() && parsed.codes.isEmpty()) return null;

        return (root, query, cb) -> {
            var status = root.get("status");

            if (!parsed.ids.isEmpty() && parsed.codes.isEmpty()) {
                return status.get("id").in(parsed.ids);
            }
            if (parsed.ids.isEmpty() && !parsed.codes.isEmpty()) {
                return status.get("stringCode").in(parsed.codes);
            }
            return cb.or(
                status.get("id").in(parsed.ids),
                status.get("stringCode").in(parsed.codes)
            );
        };
    }

    public static Specification<CastingEntity> projectTypeInTokens(List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) return null;
        if (containsNullToken(tokens)) return null;

        ParsedTokens parsed = parseUuidOrStringCodes(tokens);
        if (parsed.ids.isEmpty() && parsed.codes.isEmpty()) return null;

        return (root, query, cb) -> {
            Join<CastingEntity, CastingBasicInfoEntity> bi = root.join("basicInfo", JoinType.LEFT);
            var projectType = bi.join("projectType", JoinType.LEFT);

            if (!parsed.ids.isEmpty() && parsed.codes.isEmpty()) {
                return projectType.get("id").in(parsed.ids);
            }
            if (parsed.ids.isEmpty() && !parsed.codes.isEmpty()) {
                return projectType.get("stringCode").in(parsed.codes);
            }
            return cb.or(
                projectType.get("id").in(parsed.ids),
                projectType.get("stringCode").in(parsed.codes)
            );
        };
    }

    // ======================
    // ORDER BY (Criteria-safe)
    // ======================
    public static Specification<CastingEntity> orderByDeadlineAscNullsLast() {
        return (root, query, cb) -> {
            assert query != null;

            Join<CastingEntity, CastingBasicInfoEntity> bi = root.join("basicInfo", JoinType.LEFT);
            var deadline = bi.get("applicationDeadline");

            query.orderBy(
                cb.asc(cb.selectCase().when(cb.isNull(deadline), 1).otherwise(0)),
                cb.asc(deadline),
                cb.desc(root.get("createdAt")),
                cb.desc(root.get("id"))
            );

            return cb.conjunction();
        };
    }

    public static Specification<CastingEntity> orderByDeadlineDescNullsLast() {
        return (root, query, cb) -> {
            assert query != null;

            Join<CastingEntity, CastingBasicInfoEntity> bi = root.join("basicInfo", JoinType.LEFT);
            var deadline = bi.get("applicationDeadline");

            query.orderBy(
                cb.asc(cb.selectCase().when(cb.isNull(deadline), 1).otherwise(0)),
                cb.desc(deadline),
                cb.desc(root.get("createdAt")),
                cb.desc(root.get("id"))
            );

            return cb.conjunction();
        };
    }

    // ======================
    // Helper
    // ======================
    private static Specification<CastingEntity> and(Specification<CastingEntity> base, Specification<CastingEntity> next) {
        if (next == null) return base;
        return (base == null) ? next : base.and(next);
    }

    private static boolean containsNullToken(List<String> tokens) {
        return tokens.stream()
            .filter(Objects::nonNull)
            .anyMatch(t -> "NULL".equalsIgnoreCase(t.trim()));
    }

    private static ParsedTokens parseUuidOrStringCodes(List<String> tokens) {
        List<UUID> ids = new ArrayList<>();
        List<String> codes = new ArrayList<>();

        for (String raw : tokens) {
            if (raw == null) continue;
            String t = raw.trim();
            if (t.isEmpty()) continue;
            if ("NULL".equalsIgnoreCase(t)) continue;

            try {
                ids.add(UUID.fromString(t));
            } catch (IllegalArgumentException ex) {
                codes.add(t);
            }
        }
        return new ParsedTokens(ids, codes);
    }

    private record ParsedTokens(List<UUID> ids, List<String> codes) {
    }
}
