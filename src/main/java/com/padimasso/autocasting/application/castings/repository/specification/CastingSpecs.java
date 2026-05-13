package com.padimasso.autocasting.application.castings.repository.specification;

import com.padimasso.autocasting.application.castings.dto.EmployerCastingsFilter;
import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.sitemetadata.model.ProjectTypeOptionEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Locale;

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
            spec = spec.and((root, query, cb) -> {
                Join<CastingEntity, ProjectTypeOptionEntity> projectType = root.join("projectType", JoinType.LEFT);
                return projectType.get("stringCode").in(filter.projectTypeIdTokens());
            });
        }

        if (filter.statusIdTokens() != null && !filter.statusIdTokens().isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("status").get("stringCode").in(filter.statusIdTokens()));
        }

        return spec;
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
}
