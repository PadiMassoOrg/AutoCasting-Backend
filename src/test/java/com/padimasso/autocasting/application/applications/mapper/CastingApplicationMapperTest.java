package com.padimasso.autocasting.application.applications.mapper;

import com.padimasso.autocasting.application.applications.model.CastingApplicationEntity;
import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import com.padimasso.autocasting.application.sitemetadata.model.CastingStatusOptionEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_CLOSED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_PUBLISHED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class CastingApplicationMapperTest {

    private static final SiteMetadataObject CLOSED =
        new SiteMetadataObject(UUID.randomUUID(), CASTING_STATUS_CLOSED, "sitemetadata.casting_status");

    private final CastingApplicationMapper mapper = new CastingApplicationMapper();

    private static CastingApplicationEntity application(boolean roleDeleted) {
        var published = new CastingStatusOptionEntity();
        published.setId(UUID.randomUUID());
        published.setStringCode(CASTING_STATUS_PUBLISHED);
        var casting = CastingEntity.builder().id(UUID.randomUUID()).title("Film").status(published).build();
        var role = CastingRoleEntity.builder().id(UUID.randomUUID()).roleName("Lead").casting(casting).build();
        role.setDeleted(roleDeleted);
        return CastingApplicationEntity.builder().castingRole(role).build();
    }

    @Test
    void liveRole_reportsTheRealCastingStatus() {
        var card = mapper.toTalentCardFromEntity(application(false), CLOSED);

        assertEquals(CASTING_STATUS_PUBLISHED, card.castingStatus().stringCode());
    }

    @Test
    void deletedRole_isReportedAsClosedButKeepsItsData() {
        var card = mapper.toTalentCardFromEntity(application(true), CLOSED);

        assertSame(CLOSED, card.castingStatus());
        assertEquals("Lead", card.roleName());
        assertEquals("Film", card.castingName());
    }

    @Test
    void deletedRole_withoutAWithdrawnStatus_fallsBackToTheRealStatus() {
        var card = mapper.toTalentCardFromEntity(application(true), null);

        assertEquals(CASTING_STATUS_PUBLISHED, card.castingStatus().stringCode());
    }
}
