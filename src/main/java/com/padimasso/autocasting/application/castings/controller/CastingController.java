package com.padimasso.autocasting.application.castings.controller;

import com.padimasso.autocasting.application.castings.dto.CastingRoleFilter;
import com.padimasso.autocasting.application.castings.dto.EmployerCastingsFilter;
import com.padimasso.autocasting.application.castings.dto.response.CastingResponse;
import com.padimasso.autocasting.application.castings.dto.response.EmployerCastingEditorResponse;
import com.padimasso.autocasting.application.castings.dto.response.PublicCastingDetailsResponse;
import com.padimasso.autocasting.application.castings.dto.response.PublicCastingOverviewResponse;
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
@Tag(name = "Castings", description = "Operaciones relacionadas a los Castings de un empleador")
@SuppressWarnings("unused")
public class CastingController {

    private final CastingService castingService;
    private final CastingRoleSearchService castingRoleSearchService;
    private final CastingBasicInfoService castingBasicInfoService;

    // Employer
    @Operation(summary = "Creacion de un nuevo Casting", description = "Permite a un Employer crear un nuevo Casting (vacío).")
    @PostMapping(EMPLOYER_CASTINGS_URL)
    public ResponseEntity<String> createEmptyCasting() {
        return ResponseEntity.ok(castingService.createEmptyCasting());
    }

