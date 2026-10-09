package com.padimasso.autocasting.application.admin.mapper;

import com.padimasso.autocasting.application.castings.mapper.CastingMapper;
import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.castings.service.internal.CastingStatusTransitionPolicy;
import com.padimasso.autocasting.application.sitemetadata.model.CastingStatusOptionEntity;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_ARCHIVED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_CLOSED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_DRAFT;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_PAUSED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_PUBLISHED;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AdminCastingMapperTest {

    private final AdminCastingMapper mapper =
        new AdminCastingMapper(Mockito.mock(CastingMapper.class), new CastingStatusTransitionPolicy());

    private static CastingEntity casting(String statusCode) {
        var status = new CastingStatusOptionEntity();
        status.setId(UUID.randomUUID());
        status.setStringCode(statusCode);
        return CastingEntity.builder().id(UUID.randomUUID()).status(status).title("Film").build();
    }

    @Test
    void rowOffersTheSameNextStatusesAsTheEmployerHas() {
        assertEquals(List.of(CASTING_STATUS_PAUSED, CASTING_STATUS_CLOSED),
            mapper.toRowResponse(casting(CASTING_STATUS_PUBLISHED)).allowedStatusCodes());
        assertEquals(List.of(CASTING_STATUS_ARCHIVED), mapper.toRowResponse(casting(CASTING_STATUS_CLOSED)).allowedStatusCodes());
    }

    @Test
    void anIncompletePausedCastingCannotBePublishedFromTheRow() {
        assertEquals(List.of(CASTING_STATUS_CLOSED), mapper.toRowResponse(casting(CASTING_STATUS_PAUSED)).allowedStatusCodes());
    }

    @Test
    void draftArchivedAndDeletedCastingsOfferNothing() {
        assertEquals(List.of(), mapper.toRowResponse(casting(CASTING_STATUS_DRAFT)).allowedStatusCodes());
        assertEquals(List.of(), mapper.toRowResponse(casting(CASTING_STATUS_ARCHIVED)).allowedStatusCodes());

        var deleted = casting(CASTING_STATUS_PUBLISHED);
        deleted.setDeleted(true);
        assertEquals(List.of(), mapper.toRowResponse(deleted).allowedStatusCodes());
    }
}
