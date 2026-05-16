package com.padimasso.autocasting.application.castings.service.internal;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.padimasso.autocasting.config.AppConstants.*;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.*;

@Component
public class CastingStatusTransitionPolicy {

    public List<String> allowedNextStatuses(String currentStatusCode, LocalDate applicationDeadline, boolean publishable) {
        if (currentStatusCode == null) return List.of();

        if (applicationDeadline != null && applicationDeadline.isBefore(LocalDate.now())) {
            if (CASTING_STATUS_ARCHIVED.equals(currentStatusCode)) return List.of();
            if (CASTING_STATUS_CLOSED.equals(currentStatusCode)) return List.of(CASTING_STATUS_ARCHIVED);
            return List.of(CASTING_STATUS_CLOSED, CASTING_STATUS_ARCHIVED);
        }

        return switch (currentStatusCode) {
            case CASTING_STATUS_CLOSED -> List.of(CASTING_STATUS_ARCHIVED);
            case CASTING_STATUS_PUBLISHED -> List.of(CASTING_STATUS_PAUSED, CASTING_STATUS_CLOSED);
            case CASTING_STATUS_PAUSED -> {
                List<String> next = new ArrayList<>();
                if (publishable) next.add(CASTING_STATUS_PUBLISHED);
                next.add(CASTING_STATUS_CLOSED);
                yield next;
            }
            default -> List.of();
        };
    }

    public void assertCanPublish(String currentStatusCode, LocalDate applicationDeadline, boolean publishable) {
        if (!publishable) {
            throw new IllegalStateException(CASTINGS_NOT_PUBLISHABLE);
        }
        if (applicationDeadline == null) {
            throw new IllegalStateException(CASTINGS_DEADLINE_REQUIRED);
        }
        if (applicationDeadline.isBefore(LocalDate.now())) {
            throw new IllegalStateException(CASTINGS_DEADLINE_PASSED);
        }
        if (!(CASTING_STATUS_DRAFT.equals(currentStatusCode) || CASTING_STATUS_PAUSED.equals(currentStatusCode))) {
            throw new IllegalStateException(CASTINGS_INVALID_STATUS_TRANSITION);
        }
    }

    public void assertCanSetDraft(String currentStatusCode) {
        if (!(CASTING_STATUS_PUBLISHED.equals(currentStatusCode) || CASTING_STATUS_PAUSED.equals(currentStatusCode))) {
            throw new IllegalStateException(CASTINGS_INVALID_STATUS_TRANSITION);
        }
    }

    public void assertCanPause(String currentStatusCode) {
        if (!CASTING_STATUS_PUBLISHED.equals(currentStatusCode)) {
            throw new IllegalStateException(CASTINGS_INVALID_STATUS_TRANSITION);
        }
    }

    public void assertCanClose(String currentStatusCode) {
        if (CASTING_STATUS_ARCHIVED.equals(currentStatusCode) || CASTING_STATUS_CLOSED.equals(currentStatusCode)) {
            throw new IllegalStateException(CASTINGS_INVALID_STATUS_TRANSITION);
        }
    }

    public void assertCanArchive(String currentStatusCode) {
        if (CASTING_STATUS_ARCHIVED.equals(currentStatusCode)) {
            throw new IllegalStateException(CASTINGS_INVALID_STATUS_TRANSITION);
        }
    }
}
