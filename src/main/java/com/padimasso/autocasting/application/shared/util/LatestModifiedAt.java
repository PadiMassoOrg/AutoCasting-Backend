package com.padimasso.autocasting.application.shared.util;

import com.padimasso.autocasting.application.common.model.AuditableEntity;

import java.time.LocalDateTime;
import java.util.Collection;

public final class LatestModifiedAt {

    private LatestModifiedAt() {
    }

    public static LocalDateTime of(LocalDateTime... values) {
        LocalDateTime latest = null;
        for (LocalDateTime value : values) {
            if (value != null && (latest == null || value.isAfter(latest))) {
                latest = value;
            }
        }
        return latest;
    }

    public static LocalDateTime ofEntities(Collection<? extends AuditableEntity> entities) {
        if (entities == null) return null;
        LocalDateTime latest = null;
        for (AuditableEntity entity : entities) {
            if (entity != null) {
                latest = of(latest, entity.getModifiedAt());
            }
        }
        return latest;
    }

    public static LocalDateTime ofEntity(AuditableEntity entity) {
        return entity != null ? entity.getModifiedAt() : null;
    }
}
