package com.padimasso.autocasting.application.castings.repository.projection;

import java.time.LocalDate;

public interface CastingPublishGateProjection {
    String getCastingStatusCode();

    String getBasicInfoStatusCode();

    String getRolesStatusCode();

    String getRequirementsStatusCode();

    String getRemunerationStatusCode();

    LocalDate getApplicationDeadline();
}
