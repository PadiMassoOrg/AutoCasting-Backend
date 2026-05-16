package com.padimasso.autocasting.application.castings.controller;

import com.padimasso.autocasting.application.castings.dto.CastingRoleFilter;
import com.padimasso.autocasting.application.castings.dto.EmployerCastingsFilter;
import com.padimasso.autocasting.application.castings.dto.request.CastingUpsertRequest;
import com.padimasso.autocasting.application.castings.dto.response.*;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingCardResponse;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingRolePublicCardResponse;
import com.padimasso.autocasting.application.castings.repository.order.EmployerCastingsOrderBy;
import com.padimasso.autocasting.application.castings.service.CastingRoleSearchService;
import com.padimasso.autocasting.application.castings.service.CastingService;
import com.padimasso.autocasting.application.common.dto.MatchMode;
import com.padimasso.autocasting.application.shared.web.SliceResponse;
import com.padimasso.autocasting.config.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.EMPLOYER_CASTINGS_EMPTY_URL;
import static com.padimasso.autocasting.config.AppConstants.EMPLOYER_CASTINGS_URL;
import static com.padimasso.autocasting.config.AppConstants.EMPLOYER_CASTING_URL;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Castings", description = "Endpoints for employer casting management and public casting discovery.")
@SuppressWarnings("unused")
public class CastingController {

    private final CastingService castingService;
    private final CastingRoleSearchService castingRoleSearchService;

