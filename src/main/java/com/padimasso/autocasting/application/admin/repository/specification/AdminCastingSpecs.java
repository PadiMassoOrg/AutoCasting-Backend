package com.padimasso.autocasting.application.admin.repository.specification;

import com.padimasso.autocasting.application.castings.model.CastingEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public final class AdminCastingSpecs {

    private AdminCastingSpecs() {
    }

    public static Specification<CastingEntity> fromSearchText(String q) {
        Specification<CastingEntity> spec = (root, query, cb) -> cb.isFalse(root.get("deleted"));

        if (q == null || q.isBlank()) return spec;

        String pattern = "%" + q.trim().toLowerCase(Locale.ROOT) + "%";
        return spec.and((root, query, cb) -> cb.like(cb.lower(root.get("title")), pattern));
    }
}
