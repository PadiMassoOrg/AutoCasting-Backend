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
        @RequestBody(required = false) CastingApplicationRequest request
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
        @RequestParam(defaultValue = "12") int size
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
        @RequestParam(required = false, name = "roleId") List<UUID> roleIds,
        @RequestParam(required = false, name = "applicationStatusId") List<String> applicationStatusIdTokens,
        @RequestParam(required = false, name = "professionId") List<UUID> professionIds,
        @RequestParam(required = false, defaultValue = "CREATION_DATE_DESC") EmployerCastingApplicantsOrderBy orderBy,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "12") int size
    ) {
        var filter = new EmployerCastingApplicantsFilter(
            null,
            slug,
            q,
            roleIds,
            applicationStatusIdTokens,
            professionIds,
            orderBy
        );
        return castingApplicationService.getEmployerCastingApplicants(filter, page, size);
    }

}
