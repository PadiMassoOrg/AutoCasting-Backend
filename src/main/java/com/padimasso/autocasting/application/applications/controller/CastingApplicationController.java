package com.padimasso.autocasting.application.applications.controller;

import com.padimasso.autocasting.application.applications.dto.EmployerCastingApplicantsFilter;
import com.padimasso.autocasting.application.applications.dto.TalentCastingApplicationsFilter;
import com.padimasso.autocasting.application.applications.dto.request.CastingApplicationRequest;
import com.padimasso.autocasting.application.applications.dto.response.EmployerCastingApplicantCardResponse;
import com.padimasso.autocasting.application.applications.dto.response.EmployerCastingApplicantsGroupedResponse;
import com.padimasso.autocasting.application.applications.dto.response.TalentCastingApplicationCardResponse;
import com.padimasso.autocasting.application.applications.repository.order.EmployerCastingApplicantsOrderBy;
import com.padimasso.autocasting.application.applications.repository.order.TalentCastingApplicationsOrderBy;
import com.padimasso.autocasting.application.applications.service.CastingApplicationService;
import com.padimasso.autocasting.application.shared.web.SliceResponse;
import com.padimasso.autocasting.config.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@Tag(name = "Casting Applications", description = "Endpoints for submitting and managing casting applications.")
@SuppressWarnings("unused")
public class CastingApplicationController {

    private final CastingApplicationService castingApplicationService;

