package com.padimasso.autocasting.application.castings.controller;

import com.padimasso.autocasting.application.castings.dto.CastingRoleFilter;
import com.padimasso.autocasting.application.castings.dto.EmployerCastingsFilter;
import com.padimasso.autocasting.application.castings.dto.response.CastingResponse;
import com.padimasso.autocasting.application.castings.dto.response.EmployerCastingResponse;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingCardResponse;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingRolePublicCardResponse;
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

import static com.padimasso.autocasting.config.AppConstants.*;

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

    @Operation(summary = "Ver detalles del Casting para Employer", description = "Obtiene información del Casting por slug para un Employer", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(EMPLOYER_CASTING_URL + "/{slug}")
    public ResponseEntity<EmployerCastingResponse> getEmployerCastingDetails(@PathVariable String slug) {
        return ResponseEntity.ok(castingService.getDetailsForEmployerBySlug(slug));
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
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        var filter = new EmployerCastingsFilter(null);
        return ResponseEntity.ok(castingService.getMyCastings(filter, page, size));
    }

    // Casting Statuses:
    @Operation(
        summary = "PUBLISH Casting",
        description = "Publica un casting si pertenece al employer, es publishable y su deadline no está vencida.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(EMPLOYER_CASTING_URL + "/{castingId}/publish")
    public ResponseEntity<EmployerCastingResponse> publishCasting(@PathVariable UUID castingId) {
        return ResponseEntity.ok(castingService.publishCasting(castingId));
    }

    // Casting Database
    @Operation(summary = "Listado público de Roles (Casting Database)", description = "Permite buscar roles publicados (CastingRolePublicCardResponse) con filtros similares al Talent Database.")
    @GetMapping(AppConstants.CASTING_DATABASE_API_URL)
    public SliceResponse<CastingRolePublicCardResponse> searchPublicCastings(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "12") int size, @RequestParam(required = false) String roleName, @RequestParam(required = false) Integer ageMin, @RequestParam(required = false) Integer ageMax, @RequestParam(required = false, name = "genderId") List<String> genderIdTokens, @RequestParam(required = false, name = "ethnicityId") List<String> ethnicityIdTokens, @RequestParam(required = false, name = "professionId") List<UUID> professionIds, @RequestParam(required = false, defaultValue = "ANY") MatchMode professionsMode, @RequestParam(required = false) Integer heightMinCm, @RequestParam(required = false) Integer heightMaxCm, @RequestParam(required = false, name = "hairColorId") List<UUID> hairColorIds, @RequestParam(required = false, defaultValue = "ANY") MatchMode hairColorIdsMode, @RequestParam(required = false, name = "eyeColorId") List<UUID> eyeColorIds, @RequestParam(required = false, defaultValue = "ANY") MatchMode eyeColorIdsMode, @RequestParam(required = false) Boolean tattoo, @RequestParam(required = false) Boolean passport, @RequestParam(required = false) Boolean drivingLicense, @RequestParam(required = false, name = "skillId") List<UUID> skillIds, @RequestParam(required = false, defaultValue = "ANY") MatchMode skillsMode, @RequestParam(required = false, name = "projectTypeId") List<UUID> projectTypeIds, @RequestParam(required = false, name = "castingModalityId") List<UUID> castingModalityIds, @RequestParam(required = false) String locationText) {
        var filter = new CastingRoleFilter(roleName, ageMin, ageMax, genderIdTokens, ethnicityIdTokens, professionIds, professionsMode, heightMinCm, heightMaxCm, hairColorIds, hairColorIdsMode, eyeColorIds, eyeColorIdsMode, tattoo, passport, drivingLicense, skillIds, skillsMode, projectTypeIds, castingModalityIds, locationText);

        return castingRoleSearchService.search(filter, page, size);
    }

    @Operation(summary = "Ver detalles del Casting", description = "Obtiene información del Casting por slug", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(CASTING_DETAILS_URL + "/{slug}")
    public ResponseEntity<CastingResponse> getCastingDetails(@PathVariable String slug) {
        return ResponseEntity.ok(castingService.getDetailsBySlug(slug));
    }

}
