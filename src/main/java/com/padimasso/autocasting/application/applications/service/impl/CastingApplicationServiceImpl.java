package com.padimasso.autocasting.application.applications.service.impl;

import com.padimasso.autocasting.application.applications.dto.EmployerCastingApplicantsFilter;
import com.padimasso.autocasting.application.applications.dto.TalentCastingApplicationsFilter;
import com.padimasso.autocasting.application.applications.dto.request.CastingApplicationRequest;
import com.padimasso.autocasting.application.applications.dto.response.ApplicantRequirementSubmissionRow;
import com.padimasso.autocasting.application.applications.dto.response.EmployerCastingApplicantCardResponse;
import com.padimasso.autocasting.application.applications.dto.response.TalentCastingApplicationCardResponse;
import com.padimasso.autocasting.application.applications.mapper.CastingApplicationMapper;
import com.padimasso.autocasting.application.applications.model.CastingApplicationEntity;
import com.padimasso.autocasting.application.applications.model.CastingApplicationRequirementSubmissionEntity;
import com.padimasso.autocasting.application.applications.repository.CastingApplicationRepository;
import com.padimasso.autocasting.application.applications.repository.CastingApplicationRequirementSubmissionRepository;
import com.padimasso.autocasting.application.applications.repository.order.EmployerCastingApplicantsOrderBy;
import com.padimasso.autocasting.application.applications.repository.order.TalentCastingApplicationsOrderBy;
import com.padimasso.autocasting.application.applications.repository.projection.ApplicationProfessionProjection;
import com.padimasso.autocasting.application.applications.repository.projection.ApplicationRequirementSubmissionProjection;
import com.padimasso.autocasting.application.applications.repository.specification.CastingApplicationSpecs;
import com.padimasso.autocasting.application.applications.service.CastingApplicationService;
import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.auth.context.EmployerContext;
import com.padimasso.autocasting.application.auth.dto.response.EmployerPrincipal;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.castings.model.CastingRequirementEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRequirementRepository;
import com.padimasso.autocasting.application.castings.repository.CastingRoleRepository;
import com.padimasso.autocasting.application.shared.web.SliceResponse;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import com.padimasso.autocasting.application.sitemetadata.model.CastingApplicationStatusOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import com.padimasso.autocasting.application.shared.util.TextNormalizer;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.padimasso.autocasting.config.AppConstants.*;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class CastingApplicationServiceImpl implements CastingApplicationService {

    private final AuthContext authContext;
    private final EmployerContext employerContext;
    private final TalentProfileRepository talentProfileRepository;
    private final CastingApplicationRepository castingApplicationRepository;
    private final CastingApplicationRequirementSubmissionRepository castingApplicationRequirementSubmissionRepository;
    private final CastingRoleRepository castingRoleRepository;
    private final CastingRequirementRepository castingRequirementRepository;
    private final SiteMetadataResolver siteMetadataResolver;
    private final CastingApplicationMapper castingApplicationMapper;

    // ======================
    // Helpers
    // ======================
    private static @NonNull Set<UUID> getSubmittedIds(
        List<CastingApplicationRequest.RequirementSubmission> submissions,
        List<CastingRequirementEntity> requirements
    ) {
        if (submissions == null || submissions.isEmpty()) {
            throw new IllegalArgumentException("applications.requirements_missing");
        }
        Set<UUID> requiredIds = new HashSet<>();
        for (CastingRequirementEntity r : requirements) requiredIds.add(r.getId());
        Set<UUID> submittedIds = new HashSet<>();
        for (CastingApplicationRequest.RequirementSubmission s : submissions) {
            if (s == null || s.castingRequirementId() == null) continue;
            submittedIds.add(s.castingRequirementId());
        }
        if (!submittedIds.containsAll(requiredIds)) {
            throw new IllegalArgumentException("applications.requirements_missing");
        }
        return submittedIds;
    }

    private static String safeTrim(String s) {
        return TextNormalizer.normalizeNullable(s);
    }

    private void setEmployerOwnedApplicationStatus(UUID applicationId, String statusStringCode) {
        if (applicationId == null) throw new IllegalArgumentException("applications.id_required");
        if (statusStringCode == null || statusStringCode.isBlank()) {
            throw new IllegalArgumentException("applications.status_required");
        }

        EmployerPrincipal employer = employerContext.getCurrentEmployerOrThrow();
        UUID employerProfileId = employer.employerProfile().getId();

        CastingApplicationStatusOptionEntity status =
            siteMetadataResolver.resolveCastingApplicationStatusByCodeOrThrow(statusStringCode);

        int updated = castingApplicationRepository.setStatusIfOwned(applicationId, employerProfileId, status);

        if (updated == 0) {
            // 0 = no existe, no pertenece al employer, o está deleted
            throw new IllegalArgumentException("applications.not_found_or_forbidden");
        }
    }

    // ======================
    // Talent
    // ======================
    @Override
    @Transactional
    public void apply(UUID roleId, CastingApplicationRequest request) {
        if (roleId == null) throw new IllegalArgumentException("casting.role.required");
        UserEntity user = authContext.getCurrentUserOrThrow();
        TalentProfileEntity profile = talentProfileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("profile.not_found"));
        CastingRoleEntity role = castingRoleRepository.findByIdAndDeletedFalse(roleId)
            .orElseThrow(() -> new IllegalArgumentException("casting.role.not_found"));
        if (castingApplicationRepository.existsByCastingRoleIdAndTalentProfileId(roleId, profile.getId())) {
            throw new IllegalStateException("applications.already_applied");
        }

        List<CastingRequirementEntity> requirements =
            castingRequirementRepository.findAllByCastingRole_IdAndDeletedFalse(roleId);
        List<CastingApplicationRequest.RequirementSubmission> submissions =
            (request != null && request.submissions() != null) ? request.submissions() : List.of();

        if (!requirements.isEmpty()) {
            Set<UUID> submittedIds = getSubmittedIds(submissions, requirements);

            List<CastingRequirementEntity> submittedReqEntities =
                castingRequirementRepository.findAllByCastingRole_IdAndIdInAndDeletedFalse(roleId, submittedIds);

            if (submittedReqEntities.size() != submittedIds.size()) {
                throw new IllegalArgumentException("applications.requirement_invalid");
            }
        }

        CastingApplicationStatusOptionEntity blankStatus =
            siteMetadataResolver.resolveCastingApplicationStatusByCodeOrThrow(CASTING_APPLICATION_STATUS_BLANK);

        CastingApplicationEntity app = CastingApplicationEntity.builder()
            .castingRole(role)
            .talentProfile(profile)
            .status(blankStatus)
            .message(request != null ? TextNormalizer.normalizeNullable(request.message()) : null)
            .build();

        app = castingApplicationRepository.save(app);

        if (!requirements.isEmpty()) {
            Map<UUID, CastingRequirementEntity> reqById = new HashMap<>();
            for (CastingRequirementEntity r : requirements) reqById.put(r.getId(), r);

            for (CastingApplicationRequest.RequirementSubmission s : submissions) {
                if (s == null || s.castingRequirementId() == null) continue;

                CastingRequirementEntity req = reqById.get(s.castingRequirementId());
                if (req == null) throw new IllegalArgumentException("applications.requirement_invalid");

                CastingApplicationRequirementSubmissionEntity sub = CastingApplicationRequirementSubmissionEntity.builder()
                    .application(app)
                    .castingRequirement(req)
                    .audioUrl(TextNormalizer.normalizeNullable(s.audioUrl()))
                    .videoUrl(TextNormalizer.normalizeNullable(s.videoUrl()))
                    .notes(TextNormalizer.normalizeNullable(s.notes()))
                    .build();

                castingApplicationRequirementSubmissionRepository.save(sub);
            }
        }
    }

    @Override
    @Transactional
    public SliceResponse<TalentCastingApplicationCardResponse> getTalentCastingApplications(
        TalentCastingApplicationsFilter filter,
        int page,
        int size
    ) {
        UserEntity user = authContext.getCurrentUserOrThrow();
        TalentProfileEntity profile = talentProfileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("profile.not_found"));

        int ps = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);

        var orderBy = filter.orderBy() != null
            ? filter.orderBy()
            : TalentCastingApplicationsOrderBy.CREATION_DATE_DESC;

        var pageable = PageRequest.of(page, ps, orderBy.toSort());

        var normalizedFilter = new TalentCastingApplicationsFilter(
            profile.getId(),
            safeTrim(filter.search()),
            filter.castingStatusIdTokens(),
            filter.projectTypeIdTokens(),
            filter.modalityIdTokens(),
            filter.orderBy()
        );

        var spec = CastingApplicationSpecs.fromTalentFilter(normalizedFilter);

        // 1) Página liviana, sin entity graph con colecciones
        var pageResult = castingApplicationRepository.findAll(spec, pageable);

        var ids = pageResult.getContent().stream()
            .map(CastingApplicationEntity::getId)
            .toList();

        if (ids.isEmpty()) {
            return new SliceResponse<>(
                List.of(),
                pageResult.hasNext(),
                pageResult.getNumber(),
                pageResult.getSize()
            );
        }

        // 2) Rehidratación sólo de los elementos de la página
        var hydratedEntities = castingApplicationRepository.findAllForTalentCardsByIdIn(ids);

        var byId = hydratedEntities.stream()
            .collect(java.util.stream.Collectors.toMap(
                CastingApplicationEntity::getId,
                java.util.function.Function.identity()
            ));

        // 3) Preservar el orden original de la página
        var items = ids.stream()
            .map(byId::get)
            .filter(java.util.Objects::nonNull)
            .map(castingApplicationMapper::toTalentCardFromEntity)
            .toList();

        return new SliceResponse<>(
            items,
            pageResult.hasNext(),
            pageResult.getNumber(),
            pageResult.getSize()
        );
    }

    // ======================
    // Employer
    // ======================
    @Override
    @Transactional
    public SliceResponse<EmployerCastingApplicantCardResponse> getEmployerCastingApplicants(
        EmployerCastingApplicantsFilter filter,
        int page,
        int size
    ) {
        var employer = employerContext.getCurrentEmployerOrThrow();
        UUID employerProfileId = employer.employerProfile().getId();

        String castingSlug = safeTrim(filter.castingSlug());
        if (castingSlug == null) throw new IllegalArgumentException("casting.slug_required");

        int ps = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);

        var orderBy = filter.orderBy() != null
            ? filter.orderBy()
            : EmployerCastingApplicantsOrderBy.CREATION_DATE_DESC;

        var pageable = PageRequest.of(page, ps, orderBy.toSort());

        var effectiveFilter = new EmployerCastingApplicantsFilter(
            employerProfileId,
            castingSlug,
            safeTrim(filter.search()),
            filter.applicationStatusIdTokens(),
            filter.professionIds(),
            filter.orderBy()
        );

        var spec = CastingApplicationSpecs.fromEmployerFilter(effectiveFilter);

        // 1) Página liviana
        var pageResult = castingApplicationRepository.findAll(spec, pageable);
        List<CastingApplicationEntity> pageApps = pageResult.getContent();

        List<UUID> appIds = pageApps.stream()
            .map(CastingApplicationEntity::getId)
            .toList();

        if (appIds.isEmpty()) {
            return new SliceResponse<>(
                List.of(),
                pageResult.hasNext(),
                pageResult.getNumber(),
                pageResult.getSize()
            );
        }

        // 2) Rehidratación solo de la página visible
        var hydratedApps = castingApplicationRepository.findAllForEmployerApplicantCardsByIdIn(appIds);

        var byId = hydratedApps.stream()
            .collect(java.util.stream.Collectors.toMap(
                CastingApplicationEntity::getId,
                java.util.function.Function.identity()
            ));

        var orderedApps = appIds.stream()
            .map(byId::get)
            .filter(java.util.Objects::nonNull)
            .toList();

        // 3) Submissions
        Map<UUID, List<ApplicantRequirementSubmissionRow>> subsByAppId =
            groupSubmissions(appIds);

        // 4) Professions
        var profRows = castingApplicationRepository.findProfessionsByApplicationIds(appIds);

        Map<UUID, List<SiteMetadataObject>> professionsByAppId = profRows.stream()
            .filter(r -> r.getProfessionId() != null)
            .collect(Collectors.groupingBy(
                ApplicationProfessionProjection::getApplicationId,
                Collectors.mapping(
                    r -> new SiteMetadataObject(
                        r.getProfessionId(),
                        r.getProfessionCode(),
                        r.getProfessionCategoryStringCode()
                    ),
                    Collectors.toList()
                )
            ));

        var items = orderedApps.stream()
            .map(a -> castingApplicationMapper.toEmployerApplicantCardFromEntity(
                a,
                professionsByAppId.getOrDefault(a.getId(), List.of()),
                subsByAppId.getOrDefault(a.getId(), List.of())
            ))
            .toList();

        return new SliceResponse<>(
            items,
            pageResult.hasNext(),
            pageResult.getNumber(),
            pageResult.getSize()
        );
    }

    // ======================
    // Employer - Application Status actions
    // ======================
    @Override
    @Transactional
    public void preselectCastingApplication(UUID applicationId) {
        setEmployerOwnedApplicationStatus(applicationId, CASTING_APPLICATION_STATUS_PRESELECTED);
    }

    @Override
    @Transactional
    public void selectCastingApplication(UUID applicationId) {
        setEmployerOwnedApplicationStatus(applicationId, CASTING_APPLICATION_STATUS_SELECTED);
    }

    @Override
    @Transactional
    public void viewCastingApplication(UUID applicationId) {
        setEmployerOwnedApplicationStatus(applicationId, CASTING_APPLICATION_STATUS_VIEWED);
    }

    @Override
    @Transactional
    public void notProceedingCastingApplication(UUID applicationId) {
        setEmployerOwnedApplicationStatus(applicationId, CASTING_APPLICATION_STATUS_NOT_PROCEEDING);
    }

    @Override
    @Transactional
    public void blankCastingApplication(UUID applicationId) {
        setEmployerOwnedApplicationStatus(applicationId, CASTING_APPLICATION_STATUS_BLANK);
    }

    // ======================
    // Internal grouping
    // ======================
    private Map<UUID, List<ApplicantRequirementSubmissionRow>> groupSubmissions(List<UUID> applicationIds) {
        if (applicationIds == null || applicationIds.isEmpty()) {
            return Map.of();
        }

        List<ApplicationRequirementSubmissionProjection> subs =
            castingApplicationRequirementSubmissionRepository.findAllByApplicationIds(applicationIds);

        return subs.stream()
            .collect(Collectors.groupingBy(
                ApplicationRequirementSubmissionProjection::getApplicationId,
                Collectors.mapping(
                    s -> new ApplicantRequirementSubmissionRow(
                        s.getCastingRequirementId(),
                        s.getRequiresAudio(),
                        s.getRequiresVideo(),
                        s.getAudioUrl(),
                        s.getVideoUrl(),
                        s.getNotes()
                    ),
                    Collectors.toList()
                )
            ));
    }
}
