package com.padimasso.autocasting.application.castings.repository.projection;

import java.util.UUID;

public interface CastingCloseTargetProjection {
    UUID getCastingId();
    UUID getEmployerProfileId();
}
