package com.padimasso.autocasting.application.castings.controller;

import com.padimasso.autocasting.application.castings.dto.CastingRoleFilter;
import com.padimasso.autocasting.application.castings.dto.EmployerCastingsFilter;
import com.padimasso.autocasting.application.castings.dto.response.*;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingCardResponse;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingRolePublicCardResponse;
import com.padimasso.autocasting.application.castings.repository.order.EmployerCastingsOrderBy;
import com.padimasso.autocasting.application.castings.service.CastingBasicInfoService;
import com.padimasso.autocasting.application.castings.service.CastingRoleSearchService;
import com.padimasso.autocasting.application.castings.service.CastingService;
import com.padimasso.autocasting.application.common.dto.MatchMode;
import com.padimasso.autocasting.application.shared.web.SliceResponse;
import com.padimasso.autocasting.config.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
    private final CastingBasicInfoService castingBasicInfoService;

    // Employer
    @Operation(
        summary = "Create empty casting",
        description = "Creates a new empty casting for the authenticated employer.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(EMPLOYER_CASTINGS_URL)
    public ResponseEntity<String> createEmptyCasting() {
        return ResponseEntity.ok(castingService.createEmptyCasting());
    }

    @Operation(
        summary = "Get casting editor bootstrap",
        description = "Returns section IDs and statuses required to edit a casting.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(EMPLOYER_CASTING_URL + "/{slug}/editor")
    public ResponseEntity<EmployerCastingEditorResponse> getEmployerCastingEditor(
        @Parameter(description = "Casting slug.") @PathVariable String slug
    ) {
        return ResponseEntity.ok(castingService.getCastingEditorBySlug(slug));
    }

    @Operation(
        summary = "Employer casting details",
        description = "Returns full casting details by slug (owner employer only).",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(EMPLOYER_CASTING_URL + "/{slug}/details")
    public ResponseEntity<CastingResponse> getEmployerCastingDetails(
        @Parameter(description = "Casting slug.") @PathVariable String slug
    ) {
        return ResponseEntity.ok(castingService.getEmployerCastingDetailsBySlug(slug));
    }

    @Operation(
        summary = "Employer casting checkout summary",
        description = "Returns the minimum casting summary needed for checkout/publication (owner employer only).",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(EMPLOYER_CASTING_URL + "/{id}/checkout-summary")
    public ResponseEntity<EmployerCastingCheckoutSummaryResponse> getEmployerCastingCheckoutSummary(
        @Parameter(description = "Casting ID.") @PathVariable UUID id
    ) {
        return ResponseEntity.ok(castingService.getEmployerCastingCheckoutSummary(id));
    }

    @Operation(
        summary = "Delete casting",
        description = "Soft-deletes a casting by ID for the authenticated employer owner.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping(AppConstants.EMPLOYER_CASTING_URL + "/{castingId}")
    public ResponseEntity<Void> deleteCastingRole(
        @Parameter(description = "Casting ID.") @PathVariable UUID castingId
    ) {
        castingService.deleteCasting(castingId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "List my castings",
        description = "Returns paginated casting cards for the authenticated employer.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(EMPLOYER_CASTINGS_URL)
    public ResponseEntity<List<CastingCardResponse>> getMyCastingsCards(
        @Parameter(description = "Free-text search query.") @RequestParam(required = false, name = "q") String q,
        @Parameter(description = "Project type token filters.") @RequestParam(required = false, name = "projectTypeId") List<String> projectTypeIdTokens,
        @Parameter(description = "Casting status token filters.") @RequestParam(required = false, name = "statusId") List<String> statusIdTokens,
        @Parameter(description = "Sorting strategy.") @RequestParam(required = false, defaultValue = "CREATION_DATE_DESC") EmployerCastingsOrderBy orderBy,
        @Parameter(description = "Page index, starting from 0.") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size.") @RequestParam(defaultValue = "10") int size
    ) {
        var filter = new EmployerCastingsFilter(null, q, projectTypeIdTokens, statusIdTokens, orderBy);
        return ResponseEntity.ok(castingService.getMyCastings(filter, page, size));
    }

    // Casting Statuses:
    @Operation(summary = "Publish casting", description = "Publishes a casting if ownership, state transitions, and publishability checks pass.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(EMPLOYER_CASTING_URL + "/{castingId}/publish")
    public ResponseEntity<EmployerCastingEditorResponse> publishCasting(
        @Parameter(description = "Casting ID.") @PathVariable UUID castingId
    ) {
        return ResponseEntity.ok(castingService.publishCasting(castingId));
    }

    @Operation(summary = "Set casting to DRAFT", description = "Sets a casting back to DRAFT if ownership and transition checks pass.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(EMPLOYER_CASTING_URL + "/{castingId}/draft")
    public ResponseEntity<EmployerCastingEditorResponse> setDraft(
        @Parameter(description = "Casting ID.") @PathVariable UUID castingId
    ) {
        return ResponseEntity.ok(castingService.setDraftCasting(castingId));
    }

    @Operation(summary = "Pause casting", description = "Pauses a published casting.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(EMPLOYER_CASTING_URL + "/{castingId}/pause")
    public ResponseEntity<EmployerCastingEditorResponse> pause(
        @Parameter(description = "Casting ID.") @PathVariable UUID castingId
    ) {
        return ResponseEntity.ok(castingService.pauseCasting(castingId));
    }

    @Operation(summary = "Close casting", description = "Closes a casting.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(EMPLOYER_CASTING_URL + "/{castingId}/close")
    public ResponseEntity<EmployerCastingEditorResponse> close(
        @Parameter(description = "Casting ID.") @PathVariable UUID castingId
    ) {
        return ResponseEntity.ok(castingService.closeCasting(castingId));
    }

    @Operation(summary = "Archive casting", description = "Archives a casting.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(EMPLOYER_CASTING_URL + "/{castingId}/archive")
    public ResponseEntity<EmployerCastingEditorResponse> archive(
        @Parameter(description = "Casting ID.") @PathVariable UUID castingId
    ) {
        return ResponseEntity.ok(castingService.archiveCasting(castingId));
    }

    // Casting Database
    @Operation(summary = "Search public casting roles", description = "Returns paginated public casting-role cards using advanced filters.")
    @GetMapping(AppConstants.CASTING_DATABASE_API_URL)
    public SliceResponse<CastingRolePublicCardResponse> searchPublicCastings(
        @Parameter(description = "Page index, starting from 0.") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size.") @RequestParam(defaultValue = "12") int size,
        @Parameter(description = "Role name filter.") @RequestParam(required = false) String roleName,
        @Parameter(description = "Minimum age filter.") @RequestParam(required = false) Integer ageMin,
        @Parameter(description = "Maximum age filter.") @RequestParam(required = false) Integer ageMax,
        @Parameter(description = "Gender token filters.") @RequestParam(required = false, name = "genderId") List<String> genderIdTokens,
        @Parameter(description = "Ethnicity token filters.") @RequestParam(required = false, name = "ethnicityId") List<String> ethnicityIdTokens,
        @Parameter(description = "Profession IDs filter.") @RequestParam(required = false, name = "professionId") List<UUID> professionIds,
        @Parameter(description = "Match mode for profession IDs.") @RequestParam(required = false, defaultValue = "ANY") MatchMode professionsMode,
        @Parameter(description = "Minimum height in cm.") @RequestParam(required = false) Integer heightMinCm,
        @Parameter(description = "Maximum height in cm.") @RequestParam(required = false) Integer heightMaxCm,
        @Parameter(description = "Hair color IDs filter.") @RequestParam(required = false, name = "hairColorId") List<UUID> hairColorIds,
        @Parameter(description = "Match mode for hair color IDs.") @RequestParam(required = false, defaultValue = "ANY") MatchMode hairColorIdsMode,
        @Parameter(description = "Eye color IDs filter.") @RequestParam(required = false, name = "eyeColorId") List<UUID> eyeColorIds,
        @Parameter(description = "Match mode for eye color IDs.") @RequestParam(required = false, defaultValue = "ANY") MatchMode eyeColorIdsMode,
        @Parameter(description = "Tattoo filter.") @RequestParam(required = false) Boolean tattoo,
        @Parameter(description = "Passport filter.") @RequestParam(required = false) Boolean passport,
        @Parameter(description = "Driving license filter.") @RequestParam(required = false) Boolean drivingLicense,
        @Parameter(description = "Skill IDs filter.") @RequestParam(required = false, name = "skillId") List<UUID> skillIds,
        @Parameter(description = "Match mode for skill IDs.") @RequestParam(required = false, defaultValue = "ANY") MatchMode skillsMode,
        @Parameter(description = "Project type IDs filter.") @RequestParam(required = false, name = "projectTypeId") List<UUID> projectTypeIds,
        @Parameter(description = "Casting modality IDs filter.") @RequestParam(required = false, name = "castingModalityId") List<UUID> castingModalityIds,
        @Parameter(description = "Location text filter.") @RequestParam(required = false) String locationText
    ) {
        var filter = new CastingRoleFilter(
            roleName,
            ageMin,
            ageMax,
            genderIdTokens,
            ethnicityIdTokens,
            professionIds,
            professionsMode,
            heightMinCm,
            heightMaxCm,
            hairColorIds,
            hairColorIdsMode,
            eyeColorIds,
            eyeColorIdsMode,
            tattoo,
            passport,
            drivingLicense,
            skillIds,
            skillsMode,
            projectTypeIds,
            castingModalityIds,
            locationText);

        return castingRoleSearchService.search(filter, page, size);
    }

    @Operation(
        summary = "Public casting details by slug and role",
        description = "Returns public casting details by slug, including only the selected role in the response."
    )
    @GetMapping(AppConstants.CASTING_DETAILS_URL + "/{slug}/roles/{roleId}")
    public ResponseEntity<PublicCastingDetailsResponse> getPublicCastingDetailsBySlugAndRole(
        @Parameter(description = "Casting slug.") @PathVariable String slug,
        @Parameter(description = "Casting role ID.") @PathVariable UUID roleId
    ) {
        return ResponseEntity.ok(castingService.getPublicCastingDetailsBySlugAndRoleId(slug, roleId));
    }

    @Operation(
        summary = "Public casting details by slug",
        description = "Returns full public casting details by slug, including all roles."
    )
    @GetMapping(AppConstants.CASTING_DETAILS_URL + "/{slug}")
    public ResponseEntity<PublicCastingOverviewResponse> getPublicCastingDetailsBySlug(
        @Parameter(description = "Casting slug.") @PathVariable String slug
    ) {
        return ResponseEntity.ok(castingService.getPublicCastingDetailsBySlug(slug));
    }

}
