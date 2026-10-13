package com.padimasso.autocasting.application.events.repository.specification;

import com.padimasso.autocasting.application.events.dto.EventFilter;
import com.padimasso.autocasting.application.events.model.EventEntity;
import com.padimasso.autocasting.application.events.util.EventDateWindow;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

public final class EventSpecs {

    private EventSpecs() {
    }

    // Overlap semantics, same as the casting catalog date filter: an event matches when it ends on or
    // after dateFrom and starts on or before dateTo.
    public static Specification<EventEntity> fromFilter(EventFilter filter, LocalDate today) {
        LocalDate dateFrom = EventDateWindow.effectiveFrom(filter == null ? null : filter.dateFrom(), today);
        LocalDate dateTo = filter == null ? null : filter.dateTo();

        // An inverted window (dateTo before the effective dateFrom, e.g. a dateTo in the past) matches nothing,
        // instead of the running events that happen to span both dates.
        if (dateTo != null && dateTo.isBefore(dateFrom)) {
            return (root, query, cb) -> cb.disjunction();
        }

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

    // Catalog order: events running today first, by soonest end ("last chance"), then upcoming events by start
    // date. Both groups are covered by sorting on max(startDate, today) and then endDate; ties go to the most
    // recently created event, then id so pages are stable. Sort cannot express this, so the order lives in the
    // specification and the page request must be unsorted. The count query is left unordered.
    public static Specification<EventEntity> catalogOrder(LocalDate today) {
        return (root, query, cb) -> {
            assert query != null;
            if (!Long.class.equals(query.getResultType())) {
                query.orderBy(catalogOrders(root, cb, today));
            }
            return null;
        };
    }

    static List<Order> catalogOrders(Root<EventEntity> root, CriteriaBuilder cb, LocalDate today) {
        Expression<LocalDate> startDate = root.get("startDate");
        Expression<LocalDate> effectiveStart = cb.<LocalDate>selectCase()
            .when(cb.greaterThan(startDate, today), startDate)
            .otherwise(today);

        return List.of(
            cb.asc(effectiveStart),
            cb.asc(root.get("endDate")),
            cb.desc(root.get("createdAt")),
            cb.asc(root.get("id"))
        );
    }
}
