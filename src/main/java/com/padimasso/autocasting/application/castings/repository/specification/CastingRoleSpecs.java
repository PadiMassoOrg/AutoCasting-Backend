package com.padimasso.autocasting.application.castings.repository.specification;

import com.padimasso.autocasting.application.castings.dto.CastingRoleFilter;
import com.padimasso.autocasting.application.castings.dto.EmployerCastingRoleFilter;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.common.dto.MatchMode;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public final class CastingRoleSpecs {

    private CastingRoleSpecs() {
    }

    public static Specification<CastingRoleEntity> fromFilter(CastingRoleFilter filter) {
        Specification<CastingRoleEntity> spec = (root, query, cb) -> cb.isFalse(root.get("deleted"));

        if (filter == null) return spec;

        spec = spec.and((root, query, cb) -> cb.equal(root.get("casting").get("status").get("stringCode"), "sitemetadata.casting_status.published"));
        spec = spec.and((root, query, cb) -> cb.isFalse(root.get("casting").get("deleted")));

        if (filter.roleName() != null && !filter.roleName().isBlank()) {
            String pattern = "%" + filter.roleName().trim().toLowerCase(Locale.ROOT) + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("roleName")), pattern));
        }

        if (filter.ageMin() != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("ageMax"), filter.ageMin().shortValue()));
        }
        if (filter.ageMax() != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("ageMin"), filter.ageMax().shortValue()));
        }

        if (filter.genderIdTokens() != null && !filter.genderIdTokens().isEmpty()) {
            boolean all = filter.genderIdTokens().stream().anyMatch("NULL"::equalsIgnoreCase);
            if (!all) {
                var ids = filter.genderIdTokens().stream()
                    .filter(Objects::nonNull)
                    .map(UUID::fromString)
                    .toList();
                if (!ids.isEmpty()) {
                    spec = spec.and((root, query, cb) -> root.get("gender").get("id").in(ids));
                }
            }
        }
        if (filter.ethnicityIdTokens() != null && !filter.ethnicityIdTokens().isEmpty()) {
            boolean all = filter.ethnicityIdTokens().stream().anyMatch("NULL"::equalsIgnoreCase);
            if (!all) {
                var ids = filter.ethnicityIdTokens().stream()
                    .filter(Objects::nonNull)
                    .map(UUID::fromString)
                    .toList();
                if (!ids.isEmpty()) {
                    spec = spec.and((root, query, cb) -> root.get("ethnicity").get("id").in(ids));
                }
            }
        }

        spec = spec.and(matchIds("professions", filter.professionIds(), filter.professionsMode()));
        spec = spec.and(matchIds("skills", filter.skillIds(), filter.skillsMode()));
        if (filter.tattoo() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("tattoo"), filter.tattoo()));
        }
        if (filter.passport() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("passport"), filter.passport()));
        }
        if (filter.drivingLicense() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("drivingLicense"), filter.drivingLicense()));
        }
        if (filter.projectTypeIds() != null && !filter.projectTypeIds().isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("casting").get("projectType").get("id").in(filter.projectTypeIds()));
        }
        if (filter.castingModalityIds() != null && !filter.castingModalityIds().isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("casting").get("castingModality").get("id").in(filter.castingModalityIds()));
        }
        if (filter.locationText() != null && !filter.locationText().isBlank()) {
            String pattern = "%" + filter.locationText().trim().toLowerCase(Locale.ROOT) + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("casting").get("locationText")), pattern));
        }

        return spec;
    }

    public static Specification<CastingRoleEntity> fromEmployerFilter(EmployerCastingRoleFilter filter) {
        Specification<CastingRoleEntity> spec = (root, query, cb) -> cb.isFalse(root.get("deleted"));
        if (filter == null || filter.castingId() == null) return spec;
        return spec.and((root, query, cb) -> cb.equal(root.get("casting").get("id"), filter.castingId()));
    }

    private static Specification<CastingRoleEntity> matchIds(String field, List<UUID> ids, MatchMode mode) {
        if (ids == null || ids.isEmpty()) {
            return null;
        }

        return (root, query, cb) -> {
            query.distinct(true);
            Join<?, ?> join;
            switch (field) {
                case "professions" -> join = root.join("professions", JoinType.LEFT);
                case "skills" -> join = root.join("skills", JoinType.LEFT);
                default -> throw new IllegalArgumentException("Unsupported field: " + field);
            }

            if (mode == MatchMode.ALL && ("professions".equals(field) || "skills".equals(field))) {
                query.groupBy(root.get("id"));
                return cb.equal(cb.countDistinct(join.get("id")), ids.size());
            }

            return join.get("id").in(ids);
        };
    }
}
