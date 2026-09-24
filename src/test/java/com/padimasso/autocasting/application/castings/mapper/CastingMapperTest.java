package com.padimasso.autocasting.application.castings.mapper;

import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CastingMapperTest {

    private final CastingMapper mapper = new CastingMapper();

    // ---- reference photo url ----

    @Test
    void toRoleResponse_includesReferencePhotoUrl() {
        CastingRoleEntity role = CastingRoleEntity.builder()
            .referencePhotoUrl("https://example.com/photo.jpg")
            .build();

        var response = mapper.toRoleResponse(role);

        assertEquals("https://example.com/photo.jpg", response.referencePhotoUrl());
    }

    @Test
    void toRoleResponse_nullReferencePhotoUrl_mapsToNull() {
        CastingRoleEntity role = CastingRoleEntity.builder().build();

        var response = mapper.toRoleResponse(role);

        assertNull(response.referencePhotoUrl());
    }

    @Test
    void toPublicRoleResponse_includesReferencePhotoUrl() {
        CastingRoleEntity role = CastingRoleEntity.builder()
            .referencePhotoUrl("https://example.com/photo.jpg")
            .build();

        var response = mapper.toPublicRoleResponse(role);

        assertEquals("https://example.com/photo.jpg", response.referencePhotoUrl());
    }

    @Test
    void toPublicRoleResponse_nullReferencePhotoUrl_mapsToNull() {
        CastingRoleEntity role = CastingRoleEntity.builder().build();

        var response = mapper.toPublicRoleResponse(role);

        assertNull(response.referencePhotoUrl());
    }
}
