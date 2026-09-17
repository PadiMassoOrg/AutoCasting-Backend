package com.padimasso.autocasting.application.castings.service.internal;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static com.padimasso.autocasting.config.AppConstants.*;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CastingStatusTransitionPolicyTest {

    private final CastingStatusTransitionPolicy policy = new CastingStatusTransitionPolicy();

    // ---- allowedNextStatuses ----

    @Test
    void allowedNextStatuses_nullCurrentStatus_returnsEmpty() {
        assertTrue(policy.allowedNextStatuses(null, LocalDate.now().plusDays(1), true).isEmpty());
    }

    @Test
    void allowedNextStatuses_deadlinePassed_archived_returnsEmpty() {
        List<String> result = policy.allowedNextStatuses(CASTING_STATUS_ARCHIVED, LocalDate.now().minusDays(1), true);

        assertTrue(result.isEmpty());
    }

    @Test
    void allowedNextStatuses_deadlinePassed_closed_returnsOnlyArchived() {
        List<String> result = policy.allowedNextStatuses(CASTING_STATUS_CLOSED, LocalDate.now().minusDays(1), true);

        assertEquals(List.of(CASTING_STATUS_ARCHIVED), result);
    }

    @Test
    void allowedNextStatuses_deadlinePassed_otherStatus_returnsClosedAndArchived() {
        List<String> result = policy.allowedNextStatuses(CASTING_STATUS_PUBLISHED, LocalDate.now().minusDays(1), true);

        assertEquals(List.of(CASTING_STATUS_CLOSED, CASTING_STATUS_ARCHIVED), result);
    }

    @Test
    void allowedNextStatuses_deadlineToday_treatedAsPassed() {
        List<String> result = policy.allowedNextStatuses(CASTING_STATUS_PUBLISHED, LocalDate.now(), true);

        assertEquals(List.of(CASTING_STATUS_PAUSED, CASTING_STATUS_CLOSED), result);
    }

    @Test
    void allowedNextStatuses_closed_returnsArchived() {
        List<String> result = policy.allowedNextStatuses(CASTING_STATUS_CLOSED, LocalDate.now().plusDays(1), true);

        assertEquals(List.of(CASTING_STATUS_ARCHIVED), result);
    }

    @Test
    void allowedNextStatuses_published_returnsPausedAndClosed() {
        List<String> result = policy.allowedNextStatuses(CASTING_STATUS_PUBLISHED, LocalDate.now().plusDays(1), true);

        assertEquals(List.of(CASTING_STATUS_PAUSED, CASTING_STATUS_CLOSED), result);
    }

    @Test
    void allowedNextStatuses_paused_publishable_returnsPublishedAndClosed() {
        List<String> result = policy.allowedNextStatuses(CASTING_STATUS_PAUSED, LocalDate.now().plusDays(1), true);

        assertEquals(List.of(CASTING_STATUS_PUBLISHED, CASTING_STATUS_CLOSED), result);
    }

    @Test
    void allowedNextStatuses_paused_notPublishable_returnsOnlyClosed() {
        List<String> result = policy.allowedNextStatuses(CASTING_STATUS_PAUSED, LocalDate.now().plusDays(1), false);

        assertEquals(List.of(CASTING_STATUS_CLOSED), result);
    }

    @Test
    void allowedNextStatuses_draft_returnsEmpty() {
        List<String> result = policy.allowedNextStatuses(CASTING_STATUS_DRAFT, LocalDate.now().plusDays(1), true);

        assertTrue(result.isEmpty());
    }

    @Test
    void allowedNextStatuses_archived_noDeadline_returnsEmpty() {
        List<String> result = policy.allowedNextStatuses(CASTING_STATUS_ARCHIVED, null, true);

        assertTrue(result.isEmpty());
    }

    @Test
    void allowedNextStatuses_noDeadline_published_returnsPausedAndClosed() {
        List<String> result = policy.allowedNextStatuses(CASTING_STATUS_PUBLISHED, null, true);

        assertEquals(List.of(CASTING_STATUS_PAUSED, CASTING_STATUS_CLOSED), result);
    }

    // ---- assertCanPublish ----

    @Test
    void assertCanPublish_notPublishable_throws() {
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> policy.assertCanPublish(CASTING_STATUS_DRAFT, LocalDate.now().plusDays(1), false));

        assertEquals(CASTINGS_NOT_PUBLISHABLE, exception.getMessage());
    }

    @Test
    void assertCanPublish_nullDeadline_throws() {
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> policy.assertCanPublish(CASTING_STATUS_DRAFT, null, true));

        assertEquals(CASTINGS_DEADLINE_REQUIRED, exception.getMessage());
    }

    @Test
    void assertCanPublish_deadlinePassed_throws() {
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> policy.assertCanPublish(CASTING_STATUS_DRAFT, LocalDate.now().minusDays(1), true));

        assertEquals(CASTINGS_DEADLINE_PASSED, exception.getMessage());
    }

    @Test
    void assertCanPublish_invalidCurrentStatus_throws() {
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> policy.assertCanPublish(CASTING_STATUS_PUBLISHED, LocalDate.now().plusDays(1), true));

        assertEquals(CASTINGS_INVALID_STATUS_TRANSITION, exception.getMessage());
    }

    @Test
    void assertCanPublish_fromDraft_doesNotThrow() {
        policy.assertCanPublish(CASTING_STATUS_DRAFT, LocalDate.now().plusDays(1), true);
    }

    @Test
    void assertCanPublish_fromPaused_doesNotThrow() {
        policy.assertCanPublish(CASTING_STATUS_PAUSED, LocalDate.now().plusDays(1), true);
    }

    // ---- assertCanSetDraft ----

    @Test
    void assertCanSetDraft_fromPublished_doesNotThrow() {
        policy.assertCanSetDraft(CASTING_STATUS_PUBLISHED);
    }

    @Test
    void assertCanSetDraft_fromPaused_doesNotThrow() {
        policy.assertCanSetDraft(CASTING_STATUS_PAUSED);
    }

    @Test
    void assertCanSetDraft_fromClosed_throws() {
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> policy.assertCanSetDraft(CASTING_STATUS_CLOSED));

        assertEquals(CASTINGS_INVALID_STATUS_TRANSITION, exception.getMessage());
    }

    // ---- assertCanPause ----

    @Test
    void assertCanPause_fromPublished_doesNotThrow() {
        policy.assertCanPause(CASTING_STATUS_PUBLISHED);
    }

    @Test
    void assertCanPause_fromDraft_throws() {
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> policy.assertCanPause(CASTING_STATUS_DRAFT));

        assertEquals(CASTINGS_INVALID_STATUS_TRANSITION, exception.getMessage());
    }

    // ---- assertCanClose ----

    @Test
    void assertCanClose_fromPublished_doesNotThrow() {
        policy.assertCanClose(CASTING_STATUS_PUBLISHED);
    }

    @Test
    void assertCanClose_fromArchived_throws() {
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> policy.assertCanClose(CASTING_STATUS_ARCHIVED));

        assertEquals(CASTINGS_INVALID_STATUS_TRANSITION, exception.getMessage());
    }

    @Test
    void assertCanClose_fromClosed_throws() {
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> policy.assertCanClose(CASTING_STATUS_CLOSED));

        assertEquals(CASTINGS_INVALID_STATUS_TRANSITION, exception.getMessage());
    }

    // ---- assertCanArchive ----

    @Test
    void assertCanArchive_fromClosed_doesNotThrow() {
        policy.assertCanArchive(CASTING_STATUS_CLOSED);
    }

    @Test
    void assertCanArchive_fromArchived_throws() {
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> policy.assertCanArchive(CASTING_STATUS_ARCHIVED));

        assertEquals(CASTINGS_INVALID_STATUS_TRANSITION, exception.getMessage());
    }
}
