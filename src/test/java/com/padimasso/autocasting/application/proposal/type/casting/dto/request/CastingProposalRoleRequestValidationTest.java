package com.padimasso.autocasting.application.proposal.type.casting.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CastingProposalRoleRequestValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private CastingProposalRoleRequest role(UUID genderId, short ageMin, short ageMax, Set<UUID> professionIds,
                                            UUID payRateTypeId, String description, String remunerationNotes) {
        return new CastingProposalRoleRequest(
            null, "Protagonista", UUID.randomUUID(), genderId, ageMin, ageMax, description, professionIds, Set.of(),
            payRateTypeId, null, null, remunerationNotes, false, false, null, null, null, null, null
        );
    }

    private CastingProposalRoleRequest validRole() {
        return role(UUID.randomUUID(), (short) 20, (short) 30, Set.of(UUID.randomUUID()), UUID.randomUUID(), null, null);
    }

    private Set<String> messages(CastingProposalRoleRequest request) {
        return validator.validate(request).stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
    }

    @Test
    void validRole_hasNoViolations() {
        assertTrue(messages(validRole()).isEmpty());
    }

    @Test
    void missingGender_isRejected() {
        var request = role(null, (short) 20, (short) 30, Set.of(UUID.randomUUID()), UUID.randomUUID(), null, null);
        assertEquals(Set.of("casting.gender_required"), messages(request));
    }

    @Test
    void missingPayRateType_isRejected() {
        var request = role(UUID.randomUUID(), (short) 20, (short) 30, Set.of(UUID.randomUUID()), null, null, null);
        assertEquals(Set.of("casting.pay_rate_type_required"), messages(request));
    }

    @Test
    void noProfessions_isRejected() {
        var request = role(UUID.randomUUID(), (short) 20, (short) 30, Set.of(), UUID.randomUUID(), null, null);
        assertEquals(Set.of("talent.professions_required"), messages(request));
    }

    @Test
    void ageAbove99_isRejected() {
        var request = role(UUID.randomUUID(), (short) 20, (short) 100, Set.of(UUID.randomUUID()), UUID.randomUUID(), null, null);
        assertEquals(Set.of("casting.age_max"), messages(request));
    }

    @Test
    void descriptionOver2000Chars_isRejected() {
        var request = role(UUID.randomUUID(), (short) 20, (short) 30, Set.of(UUID.randomUUID()), UUID.randomUUID(), "a".repeat(2001), null);
        assertEquals(Set.of("casting.description_max_length"), messages(request));
    }

    @Test
    void remunerationNotesOver2000Chars_isRejected() {
        var request = role(UUID.randomUUID(), (short) 20, (short) 30, Set.of(UUID.randomUUID()), UUID.randomUUID(), null, "a".repeat(2001));
        assertEquals(Set.of("casting.remuneration_notes_max_length"), messages(request));
    }
}