    @Operation(summary = "Create empty casting", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(EMPLOYER_CASTINGS_EMPTY_URL)
    public ResponseEntity<String> createEmptyCasting() {
        return ResponseEntity.ok(castingService.createEmptyCasting());
    }

    @Operation(summary = "Create casting", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(EMPLOYER_CASTINGS_URL)
    public ResponseEntity<CastingResponse> createCasting(@Valid @RequestBody CastingUpsertRequest request) {
        return ResponseEntity.ok(castingService.createCasting(request));
    }

    @Operation(summary = "Update casting", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping(EMPLOYER_CASTING_URL + "/{castingId}")
    public ResponseEntity<CastingResponse> updateCasting(@PathVariable UUID castingId, @Valid @RequestBody CastingUpsertRequest request) {
        return ResponseEntity.ok(castingService.updateCasting(castingId, request));
    }

    @Operation(summary = "Employer casting editor", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(EMPLOYER_CASTING_URL + "/{slug}/editor")
    public ResponseEntity<EmployerCastingEditorResponse> getEmployerCastingEditor(@PathVariable String slug) {
        return ResponseEntity.ok(castingService.getCastingEditorBySlug(slug));
    }

    @Operation(summary = "Employer casting details", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(EMPLOYER_CASTING_URL + "/{slug}/details")
    public ResponseEntity<CastingResponse> getEmployerCastingDetails(@PathVariable String slug) {
        return ResponseEntity.ok(castingService.getEmployerCastingDetailsBySlug(slug));
    }

    @Operation(summary = "Employer casting checkout summary", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(EMPLOYER_CASTING_URL + "/{id}/checkout-summary")
    public ResponseEntity<EmployerCastingCheckoutSummaryResponse> getEmployerCastingCheckoutSummary(@PathVariable UUID id) {
        return ResponseEntity.ok(castingService.getEmployerCastingCheckoutSummary(id));
    }

    @Operation(summary = "Delete casting", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping(AppConstants.EMPLOYER_CASTING_URL + "/{castingId}")
    public ResponseEntity<Void> deleteCasting(@PathVariable UUID castingId) {
        castingService.deleteCasting(castingId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List my castings", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(EMPLOYER_CASTINGS_URL)
    public ResponseEntity<List<CastingCardResponse>> getMyCastingsCards(@RequestParam(required = false, name = "q") String q, @RequestParam(required = false, name = "projectTypeId") List<String> projectTypeIdTokens, @RequestParam(required = false, name = "statusId") List<String> statusIdTokens, @RequestParam(required = false, defaultValue = "CREATION_DATE_DESC") EmployerCastingsOrderBy orderBy, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        var filter = new EmployerCastingsFilter(null, q, projectTypeIdTokens, statusIdTokens, orderBy);
        return ResponseEntity.ok(castingService.getMyCastings(filter, page, size));
    }

    @PostMapping(EMPLOYER_CASTING_URL + "/{castingId}/publish")
    public ResponseEntity<EmployerCastingEditorResponse> publishCasting(@PathVariable UUID castingId) {
        return ResponseEntity.ok(castingService.publishCasting(castingId));
    }

    @PostMapping(EMPLOYER_CASTING_URL + "/{castingId}/draft")
    public ResponseEntity<EmployerCastingEditorResponse> setDraft(@PathVariable UUID castingId) {
        return ResponseEntity.ok(castingService.setDraftCasting(castingId));
    }

    @PostMapping(EMPLOYER_CASTING_URL + "/{castingId}/pause")
    public ResponseEntity<EmployerCastingEditorResponse> pause(@PathVariable UUID castingId) {
        return ResponseEntity.ok(castingService.pauseCasting(castingId));
    }

    @PostMapping(EMPLOYER_CASTING_URL + "/{castingId}/close")
    public ResponseEntity<EmployerCastingEditorResponse> close(@PathVariable UUID castingId) {
        return ResponseEntity.ok(castingService.closeCasting(castingId));
    }

    @PostMapping(EMPLOYER_CASTING_URL + "/{castingId}/archive")
    public ResponseEntity<EmployerCastingEditorResponse> archive(@PathVariable UUID castingId) {
        return ResponseEntity.ok(castingService.archiveCasting(castingId));
    }

    @GetMapping(AppConstants.CASTING_DATABASE_API_URL)
    public SliceResponse<CastingRolePublicCardResponse> searchPublicCastings(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "12") int size, @RequestParam(required = false) String roleName, @RequestParam(required = false) Integer ageMin, @RequestParam(required = false) Integer ageMax, @RequestParam(required = false, name = "genderId") List<String> genderIdTokens, @RequestParam(required = false, name = "ethnicityId") List<String> ethnicityIdTokens, @RequestParam(required = false, name = "professionId") List<UUID> professionIds, @RequestParam(required = false, defaultValue = "ANY") MatchMode professionsMode, @RequestParam(required = false) Integer heightMinCm, @RequestParam(required = false) Integer heightMaxCm, @RequestParam(required = false, name = "hairColorId") List<UUID> hairColorIds, @RequestParam(required = false, defaultValue = "ANY") MatchMode hairColorIdsMode, @RequestParam(required = false, name = "eyeColorId") List<UUID> eyeColorIds, @RequestParam(required = false, defaultValue = "ANY") MatchMode eyeColorIdsMode, @RequestParam(required = false) Boolean tattoo, @RequestParam(required = false) Boolean passport, @RequestParam(required = false) Boolean drivingLicense, @RequestParam(required = false, name = "skillId") List<UUID> skillIds, @RequestParam(required = false, defaultValue = "ANY") MatchMode skillsMode, @RequestParam(required = false, name = "projectTypeId") List<UUID> projectTypeIds, @RequestParam(required = false, name = "castingModalityId") List<UUID> castingModalityIds, @RequestParam(required = false) String locationText) {
        return castingRoleSearchService.search(new CastingRoleFilter(roleName, ageMin, ageMax, genderIdTokens, ethnicityIdTokens, professionIds, professionsMode, heightMinCm, heightMaxCm, hairColorIds, hairColorIdsMode, eyeColorIds, eyeColorIdsMode, tattoo, passport, drivingLicense, skillIds, skillsMode, projectTypeIds, castingModalityIds, locationText), page, size);
    }

    @GetMapping(AppConstants.CASTING_DETAILS_URL + "/{slug}/roles/{roleId}")
    public ResponseEntity<PublicCastingDetailsResponse> getPublicCastingDetailsBySlugAndRole(@PathVariable String slug, @PathVariable UUID roleId) {
        return ResponseEntity.ok(castingService.getPublicCastingDetailsBySlugAndRoleId(slug, roleId));
    }

    @GetMapping(AppConstants.CASTING_DETAILS_URL + "/{slug}")
    public ResponseEntity<PublicCastingOverviewResponse> getPublicCastingDetailsBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(castingService.getPublicCastingDetailsBySlug(slug));
    }
}