    //   Talent
    @Operation(
        summary = "Apply to a casting role",
        description = "Creates an application for a role. If the role has requirements, submissions are required.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(AppConstants.TALENT_CASTING_APPLICATION_URL + "/{roleId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void apply(
        @Parameter(description = "Casting role ID.") @PathVariable UUID roleId,
        @Valid @RequestBody(required = false) CastingApplicationRequest request
    ) {
        castingApplicationService.apply(roleId, request);
    }

    @Operation(
        summary = "List my applications (Talent)",
        description = "Returns paginated role/casting cards for applications submitted by the authenticated talent.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(AppConstants.TALENT_CASTING_APPLICATIONS_URL)
    public SliceResponse<TalentCastingApplicationCardResponse> getMyApplications(
        @Parameter(description = "Free-text search query.") @RequestParam(required = false, name = "q") String q,
        @Parameter(description = "Casting status token filters.") @RequestParam(required = false, name = "castingStatusId") List<String> castingStatusIdTokens,
        @Parameter(description = "Project type token filters.") @RequestParam(required = false, name = "projectTypeId") List<String> projectTypeIdTokens,
        @Parameter(description = "Casting modality token filters.") @RequestParam(required = false, name = "modalityId") List<String> modalityIdTokens,
        @Parameter(description = "Sorting strategy.") @RequestParam(required = false, defaultValue = "CREATION_DATE_DESC") TalentCastingApplicationsOrderBy orderBy,
        @Parameter(description = "Page index, starting from 0.") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size.") @RequestParam(defaultValue = "8") int size
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
        summary = "List applicants by casting (Employer)",
        description = "Returns paginated applicants for a casting slug across all roles.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(AppConstants.EMPLOYER_CASTING_URL + "/{slug}/applicants")
    public SliceResponse<EmployerCastingApplicantCardResponse> getApplicantsByCasting(
        @Parameter(description = "Casting slug.") @PathVariable String slug,
        @Parameter(description = "Free-text search query.") @RequestParam(required = false, name = "q") String q,
        @Parameter(description = "Optional casting role ID filter.") @RequestParam(required = false, name = "roleId") UUID roleId,
        @Parameter(description = "Application status token filters.") @RequestParam(required = false, name = "applicationStatusId") List<String> applicationStatusIdTokens,
        @Parameter(description = "Profession ID filters.") @RequestParam(required = false, name = "professionId") List<UUID> professionIds,
        @Parameter(description = "Sorting strategy.") @RequestParam(required = false, defaultValue = "CREATION_DATE_DESC") EmployerCastingApplicantsOrderBy orderBy,
        @Parameter(description = "Page index, starting from 0.") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size.") @RequestParam(defaultValue = "8") int size
    ) {
        var filter = new EmployerCastingApplicantsFilter(
            null,
            slug,
            roleId,
            q,
            applicationStatusIdTokens,
            professionIds,
            orderBy
        );
        return castingApplicationService.getEmployerCastingApplicants(filter, page, size);
    }

    @Operation(
        summary = "List applicants by casting grouped by role (Employer)",
        description = "Returns casting roles with an initial slice of applicants for each role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(AppConstants.EMPLOYER_CASTING_URL + "/{slug}/applicants/grouped")
    public EmployerCastingApplicantsGroupedResponse getApplicantsByCastingGrouped(
        @Parameter(description = "Casting slug.") @PathVariable String slug,
        @Parameter(description = "Free-text search query.") @RequestParam(required = false, name = "q") String q,
        @Parameter(description = "Application status token filters.") @RequestParam(required = false, name = "applicationStatusId") List<String> applicationStatusIdTokens,
        @Parameter(description = "Profession ID filters.") @RequestParam(required = false, name = "professionId") List<UUID> professionIds,
        @Parameter(description = "Sorting strategy.") @RequestParam(required = false, defaultValue = "CREATION_DATE_DESC") EmployerCastingApplicantsOrderBy orderBy,
        @Parameter(description = "Applicants returned per role.") @RequestParam(defaultValue = "4") int perRoleSize
    ) {
        var filter = new EmployerCastingApplicantsFilter(
            null,
            slug,
            null,
            q,
            applicationStatusIdTokens,
            professionIds,
            orderBy
        );
        return castingApplicationService.getEmployerCastingApplicantsGrouped(filter, perRoleSize);
    }

    // Casting Application Statuses
    @Operation(
        summary = "Set application to PRESELECTED",
        description = "Sets application status to PRESELECTED (owner employer only, valid transition required).",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(AppConstants.EMPLOYER_CASTING_APPLICATIONS_URL + "/{applicationId}/preselect")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void preselect(@Parameter(description = "Casting application ID.") @PathVariable UUID applicationId) {
        castingApplicationService.preselectCastingApplication(applicationId);
    }

    @Operation(
        summary = "Set application to SELECTED",
        description = "Sets application status to SELECTED (owner employer only, valid transition required).",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(AppConstants.EMPLOYER_CASTING_APPLICATIONS_URL + "/{applicationId}/select")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void select(@Parameter(description = "Casting application ID.") @PathVariable UUID applicationId) {
        castingApplicationService.selectCastingApplication(applicationId);
    }

    @Operation(
        summary = "Set application to VIEWED",
        description = "Sets application status to VIEWED (owner employer only).",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(AppConstants.EMPLOYER_CASTING_APPLICATIONS_URL + "/{applicationId}/view")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void view(@Parameter(description = "Casting application ID.") @PathVariable UUID applicationId) {
        castingApplicationService.viewCastingApplication(applicationId);
    }

    @Operation(
        summary = "Set application to NOT_PROCEEDING",
        description = "Sets application status to NOT_PROCEEDING (owner employer only, valid transition required).",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(AppConstants.EMPLOYER_CASTING_APPLICATIONS_URL + "/{applicationId}/not-proceeding")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void notProceeding(@Parameter(description = "Casting application ID.") @PathVariable UUID applicationId) {
        castingApplicationService.notProceedingCastingApplication(applicationId);
    }

    @Operation(
        summary = "Reset application status to BLANK",
        description = "Resets application status to BLANK (owner employer only, valid transition required).",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(AppConstants.EMPLOYER_CASTING_APPLICATIONS_URL + "/{applicationId}/blank")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void blank(@Parameter(description = "Casting application ID.") @PathVariable UUID applicationId) {
        castingApplicationService.blankCastingApplication(applicationId);
    }

}