    @Operation(summary = "Casting editor bootstrap", description = "Devuelve IDs de secciones y estado para editar un casting", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(EMPLOYER_CASTING_URL + "/{slug}/editor")
    public ResponseEntity<EmployerCastingEditorResponse> getEmployerCastingEditor(@PathVariable String slug) {
        return ResponseEntity.ok(castingService.getCastingEditorBySlug(slug));
    }

    @Operation(
        summary = "Employer casting details",
        description = "Obtiene detalles completos del casting por slug. Solo owner.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(EMPLOYER_CASTING_URL + "/{slug}/details")
    public ResponseEntity<CastingResponse> getEmployerCastingDetails(@PathVariable String slug) {
        return ResponseEntity.ok(castingService.getEmployerCastingDetailsBySlug(slug));
    }

    @Operation(summary = "DELETE Casting", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping(AppConstants.EMPLOYER_CASTING_URL + "/{castingId}")
    public ResponseEntity<Void> deleteCastingRole(@PathVariable UUID castingId) {
        castingService.deleteCasting(castingId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listado de mis Castings", description = "Permite a un Employer ver sus Castings. CARDS")
    @GetMapping(EMPLOYER_CASTINGS_URL)
    public ResponseEntity<List<CastingCardResponse>> getMyCastingsCards(
        @RequestParam(required = false, name = "q") String q,
        @RequestParam(required = false, name = "projectTypeId") List<String> projectTypeIdTokens,
        @RequestParam(required = false, name = "statusId") List<String> statusIdTokens,
        @RequestParam(required = false, defaultValue = "CREATION_DATE_DESC") EmployerCastingsOrderBy orderBy,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        var filter = new EmployerCastingsFilter(null, q, projectTypeIdTokens, statusIdTokens, orderBy);
        return ResponseEntity.ok(castingService.getMyCastings(filter, page, size));
    }

    // Casting Statuses:
    @Operation(summary = "PUBLISH Casting", description = "Publica un casting si pertenece al employer, es publishable y su deadline no está vencida.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(EMPLOYER_CASTING_URL + "/{castingId}/publish")
    public ResponseEntity<EmployerCastingEditorResponse> publishCasting(@PathVariable UUID castingId) {
        return ResponseEntity.ok(castingService.publishCasting(castingId));
    }

    @Operation(summary = "Set Casting to DRAFT", description = "Vuelve el casting a DRAFT si pertenece al employer y la transición es válida.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(EMPLOYER_CASTING_URL + "/{castingId}/draft")
    public ResponseEntity<EmployerCastingEditorResponse> setDraft(@PathVariable UUID castingId) {
        return ResponseEntity.ok(castingService.setDraftCasting(castingId));
    }

    @Operation(summary = "PAUSE Casting", description = "Pausa un casting publicado.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(EMPLOYER_CASTING_URL + "/{castingId}/pause")
    public ResponseEntity<EmployerCastingEditorResponse> pause(@PathVariable UUID castingId) {
        return ResponseEntity.ok(castingService.pauseCasting(castingId));
    }

    @Operation(summary = "CLOSE Casting", description = "Cierra un casting.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(EMPLOYER_CASTING_URL + "/{castingId}/close")
    public ResponseEntity<EmployerCastingEditorResponse> close(@PathVariable UUID castingId) {
        return ResponseEntity.ok(castingService.closeCasting(castingId));
    }

    @Operation(summary = "ARCHIVE Casting", description = "Archiva un casting.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(EMPLOYER_CASTING_URL + "/{castingId}/archive")
    public ResponseEntity<EmployerCastingEditorResponse> archive(@PathVariable UUID castingId) {
        return ResponseEntity.ok(castingService.archiveCasting(castingId));
    }

    // Casting Database
    @Operation(summary = "Listado público de Roles (Casting Database)", description = "Permite buscar roles publicados (CastingRolePublicCardResponse) con filtros similares al Talent Database.")
    @GetMapping(AppConstants.CASTING_DATABASE_API_URL)
    public SliceResponse<CastingRolePublicCardResponse> searchPublicCastings(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "12") int size,
        @RequestParam(required = false) String roleName,
        @RequestParam(required = false) Integer ageMin,
        @RequestParam(required = false) Integer ageMax,
        @RequestParam(required = false, name = "genderId") List<String> genderIdTokens,
        @RequestParam(required = false, name = "ethnicityId") List<String> ethnicityIdTokens,
        @RequestParam(required = false, name = "professionId") List<UUID> professionIds,
        @RequestParam(required = false, defaultValue = "ANY") MatchMode professionsMode,
        @RequestParam(required = false) Integer heightMinCm,
        @RequestParam(required = false) Integer heightMaxCm,
        @RequestParam(required = false, name = "hairColorId") List<UUID> hairColorIds,
        @RequestParam(required = false, defaultValue = "ANY") MatchMode hairColorIdsMode,
        @RequestParam(required = false, name = "eyeColorId") List<UUID> eyeColorIds,
        @RequestParam(required = false, defaultValue = "ANY") MatchMode eyeColorIdsMode,
        @RequestParam(required = false) Boolean tattoo,
        @RequestParam(required = false) Boolean passport,
        @RequestParam(required = false) Boolean drivingLicense,
        @RequestParam(required = false, name = "skillId") List<UUID> skillIds,
        @RequestParam(required = false, defaultValue = "ANY") MatchMode skillsMode,
        @RequestParam(required = false, name = "projectTypeId") List<UUID> projectTypeIds,
        @RequestParam(required = false, name = "castingModalityId") List<UUID> castingModalityIds,
        @RequestParam(required = false) String locationText
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
        description = "Obtiene detalles del casting por slug pero devuelve SOLO el role seleccionado (en array)."
    )
    @GetMapping(AppConstants.CASTING_DETAILS_URL + "/{slug}/roles/{roleId}")
    public ResponseEntity<PublicCastingDetailsResponse> getPublicCastingDetailsBySlugAndRole(
        @PathVariable String slug,
        @PathVariable UUID roleId
    ) {
        return ResponseEntity.ok(castingService.getPublicCastingDetailsBySlugAndRoleId(slug, roleId));
    }

    @Operation(
        summary = "Public casting details by slug",
        description = "Obtiene detalles públicos completos del casting por slug, devolviendo todos los roles."
    )
    @GetMapping(AppConstants.CASTING_DETAILS_URL + "/{slug}")
    public ResponseEntity<PublicCastingOverviewResponse> getPublicCastingDetailsBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(castingService.getPublicCastingDetailsBySlug(slug));
    }

}
