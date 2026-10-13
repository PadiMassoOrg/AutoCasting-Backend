package com.padimasso.autocasting.application.events.repository.specification;

import com.padimasso.autocasting.application.events.dto.EventFilter;
import com.padimasso.autocasting.application.events.model.EventEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings({"unchecked", "rawtypes"})
class EventSpecsTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 12);

    private final CriteriaBuilder cb = mock(CriteriaBuilder.class);
    private final CriteriaBuilder.Case<LocalDate> caseBuilder = mock(CriteriaBuilder.Case.class);
    private final Root<EventEntity> root = mock(Root.class);
    private final CriteriaQuery<EventEntity> query = mock(CriteriaQuery.class);

    private final Path startPath = mock(Path.class);
    private final Path endPath = mock(Path.class);
    private final Path createdPath = mock(Path.class);
    private final Path idPath = mock(Path.class);
    private final Expression<LocalDate> effectiveStart = mock(Expression.class);
    private final Predicate startsAfterToday = mock(Predicate.class);

    private final Order effectiveStartAsc = mock(Order.class);
    private final Order endAsc = mock(Order.class);
    private final Order createdDesc = mock(Order.class);
    private final Order idAsc = mock(Order.class);

    @BeforeEach
    void stubCriteria() {
        when(root.get("startDate")).thenReturn(startPath);
        when(root.get("endDate")).thenReturn(endPath);
        when(root.get("createdAt")).thenReturn(createdPath);
        when(root.get("id")).thenReturn(idPath);

        when(cb.<LocalDate>selectCase()).thenReturn(caseBuilder);
        when(cb.greaterThan((Expression) startPath, (Comparable) TODAY)).thenReturn(startsAfterToday);
        when(caseBuilder.when(startsAfterToday, startPath)).thenReturn(caseBuilder);
        when(caseBuilder.otherwise(TODAY)).thenReturn(effectiveStart);

        when(cb.asc(effectiveStart)).thenReturn(effectiveStartAsc);
        when(cb.asc(endPath)).thenReturn(endAsc);
        when(cb.desc(createdPath)).thenReturn(createdDesc);
        when(cb.asc(idPath)).thenReturn(idAsc);
    }

    @Test
    void catalogOrder_ordersByEffectiveStartThenEndThenNewestCreatedThenId() {
        when(query.getResultType()).thenReturn((Class) EventEntity.class);

        var predicate = EventSpecs.catalogOrder(TODAY).toPredicate(root, query, cb);

        ArgumentCaptor<List<Order>> orders = ArgumentCaptor.forClass(List.class);
        verify(query).orderBy(orders.capture());
        assertEquals(List.of(effectiveStartAsc, endAsc, createdDesc, idAsc), orders.getValue());
        assertNull(predicate);
    }

    @Test
    void catalogOrder_effectiveStartIsTheStartDateOnlyWhenItIsAfterToday() {
        when(query.getResultType()).thenReturn((Class) EventEntity.class);

        EventSpecs.catalogOrder(TODAY).toPredicate(root, query, cb);

        verify(cb).greaterThan((Expression) startPath, (Comparable) TODAY);
        verify(caseBuilder).when(startsAfterToday, startPath);
        verify(caseBuilder).otherwise(TODAY);
    }

    @Test
    void catalogOrder_doesNotOrderTheCountQuery() {
        when(query.getResultType()).thenReturn((Class) Long.class);

        EventSpecs.catalogOrder(TODAY).toPredicate(root, query, cb);

        verify(query, never()).orderBy(anyList());
    }

    @Test
    void fromFilter_withADateToBeforeTheEffectiveDateFromMatchesNothing() {
        Predicate matchesNothing = mock(Predicate.class);
        when(cb.disjunction()).thenReturn(matchesNothing);

        var predicate = EventSpecs.fromFilter(new EventFilter(null, TODAY.minusDays(1)), TODAY).toPredicate(root, query, cb);

        assertEquals(matchesNothing, predicate);
    }

    @Test
    void fromFilter_withADateToBeforeAPastDateFromIsClampedFirstAndStillMatchesNothing() {
        Predicate matchesNothing = mock(Predicate.class);
        when(cb.disjunction()).thenReturn(matchesNothing);

        var predicate = EventSpecs.fromFilter(new EventFilter(TODAY.minusDays(30), TODAY.minusDays(10)), TODAY)
            .toPredicate(root, query, cb);

        assertEquals(matchesNothing, predicate);
    }
}
