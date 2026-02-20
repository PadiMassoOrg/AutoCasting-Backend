package com.padimasso.autocasting.application.castings.repository.specification;

import com.padimasso.autocasting.application.castings.dto.EmployerCastingsFilter;
import com.padimasso.autocasting.application.castings.model.CastingBasicInfoEntity;
import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.castings.model.CastingRolesSectionEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

public final class CastingSpecs {

    private CastingSpecs() {
    }

    public static Specification<CastingEntity> fromFilter(EmployerCastingsFilter f) {
        Specification<CastingEntity> spec = null;

        spec = and(spec, forEmployer(f.employerProfileId()));
        spec = and(spec, searchText(f.search()));
        spec = and(spec, statusInTokens(f.statusIdTokens()));
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

    public static Specification<CastingEntity> searchText(String raw) {
        if (raw == null) return null;

        List<String> tokens = Arrays.stream(raw.trim().split("\\s+"))
            .map(String::trim)
            .filter(s -> !s.isBlank())
            .limit(6)
            .toList();

        if (tokens.isEmpty()) return null;

        return (root, query, cb) -> {
            Join<CastingEntity, CastingBasicInfoEntity> bi = joinOnce(root, "basicInfo", JoinType.LEFT);

            var predicatesPerToken = new ArrayList<jakarta.persistence.criteria.Predicate>();

            for (String t : tokens) {
                String like = toLikePattern(t);

                var inBasicInfo = cb.or(
                    cb.like(cb.lower(bi.get("title")), like, '\\'),
                    cb.like(cb.lower(bi.get("description")), like, '\\')
                );

                var sq = Objects.requireNonNull(query).subquery(Integer.class);
                Root<CastingRoleEntity> role = sq.from(CastingRoleEntity.class);

                Join<CastingRoleEntity, CastingRolesSectionEntity> rs = role.join("rolesSection", JoinType.INNER);
                Join<CastingRolesSectionEntity, CastingEntity> casting = rs.join("casting", JoinType.INNER);

                var inRoles = cb.exists(
                    sq.select(cb.literal(1)).where(
                        cb.equal(casting.get("id"), root.get("id")),
                        cb.or(
                            cb.like(cb.lower(role.get("roleName")), like, '\\'),
                            cb.like(cb.lower(role.get("description")), like, '\\')
                        )
                    )
                );

                predicatesPerToken.add(cb.or(inBasicInfo, inRoles));
            }

            return cb.and(predicatesPerToken.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
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
            Join<CastingEntity, CastingBasicInfoEntity> bi = joinOnce(root, "basicInfo", JoinType.LEFT);
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

            Join<CastingEntity, CastingBasicInfoEntity> bi = joinOnce(root, "basicInfo", JoinType.LEFT);
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

            Join<CastingEntity, CastingBasicInfoEntity> bi = joinOnce(root, "basicInfo", JoinType.LEFT);
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

    private static String toLikePattern(String token) {
        String s = token.toLowerCase(Locale.ROOT).trim();
        s = s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
        return "%" + s + "%";
    }

    @SuppressWarnings("unchecked")
    private static <X, Y> Join<X, Y> joinOnce(jakarta.persistence.criteria.From<X, ?> from, String attr, JoinType type) {
        for (var j : from.getJoins()) {
            if (j.getAttribute() != null && attr.equals(j.getAttribute().getName())) {
                return (Join<X, Y>) j;
            }
        }
        return from.join(attr, type);
    }

    private record ParsedTokens(List<UUID> ids, List<String> codes) {
    }
}
