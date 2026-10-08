package com.padimasso.autocasting.application.admin.dto.request;

import com.padimasso.autocasting.application.castings.dto.request.CastingRoleRequest;
import com.padimasso.autocasting.application.castings.dto.request.CastingUpsertRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The largest values the Admin casting schemas accept (castingBasicInfoSchema.ts, castingRoleSchema.ts)
 * must also pass the Backend's bean validation, so a save the Admin approves is never rejected by the server.
 */
class AdminCastingUpdateRequestValidationTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
    private static final String MAX_TEXT = "x".repeat(255);

    private static java.util.List<String> violations(Object request) {
        return VALIDATOR.validate(request).stream().map(v -> v.getPropertyPath() + ": " + v.getMessage()).toList();
    }

    private static CastingUpsertRequest casting(String title, String description, LocalDate start, LocalDate end) {
        return new CastingUpsertRequest(title, null, null, MAX_TEXT, null, true, MAX_TEXT, start, end, description);
    }

    private static CastingRoleRequest role(String roleName, Short ageMin, Short ageMax, String requirementDescription) {
        return new CastingRoleRequest(
            UUID.randomUUID(), roleName, UUID.randomUUID(), null, ageMin, ageMax, null, Set.of(), Set.of(), UUID.randomUUID(),
            null, BigDecimal.TEN, null, false, false, requirementDescription, null, null, null, null, null
        );
    }

    @Test
    void castingAtItsLimitsPassesBackendValidation() {
        var day = LocalDate.of(2030, 1, 1);

        assertEquals(java.util.List.of(), violations(new AdminCastingUpdateRequest("reason", casting(MAX_TEXT, "x".repeat(3000), day, day))));
    }

    @Test
    void roleAtItsLimitsPassesBackendValidation() {
        assertEquals(java.util.List.of(), violations(new AdminCastingRoleUpdateRequest("reason", role(MAX_TEXT, (short) 0, (short) 99, "x".repeat(3000)))));
    }

    @Test
    void reasonAndPayloadAreRequired() {
        var noReason = violations(new AdminCastingUpdateRequest(" ", casting("T", null, null, null)));
        var noPayload = violations(new AdminCastingRoleUpdateRequest("reason", null));

        assertTrue(noReason.stream().anyMatch(v -> v.startsWith("reason")));
        assertTrue(noPayload.stream().anyMatch(v -> v.startsWith("role")));
    }

    @Test
    void nestedCastingAndRoleRulesAreEnforced() {
        var shootingBackwards = violations(new AdminCastingUpdateRequest("r", casting("T", null, LocalDate.of(2030, 2, 1), LocalDate.of(2030, 1, 1))));
        var ageBackwards = violations(new AdminCastingRoleUpdateRequest("r", role("A", (short) 40, (short) 30, null)));
        var blankTitle = violations(new AdminCastingUpdateRequest("r", casting(" ", null, null, null)));

        assertTrue(shootingBackwards.stream().anyMatch(v -> v.contains("shooting_date_range_invalid")));
        assertTrue(ageBackwards.stream().anyMatch(v -> v.contains("age_range_invalid")));
        assertTrue(blankTitle.stream().anyMatch(v -> v.contains("title_required")));
    }

    @Test
    void roleCreateDuplicateAndDeleteRequireAReason() {
        assertTrue(violations(new AdminCastingRoleCreateRequest(" ", role("A", (short) 18, (short) 30, null)))
            .stream().anyMatch(v -> v.startsWith("reason")));
        assertTrue(violations(new AdminCastingRoleDuplicateRequest("", null)).stream().anyMatch(v -> v.startsWith("reason")));
        assertTrue(violations(new AdminCastingRoleDeleteRequest(null)).stream().anyMatch(v -> v.startsWith("reason")));
    }

    @Test
    void roleCreateAppliesTheSameRoleRulesAsUpdate() {
        assertEquals(java.util.List.of(), violations(new AdminCastingRoleCreateRequest("r", role(MAX_TEXT, (short) 0, (short) 99, "x".repeat(3000)))));
        assertTrue(violations(new AdminCastingRoleCreateRequest("r", role("A", (short) 40, (short) 30, null)))
            .stream().anyMatch(v -> v.contains("age_range_invalid")));
        assertTrue(violations(new AdminCastingRoleCreateRequest("r", null)).stream().anyMatch(v -> v.startsWith("role")));
    }

    @Test
    void duplicateRoleNameIsOptionalButBounded() {
        assertEquals(java.util.List.of(), violations(new AdminCastingRoleDuplicateRequest("r", null)));
        assertEquals(java.util.List.of(), violations(new AdminCastingRoleDuplicateRequest("r", MAX_TEXT)));
        assertTrue(violations(new AdminCastingRoleDuplicateRequest("r", "x".repeat(256)))
            .stream().anyMatch(v -> v.contains("role_name_max_length")));
    }
}
