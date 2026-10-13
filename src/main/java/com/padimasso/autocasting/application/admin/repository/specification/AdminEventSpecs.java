package com.padimasso.autocasting.application.admin.repository.specification;

import com.padimasso.autocasting.application.events.model.EventEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Locale;

public final class AdminEventSpecs {

    private AdminEventSpecs() {
    }

    // Unlike the public catalog, admins also see ended events and events of suspended owners.
    public static Specification<EventEntity> fromSearchText(String q) {
        Specification<EventEntity> spec = (root, query, cb) -> cb.isFalse(root.get("deleted"));

        if (q == null || q.isBlank()) return spec;

        String pattern = toLikePattern(q);
        return spec.and((root, query, cb) -> cb.or(
            cb.like(cb.lower(root.get("name")), pattern, '\\'),
            cb.like(cb.lower(root.get("slug")), pattern, '\\')
        ));
    }

    // Overlap semantics, same as the public catalog: an event matches when it ends on or after dateFrom and starts on
    // or before dateTo. Unlike the catalog, dateFrom is not clamped to today, so admins can look into the past.
    public static Specification<EventEntity> overlapsDateWindow(LocalDate dateFrom, LocalDate dateTo) {
        // An inverted window matches nothing, instead of the running events that happen to span both dates.
        if (dateFrom != null && dateTo != null && dateTo.isBefore(dateFrom)) {
            return (root, query, cb) -> cb.disjunction();
        }

        return (root, query, cb) -> {
            var predicate = cb.conjunction();
            if (dateFrom != null) predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("endDate"), dateFrom));
            if (dateTo != null) predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("startDate"), dateTo));
            return predicate;
        };
    }

    // Wildcards typed by the admin are matched literally.
    static String toLikePattern(String token) {
        String s = token.toLowerCase(Locale.ROOT).trim();
        s = s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
        return "%" + s + "%";
    }
}
