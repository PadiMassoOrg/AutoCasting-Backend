package com.padimasso.autocasting.application.talent.util;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import com.padimasso.autocasting.application.talent.model.BasicInfoEntity;
import com.padimasso.autocasting.application.talent.model.CharacteristicsEntity;
import com.padimasso.autocasting.application.talent.model.ContactEntity;
import com.padimasso.autocasting.application.talent.model.CreditEntity;
import com.padimasso.autocasting.application.talent.model.EducationEntity;
import com.padimasso.autocasting.application.talent.model.MediaEntity;
import com.padimasso.autocasting.application.talent.model.ProfileSocialMediaLinkEntity;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TalentProfileLastSavedTest {

    private static final LocalDateTime BASE = LocalDateTime.of(2026, 1, 1, 10, 0);

    @Test
    void picksProfileTimestampWhenItIsTheLatest() {
        var profile = profileWithSections();
        profile.setModifiedAt(BASE.plusDays(30));

        assertEquals(BASE.plusDays(30), TalentProfileLastSaved.of(profile, List.of()));
    }

    @Test
    void picksLatestCreditChange() {
        var profile = profileWithSections();
        profile.setCredits(Set.of(modified(CreditEntity.builder().build(), BASE.plusDays(40))));

        assertEquals(BASE.plusDays(40), TalentProfileLastSaved.of(profile, List.of()));
    }

    @Test
    void picksLatestEducationChange() {
        var profile = profileWithSections();
        profile.setEducation(Set.of(modified(EducationEntity.builder().build(), BASE.plusDays(50))));

        assertEquals(BASE.plusDays(50), TalentProfileLastSaved.of(profile, List.of()));
    }

    @Test
    void picksLatestSocialMediaLinkChange() {
        var profile = profileWithSections();
        var link = modified(ProfileSocialMediaLinkEntity.builder().build(), BASE.plusDays(60));

        assertEquals(BASE.plusDays(60), TalentProfileLastSaved.of(profile, List.of(link)));
    }

    @Test
    void picksLatestSectionChange() {
        var profile = profileWithSections();
        profile.getMedia().setModifiedAt(BASE.plusDays(20));

        assertEquals(BASE.plusDays(20), TalentProfileLastSaved.of(profile, List.of()));
    }

    @Test
    void toleratesMissingSections() {
        var profile = TalentProfileEntity.builder().build();

        assertNull(TalentProfileLastSaved.of(profile, null));
    }

    private static TalentProfileEntity profileWithSections() {
        var profile = TalentProfileEntity.builder()
            .basicInfo(modified(BasicInfoEntity.builder().build(), BASE.plusDays(1)))
            .contact(modified(ContactEntity.builder().build(), BASE.plusDays(2)))
            .media(modified(MediaEntity.builder().build(), BASE.plusDays(3)))
            .characteristics(modified(CharacteristicsEntity.builder().build(), BASE.plusDays(4)))
            .build();
        profile.setModifiedAt(BASE);
        return profile;
    }

    private static <T extends AuditableEntity> T modified(T entity, LocalDateTime modifiedAt) {
        entity.setModifiedAt(modifiedAt);
        return entity;
    }
}
