package com.padimasso.autocasting.application.applications.repository.projection;

import java.util.UUID;

public interface ApplicationProfessionProjection {
    UUID getApplicationId();

    UUID getProfessionId();

    String getProfessionCode();

    String getProfessionCategoryStringCode();
}
