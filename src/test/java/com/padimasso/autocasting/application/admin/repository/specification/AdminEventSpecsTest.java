package com.padimasso.autocasting.application.admin.repository.specification;

import com.padimasso.autocasting.application.events.model.EventEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings({"unchecked", "rawtypes"})
class AdminEventSpecsTest {

    private static final LocalDate FROM = LocalDate.of(2026, 10, 12);
    private static final LocalDate TO = LocalDate.of(2026, 10, 20);

    private final CriteriaBuilder cb = mock(CriteriaBuilder.class);
    private final Root<EventEntity> root = mock(Root.class);
    private final CriteriaQuery<EventEntity> query = mock(CriteriaQuery.class);

    private final Path startPath = mock(Path.class);
    private final Path endPath = mock(Path.class);
    private final Predicate all = mock(Predicate.class);
    private final Predicate endsOnOrAfterFrom = mock(Predicate.class);
    private final Predicate startsOnOrBeforeTo = mock(Predicate.class);
    private final Predicate withFrom = mock(Predicate.class);
    private final Predicate withFromAndTo = mock(Predicate.class);
    private final Predicate withTo = mock(Predicate.class);

    @BeforeEach
    void stubCriteria() {
        when(root.get("startDate")).thenReturn(startPath);
        when(root.get("endDate")).thenReturn(endPath);
        when(cb.conjunction()).thenReturn(all);
        when(cb.greaterThanOrEqualTo((Expression) endPath, (Comparable) FROM)).thenReturn(endsOnOrAfterFrom);
        when(cb.lessThanOrEqualTo((Expression) startPath, (Comparable) TO)).thenReturn(startsOnOrBeforeTo);
        when(cb.and(all, endsOnOrAfterFrom)).thenReturn(withFrom);
        when(cb.and(withFrom, startsOnOrBeforeTo)).thenReturn(withFromAndTo);
        when(cb.and(all, startsOnOrBeforeTo)).thenReturn(withTo);
    }

    @Test
    void overlapsDateWindow_withBothDatesMatchesEventsEndingOnOrAfterFromAndStartingOnOrBeforeTo() {
        var predicate = AdminEventSpecs.overlapsDateWindow(FROM, TO).toPredicate(root, query, cb);

        assertEquals(withFromAndTo, predicate);
    }

    @Test
    void overlapsDateWindow_withOnlyDateToDoesNotConstrainTheEndDate() {
        var predicate = AdminEventSpecs.overlapsDateWindow(null, TO).toPredicate(root, query, cb);

        assertEquals(withTo, predicate);
        verify(cb, never()).greaterThanOrEqualTo((Expression) endPath, (Comparable) FROM);
    }

    @Test
    void overlapsDateWindow_aDateFromInThePastIsNotClampedToToday() {
        LocalDate past = LocalDate.of(2020, 1, 1);
        Predicate pastPredicate = mock(Predicate.class);
        Predicate combined = mock(Predicate.class);
        when(cb.greaterThanOrEqualTo((Expression) endPath, (Comparable) past)).thenReturn(pastPredicate);
        when(cb.and(all, pastPredicate)).thenReturn(combined);

        var predicate = AdminEventSpecs.overlapsDateWindow(past, null).toPredicate(root, query, cb);

        assertEquals(combined, predicate);
    }

    @Test
    void overlapsDateWindow_withDateToBeforeDateFromMatchesNothing() {
        Predicate matchesNothing = mock(Predicate.class);
        when(cb.disjunction()).thenReturn(matchesNothing);

        var predicate = AdminEventSpecs.overlapsDateWindow(TO, FROM).toPredicate(root, query, cb);

        assertEquals(matchesNothing, predicate);
    }

    @Test
    void toLikePattern_lowercasesTrimsAndWrapsInWildcards() {
        assertEquals("%e-1a2b3c4d%", AdminEventSpecs.toLikePattern("  E-1A2B3C4D "));
    }

    @Test
    void toLikePattern_escapesWildcardsTypedByTheAdmin() {
        assertEquals("%100\\% a\\_b%", AdminEventSpecs.toLikePattern("100% a_b"));
    }

    @Test
    void toLikePattern_escapesTheEscapeCharacterItself() {
        assertEquals("%a\\\\b%", AdminEventSpecs.toLikePattern("a\\b"));
    }
}
