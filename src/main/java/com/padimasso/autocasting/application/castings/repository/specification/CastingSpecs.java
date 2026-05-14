package com.padimasso.autocasting.application.castings.repository.specification;

import com.padimasso.autocasting.application.castings.dto.EmployerCastingsFilter;
import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.sitemetadata.model.ProjectTypeOptionEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_ARCHIVED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_CLOSED;

public final class CastingSpecs {

    private CastingSpecs() {
    }

    public static Specification<CastingEntity> fromFilter(EmployerCastingsFilter filter) {
        Specification<CastingEntity> spec = (root, query, cb) -> cb.isFalse(root.get("deleted"));

        if (filter == null) return spec;

        if (filter.employerProfileId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("employerProfile").get("id"), filter.employerProfileId()));
        }

        if (filter.search() != null && !filter.search().isBlank()) {
            String pattern = "%" + filter.search().trim().toLowerCase(Locale.ROOT) + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("title")), pattern));
        }

        if (filter.projectTypeIdTokens() != null && !filter.projectTypeIdTokens().isEmpty()) {
            spec = spec.and(projectTypeInTokens(filter.projectTypeIdTokens()));
        }

        if (filter.statusIdTokens() == null || filter.statusIdTokens().isEmpty()) {
            spec = spec.and(excludeStatusCodes(Set.of(CASTING_STATUS_CLOSED, CASTING_STATUS_ARCHIVED)));
        } else {
            spec = spec.and(statusInTokens(filter.statusIdTokens()));
        }

        return spec;
    }

    public static Specification<CastingEntity> statusInTokens(List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) return null;
        if (containsNullToken(tokens)) return null;

        ParsedTokens parsed = parseUuidOrStringCodes(tokens);
        if (parsed.ids().isEmpty() && parsed.codes().isEmpty()) return null;

        return (root, query, cb) -> {
            var status = root.get("status");

            if (!parsed.ids().isEmpty() && parsed.codes().isEmpty()) {
                return status.get("id").in(parsed.ids());
            }
            if (parsed.ids().isEmpty() && !parsed.codes().isEmpty()) {
                return status.get("stringCode").in(parsed.codes());
            }
            return cb.or(
                status.get("id").in(parsed.ids()),
                status.get("stringCode").in(parsed.codes())
            );
        };
    }

    public static Specification<CastingEntity> excludeStatusCodes(Set<String> codes) {
        if (codes == null || codes.isEmpty()) return null;

        return (root, query, cb) -> cb.not(root.get("status").get("stringCode").in(codes));
    }

    public static Specification<CastingEntity> projectTypeInTokens(List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) return null;
        if (containsNullToken(tokens)) return null;

        ParsedTokens parsed = parseUuidOrStringCodes(tokens);
        if (parsed.ids().isEmpty() && parsed.codes().isEmpty()) return null;

        return (root, query, cb) -> {
            Join<CastingEntity, ProjectTypeOptionEntity> projectType = root.join("projectType", JoinType.LEFT);

            if (!parsed.ids().isEmpty() && parsed.codes().isEmpty()) {
                return projectType.get("id").in(parsed.ids());
            }
            if (parsed.ids().isEmpty() && !parsed.codes().isEmpty()) {
                return projectType.get("stringCode").in(parsed.codes());
            }
            return cb.or(
                projectType.get("id").in(parsed.ids()),
                projectType.get("stringCode").in(parsed.codes())
            );
        };
    }

    public static Specification<CastingEntity> orderByDeadlineAscNullsLast() {
        return (root, query, cb) -> {
            query.orderBy(
                cb.asc(cb.selectCase().when(cb.isNull(root.get("applicationDeadline")), 1).otherwise(0)),
                cb.asc(root.get("applicationDeadline")),
                cb.desc(root.get("createdAt"))
            );
            return cb.conjunction();
        };
    }

    public static Specification<CastingEntity> orderByDeadlineDescNullsLast() {
        return (root, query, cb) -> {
            query.orderBy(
                cb.asc(cb.selectCase().when(cb.isNull(root.get("applicationDeadline")), 1).otherwise(0)),
                cb.desc(root.get("applicationDeadline")),
                cb.desc(root.get("createdAt"))
            );
            return cb.conjunction();
        };
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
            String token = raw.trim();
            if (token.isEmpty() || "NULL".equalsIgnoreCase(token)) continue;

            try {
                ids.add(UUID.fromString(token));
            } catch (IllegalArgumentException ex) {
                codes.add(token);
            }
        }

        return new ParsedTokens(ids, codes);
    }

    private record ParsedTokens(List<UUID> ids, List<String> codes) {
    }
}
