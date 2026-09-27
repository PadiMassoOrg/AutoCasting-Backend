package com.padimasso.autocasting.application.shared.util;

import com.padimasso.autocasting.application.talent.model.CreditEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LatestModifiedAtTest {

    private static final LocalDateTime EARLY = LocalDateTime.of(2026, 1, 1, 10, 0);
    private static final LocalDateTime LATE = LocalDateTime.of(2026, 6, 1, 10, 0);

    @Test
    void of_returnsLatestIgnoringNulls() {
        assertEquals(LATE, LatestModifiedAt.of(EARLY, null, LATE));
    }

    @Test
    void of_allNull_returnsNull() {
        assertNull(LatestModifiedAt.of(null, null));
    }

    @Test
    void ofEntities_returnsLatestAndToleratesNullItems() {
        var early = CreditEntity.builder().build();
        early.setModifiedAt(EARLY);
        var late = CreditEntity.builder().build();
        late.setModifiedAt(LATE);

        assertEquals(LATE, LatestModifiedAt.ofEntities(Arrays.asList(early, null, late)));
    }

    @Test
    void ofEntities_nullOrEmpty_returnsNull() {
        assertNull(LatestModifiedAt.ofEntities(null));
        assertNull(LatestModifiedAt.ofEntities(List.of()));
    }

    @Test
    void ofEntity_null_returnsNull() {
        assertNull(LatestModifiedAt.ofEntity(null));
    }
}
