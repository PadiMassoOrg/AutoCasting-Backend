package com.padimasso.autocasting.application.castings.repository.specification;

import com.padimasso.autocasting.application.castings.dto.EmployerCastingRequirementsFilter;
import com.padimasso.autocasting.application.castings.model.CastingRequirementEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CastingRequirementSpecs {

    private CastingRequirementSpecs() {
    }

    public static Specification<CastingRequirementEntity> fromEmployerFilter(EmployerCastingRequirementsFilter f) {
        Specification<CastingRequirementEntity> spec = null;

        spec = and(spec, byRequirementsSectionId(f.requirementsSectionId()));
        // TODO: Filtering

        return spec;
    }

    // Helpers
    public static Specification<CastingRequirementEntity> byRequirementsSectionId(UUID sectionId) {
        if (sectionId == null) return null;
        return (root, query, cb) ->
            cb.equal(root.join("castingRequirementsSection").get("id"), sectionId);
    }

    private static Specification<CastingRequirementEntity> and(
        Specification<CastingRequirementEntity> base,
        Specification<CastingRequirementEntity> next
    ) {
        if (next == null) return base;
        return (base == null) ? next : base.and(next);
    }
}
