package com.padimasso.autocasting.application.castings.repository.projection;

import java.time.LocalDate;
import java.util.UUID;

public interface CastingCardStatusGateProjection {
    UUID getId();

    String getCastingStatusCode();

    String getBasicInfoStatusCode();

    String getRolesStatusCode();

    String getRequirementsStatusCode();

    String getRemunerationStatusCode();

    LocalDate getApplicationDeadline();
}
