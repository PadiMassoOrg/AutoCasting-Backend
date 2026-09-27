package com.padimasso.autocasting.application.employer.util;

import com.padimasso.autocasting.application.employer.model.EmployerBasicInfoEntity;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.talent.model.ProfileSocialMediaLinkEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EmployerProfileLastSavedTest {

    private static final LocalDateTime BASE = LocalDateTime.of(2026, 1, 1, 10, 0);

    @Test
    void picksLatestAcrossProfileBasicInfoAndLinks() {
        var basicInfo = EmployerBasicInfoEntity.builder().build();
        basicInfo.setModifiedAt(BASE.plusDays(5));
        var profile = EmployerProfileEntity.builder().basicInfo(basicInfo).build();
        profile.setModifiedAt(BASE);
        var link = ProfileSocialMediaLinkEntity.builder().build();
        link.setModifiedAt(BASE.plusDays(3));

        assertEquals(BASE.plusDays(5), EmployerProfileLastSaved.of(profile, List.of(link)));
    }

    @Test
    void linkChangeCanBeTheLatest() {
        var profile = EmployerProfileEntity.builder().build();
        profile.setModifiedAt(BASE);
        var link = ProfileSocialMediaLinkEntity.builder().build();
        link.setModifiedAt(BASE.plusDays(9));

        assertEquals(BASE.plusDays(9), EmployerProfileLastSaved.of(profile, List.of(link)));
    }

    @Test
    void toleratesMissingBasicInfoAndLinks() {
        assertNull(EmployerProfileLastSaved.of(EmployerProfileEntity.builder().build(), null));
    }
}
