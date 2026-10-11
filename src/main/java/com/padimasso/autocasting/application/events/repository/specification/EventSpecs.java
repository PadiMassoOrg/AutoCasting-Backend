package com.padimasso.autocasting.application.events.repository.specification;

import com.padimasso.autocasting.application.events.dto.EventFilter;
import com.padimasso.autocasting.application.events.model.EventEntity;
import com.padimasso.autocasting.application.events.util.EventDateWindow;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class EventSpecs {

    private EventSpecs() {
    }

    // Overlap semantics, same as the casting catalog date filter: an event matches when it ends on or
    // after dateFrom and starts on or before dateTo.
    public static Specification<EventEntity> fromFilter(EventFilter filter, LocalDate today) {
        LocalDate dateFrom = EventDateWindow.effectiveFrom(filter == null ? null : filter.dateFrom(), today);
        LocalDate dateTo = filter == null ? null : filter.dateTo();

        Specification<EventEntity> spec = (root, query, cb) -> {
            var owner = root.join("user", JoinType.INNER);
            return cb.and(
                cb.isFalse(root.get("deleted")),
                cb.isFalse(owner.get("suspended")),
                cb.greaterThanOrEqualTo(root.get("endDate"), dateFrom)
            );
        };

        if (dateTo != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("startDate"), dateTo));
        }

        return spec;
    }
}
