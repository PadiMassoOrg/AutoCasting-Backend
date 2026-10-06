package com.padimasso.autocasting.application.admin.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.padimasso.autocasting.application.history.dto.HistoryChangeEntry;
import org.junit.jupiter.api.Test;

import com.padimasso.autocasting.application.admin.dto.response.AdminTalentProfileResponse;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import com.padimasso.autocasting.application.talent.dto.response.CreditResponse;
import com.padimasso.autocasting.application.talent.dto.response.EducationResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProfileChangeDiffTest {

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    private List<HistoryChangeEntry> diff(String section, Object before, Object after) {
        return ProfileChangeDiff.diff(mapper, section, mapper.valueToTree(before), mapper.valueToTree(after));
    }

    private static Map<String, Object> meta(String id, String code) {
        return Map.of("id", id, "stringCode", code, "categoryStringCode", "cat");
    }

    private static Map<String, Object> credit(String id, String project, String year, Map<String, Object> type) {
        return Map.of("id", id, "projectName", project, "producerName", "Producer", "role", "Lead", "year", year, "productionType", type, "modifiedAt", "t");
    }

    private static List<String> keys(List<HistoryChangeEntry> entries) {
        return entries.stream().map(HistoryChangeEntry::fieldKey).toList();
    }

    @Test
    void objectSection_returnsOnlyChangedFields() {
        var before = Map.of("id", "1", "stageName", "Old", "modifiedAt", "t1", "birthDate", "1990-01-01");
        var after = Map.of("id", "1", "stageName", "New", "modifiedAt", "t2", "birthDate", "1990-01-01");

        var result = diff("basicInfo", before, after);

        assertEquals(List.of("basicInfo.stageName"), keys(result));
        assertEquals("Old", result.get(0).previousValue());
        assertEquals("New", result.get(0).newValue());
    }

    @Test
    void identicalSections_returnNothing() {
        assertTrue(diff("contact", Map.of("id", "1", "phoneNumber", "123"), Map.of("id", "2", "phoneNumber", "123")).isEmpty());
    }

    @Test
    void emptyTextAndNullAreTheSame() {
        assertTrue(diff("characteristics", Map.of("shirtSize", ""), Map.of()).isEmpty());
    }

    @Test
    void skills_reportOnlyWhatWasAddedAndRemoved() {
        var before = List.of(meta("1", "skill.boxing"), meta("2", "skill.russian"), meta("3", "skill.chess"));
        var after = List.of(meta("1", "skill.boxing"), meta("3", "skill.chess"), meta("4", "skill.capoeira"));

        var result = diff("skills", before, after);

        assertEquals(List.of("skills.added", "skills.removed"), keys(result));
        assertNull(result.get(0).previousValue());
        assertEquals(List.of(Map.of("stringCode", "skill.capoeira")), result.get(0).newValue());
        assertEquals(List.of(Map.of("stringCode", "skill.russian")), result.get(1).previousValue());
        assertNull(result.get(1).newValue());
    }

    @Test
    void reorderedSkills_returnNothing() {
        var before = List.of(meta("1", "a"), meta("2", "b"));

        assertTrue(diff("skills", before, List.of(meta("2", "b"), meta("1", "a"))).isEmpty());
    }

    @Test
    void credits_nameAddedAndRemovedItemsOnly() {
        var type = meta("t1", "type.film");
        var before = List.of(credit("1", "Hamlet", "2020", type), credit("2", "Macbeth", "2018", type));
        var after = List.of(credit("1", "Hamlet", "2020", type), credit("3", "Othello", "2024", type));

        var result = diff("credits", before, after);

        assertEquals(List.of("credits.added", "credits.removed"), keys(result));
        assertEquals(List.of("Othello (2024)"), result.get(0).newValue());
        assertEquals(List.of("Macbeth (2018)"), result.get(1).previousValue());
    }

    @Test
    void credits_reportOnlyTheChangedFieldOfAnExistingItem() {
        var type = meta("t1", "type.film");
        var before = List.of(credit("1", "Hamlet", "2010", type));
        var after = List.of(credit("1", "Hamlet", "2012", type));

        var result = diff("credits", before, after);

        assertEquals(List.of("credits[Hamlet].year"), keys(result));
        assertEquals("2010", result.get(0).previousValue());
        assertEquals("2012", result.get(0).newValue());
    }

    @Test
    void credits_productionTypeChangeShowsCodesOnly() {
        var before = List.of(credit("1", "Hamlet", "2010", meta("t1", "type.film")));
        var after = List.of(credit("1", "Hamlet", "2010", meta("t2", "type.theatre")));

        var result = diff("credits", before, after);

        assertEquals(List.of("credits[Hamlet].productionType"), keys(result));
        assertEquals(Map.of("stringCode", "type.film"), result.get(0).previousValue());
        assertEquals(Map.of("stringCode", "type.theatre"), result.get(0).newValue());
    }

    @Test
    void education_usesTheCourseNameAsLabel() {
        var before = List.of(Map.of("id", "1", "institution", "NYU", "courseName", "Acting", "graduationYear", "2015"));
        var after = List.of(Map.of("id", "1", "institution", "NYU", "courseName", "Acting", "graduationYear", "2016"));

        assertEquals(List.of("education[Acting].graduationYear"), keys(diff("education", before, after)));
    }

    @Test
    void socialLinks_reportUrlChangesAndAdditionsByNetwork() {
        var instagram = Map.of("optionId", "o1", "stringCode", "sitemetadata.social_media.instagram", "url", "https://ig.com/a");
        var instagramChanged = Map.of("optionId", "o1", "stringCode", "sitemetadata.social_media.instagram", "url", "https://ig.com/b");
        var tiktok = Map.of("optionId", "o2", "stringCode", "sitemetadata.social_media.tiktok", "url", "https://tt.com/a");

        var result = diff("socialMedia", Map.of("links", List.of(instagram), "modifiedAt", "t"), Map.of("links", List.of(instagramChanged, tiktok), "modifiedAt", "t2"));

        assertEquals(List.of("socialMedia.links.added", "socialMedia.links[instagram].url"), keys(result));
        assertEquals(List.of(Map.of("stringCode", "sitemetadata.social_media.tiktok")), result.get(0).newValue());
        assertEquals("https://ig.com/a", result.get(1).previousValue());
    }

    @Test
    void professionsAndSingleMetadataValues_useCodes() {
        var before = Map.of("gender", meta("g1", "gender.female"), "professions", List.of(meta("p1", "prof.actor")));
        var after = Map.of("gender", meta("g2", "gender.male"), "professions", List.of(meta("p1", "prof.actor"), meta("p2", "prof.model")));

        var result = diff("basicInfo", before, after);

        assertEquals(List.of("basicInfo.gender", "basicInfo.professions.added"), keys(result).stream().sorted().toList());
        var gender = result.stream().filter(entry -> entry.fieldKey().equals("basicInfo.gender")).findFirst().orElseThrow();
        assertEquals(Map.of("stringCode", "gender.female"), gender.previousValue());
    }

    @Test
    void storageUrls_areShortenedToTheFileName() {
        var base = "https://abc.supabase.co/storage/v1/object/public/bucket/talent/p/media/headshot/";

        var result = diff("media", Map.of("headshotImageUrl", base + "1.webp"), Map.of("headshotImageUrl", base + "2.webp"));

        assertEquals("1.webp", result.get(0).previousValue());
        assertEquals("2.webp", result.get(0).newValue());
    }

    @Test
    void otherPictures_areComparedByPosition() {
        var result = diff("media", Map.of("otherPicturesUrl", List.of("https://x/storage/v1/object/public/b/1.webp")), Map.of("otherPicturesUrl", List.of("https://x/storage/v1/object/public/b/1.webp", "https://x/storage/v1/object/public/b/2.webp")));

        assertEquals(List.of("media.otherPicturesUrl[2]"), keys(result));
        assertNull(result.get(0).previousValue());
        assertEquals("2.webp", result.get(0).newValue());
    }

    @Test
    void sectionMissingBefore_returnsFieldEntries() {
        var result = diff("contact", null, Map.of("phoneNumber", "123"));

        assertEquals(List.of("contact.phoneNumber"), keys(result));
    }

    @Test
    void realResponseRecords_onlyTheOneAddedSkillAndTheOneChangedFieldAppear() {
        var boxing = new SiteMetadataObject(UUID.randomUUID(), "skill.boxing", "cat.sport");
        var chess = new SiteMetadataObject(UUID.randomUUID(), "skill.chess", "cat.sport");
        var cycling = new SiteMetadataObject(UUID.randomUUID(), "skill.cycling", "cat.sport");
        var film = new SiteMetadataObject(UUID.randomUUID(), "type.film", null);
        var creditId = UUID.randomUUID();
        var educationId = UUID.randomUUID();
        var otherCreditId = UUID.randomUUID();
        var saved = LocalDateTime.of(2026, 10, 6, 5, 31);
        var savedAgain = LocalDateTime.of(2026, 10, 6, 5, 32);

        var before = new AdminTalentProfileResponse(
            null, null, true, null, null, null, null, null, null,
            Set.of(boxing, chess),
            Set.of(new CreditResponse(creditId, film, "Hamlet", "BBC", "Lead", "2010", saved),
                new CreditResponse(otherCreditId, film, "Macbeth", "ITV", "King", "2018", saved)),
            Set.of(new EducationResponse(educationId, "NYU", "Acting", "2015", saved)),
            saved, saved, "a", saved, "a"
        );
        var after = new AdminTalentProfileResponse(
            null, null, true, null, null, null, null, null, null,
            Set.of(boxing, chess, cycling),
            Set.of(new CreditResponse(creditId, film, "Hamlet", "BBC", "Lead", "2012", savedAgain),
                new CreditResponse(otherCreditId, film, "Macbeth", "ITV", "King", "2018", savedAgain)),
            Set.of(new EducationResponse(educationId, "NYU", "Acting", "2016", savedAgain)),
            savedAgain, saved, "a", savedAgain, "a"
        );
        var beforeTree = mapper.valueToTree(before);
        var afterTree = mapper.valueToTree(after);

        var skills = ProfileChangeDiff.diff(mapper, "skills", beforeTree.get("skills"), afterTree.get("skills"));
        var credits = ProfileChangeDiff.diff(mapper, "credits", beforeTree.get("credits"), afterTree.get("credits"));
        var education = ProfileChangeDiff.diff(mapper, "education", beforeTree.get("education"), afterTree.get("education"));

        assertEquals(List.of("skills.added"), keys(skills));
        assertEquals(List.of(Map.of("stringCode", "skill.cycling")), skills.get(0).newValue());
        assertEquals(List.of("credits[Hamlet].year"), keys(credits));
        assertEquals(List.of("education[Acting].graduationYear"), keys(education));
    }
}
