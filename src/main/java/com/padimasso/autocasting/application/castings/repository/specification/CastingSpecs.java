package com.padimasso.autocasting.application.castings.repository.specification;

import com.padimasso.autocasting.application.castings.dto.EmployerCastingsFilter;
import com.padimasso.autocasting.application.castings.model.CastingEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class CastingSpecs {

    private CastingSpecs() {
    }

    public static Specification<CastingEntity> forEmployer(UUID employerProfileId) {
        if (employerProfileId == null) return null;
        return (root, query, cb) ->
            cb.equal(root.join("employerProfile").get("id"), employerProfileId);
    }

    public static Specification<CastingEntity> fromFilter(EmployerCastingsFilter f) {
        Specification<CastingEntity> spec = null;

        spec = and(spec, forEmployer(f.employerProfileId()));

        return spec;
    }

    private static Specification<CastingEntity> and(
        Specification<CastingEntity> base,
        Specification<CastingEntity> next
    ) {
        if (next == null) return base;
        return (base == null) ? next : base.and(next);
    }
}

