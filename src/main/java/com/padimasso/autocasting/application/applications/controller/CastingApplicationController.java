package com.padimasso.autocasting.application.applications.controller;

import com.padimasso.autocasting.application.applications.dto.EmployerCastingApplicantsFilter;
import com.padimasso.autocasting.application.applications.dto.TalentCastingApplicationsFilter;
import com.padimasso.autocasting.application.applications.dto.request.CastingApplicationRequest;
import com.padimasso.autocasting.application.applications.dto.response.EmployerCastingApplicantCardResponse;
import com.padimasso.autocasting.application.applications.dto.response.TalentCastingApplicationCardResponse;
import com.padimasso.autocasting.application.applications.repository.order.EmployerCastingApplicantsOrderBy;
import com.padimasso.autocasting.application.applications.repository.order.TalentCastingApplicationsOrderBy;
import com.padimasso.autocasting.application.applications.service.CastingApplicationService;
import com.padimasso.autocasting.application.shared.web.SliceResponse;
import com.padimasso.autocasting.config.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Casting Application", description = "Operaciones relacionadas a los Aplicantes de un Casting")
@SuppressWarnings("unused")
public class CastingApplicationController {

    private final CastingApplicationService castingApplicationService;

    //   Talent
    @Operation(
        summary = "Apply to a casting role",
        description = "Crea una Application para un Role. Si el Role tiene requirements, requiere submissions.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(AppConstants.TALENT_CASTING_APPLICATION_URL + "/{roleId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void apply(
        @PathVariable UUID roleId,
        @Valid @RequestBody(required = false) CastingApplicationRequest request
    ) {
        castingApplicationService.apply(roleId, request);
    }

    @Operation(
        summary = "My applications (Talent)",
        description = "Listado de roles/castings a los que el talento aplicó. No expone entity application.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(AppConstants.TALENT_CASTING_APPLICATIONS_URL)
    public SliceResponse<TalentCastingApplicationCardResponse> getMyApplications(
        @RequestParam(required = false, name = "q") String q,
        @RequestParam(required = false, name = "castingStatusId") List<String> castingStatusIdTokens,
        @RequestParam(required = false, name = "projectTypeId") List<String> projectTypeIdTokens,
        @RequestParam(required = false, name = "modalityId") List<String> modalityIdTokens,
        @RequestParam(required = false, defaultValue = "CREATION_DATE_DESC") TalentCastingApplicationsOrderBy orderBy,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "8") int size
    ) {
        var filter = new TalentCastingApplicationsFilter(
            null,
            q,
            castingStatusIdTokens,
            projectTypeIdTokens,
            modalityIdTokens,
            orderBy
        );
        return castingApplicationService.getTalentCastingApplications(filter, page, size);
    }

    //    Employer
    @Operation(
        summary = "Applicants by casting (Employer)",
        description = "Listado de aplicantes por castingSlug (todas las roles).",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(AppConstants.EMPLOYER_CASTING_URL + "/{slug}/applicants")
    public SliceResponse<EmployerCastingApplicantCardResponse> getApplicantsByCasting(
        @PathVariable String slug,
        @RequestParam(required = false, name = "q") String q,
        @RequestParam(required = false, name = "applicationStatusId") List<String> applicationStatusIdTokens,
        @RequestParam(required = false, name = "professionId") List<UUID> professionIds,
        @RequestParam(required = false, defaultValue = "CREATION_DATE_DESC") EmployerCastingApplicantsOrderBy orderBy,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "8") int size
    ) {
        var filter = new EmployerCastingApplicantsFilter(
            null,
            slug,
            q,
            applicationStatusIdTokens,
            professionIds,
            orderBy
        );
        return castingApplicationService.getEmployerCastingApplicants(filter, page, size);
    }

    // Casting Application Statuses
    @Operation(
        summary = "Set application to PRESELECTED",
        description = "Marca la Application como PRESELECTED (solo owner employer y transición válida).",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(AppConstants.EMPLOYER_CASTING_APPLICATIONS_URL + "/{applicationId}/preselect")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void preselect(@PathVariable UUID applicationId) {
        castingApplicationService.preselectCastingApplication(applicationId);
    }

    @Operation(
        summary = "Set application to SELECTED",
        description = "Marca la Application como SELECTED (solo owner employer y transición válida).",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(AppConstants.EMPLOYER_CASTING_APPLICATIONS_URL + "/{applicationId}/select")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void select(@PathVariable UUID applicationId) {
        castingApplicationService.selectCastingApplication(applicationId);
    }

    @Operation(
        summary = "Set application to VIEWED",
        description = "Marca la Application como VIEWED (solo owner employer).",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(AppConstants.EMPLOYER_CASTING_APPLICATIONS_URL + "/{applicationId}/view")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void view(@PathVariable UUID applicationId) {
        castingApplicationService.viewCastingApplication(applicationId);
    }

    @Operation(
        summary = "Set application to NOT_PROCEEDING",
        description = "Marca la Application como NOT_PROCEEDING (solo owner employer y transición válida).",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(AppConstants.EMPLOYER_CASTING_APPLICATIONS_URL + "/{applicationId}/not-proceeding")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void notProceeding(@PathVariable UUID applicationId) {
        castingApplicationService.notProceedingCastingApplication(applicationId);
    }

    @Operation(
        summary = "Reset application status to BLANK",
        description = "Resetea la Application a BLANK (solo owner employer y transición válida).",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(AppConstants.EMPLOYER_CASTING_APPLICATIONS_URL + "/{applicationId}/blank")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void blank(@PathVariable UUID applicationId) {
        castingApplicationService.blankCastingApplication(applicationId);
    }

}
