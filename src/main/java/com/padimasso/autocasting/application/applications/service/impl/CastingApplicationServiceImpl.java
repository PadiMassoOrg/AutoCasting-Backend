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
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.castings.model.CastingRequirementEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRequirementRepository;
import com.padimasso.autocasting.application.castings.repository.CastingRoleRepository;
import com.padimasso.autocasting.application.shared.web.SliceResponse;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import com.padimasso.autocasting.application.sitemetadata.model.CastingApplicationStatusOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.repository.CastingApplicationStatusOptionRepository;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.padimasso.autocasting.config.AppConstants.CASTING_APPLICATION_STATUS_BLANK;
import static com.padimasso.autocasting.config.AppConstants.MAX_PAGE_SIZE;

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
    private final CastingApplicationStatusOptionRepository statusOptionRepository;
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
        if (s == null) return null;
        var t = s.trim();
        return t.isBlank() ? null : t;
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

        CastingApplicationStatusOptionEntity blankStatus = statusOptionRepository
            .findByStringCode(CASTING_APPLICATION_STATUS_BLANK)
            .orElseThrow(() -> new IllegalStateException("sitemetadata.application_status.not_found"));

        CastingApplicationEntity app = CastingApplicationEntity.builder()
            .castingRole(role)
            .talentProfile(profile)
            .status(blankStatus)
            .message(request != null ? request.message() : null)
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
                    .audioUrl(s.audioUrl())
                    .videoUrl(s.videoUrl())
                    .notes(s.notes())
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

        var f = new TalentCastingApplicationsFilter(
            profile.getId(),
            safeTrim(filter.search()),
            filter.castingStatusIdTokens(),
            filter.projectTypeIdTokens(),
            filter.modalityIdTokens(),
            filter.orderBy()
        );

        var spec = CastingApplicationSpecs.fromTalentFilter(f);
        var result = castingApplicationRepository.findAll(spec, pageable);
        var items = result.getContent().stream()
            .map(castingApplicationMapper::toTalentCardFromEntity)
            .toList();

        return new SliceResponse<>(
            items,
            result.hasNext(),
            result.getNumber(),
            result.getSize()
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

        var f = new EmployerCastingApplicantsFilter(
            employerProfileId,
            castingSlug,
            safeTrim(filter.search()),
            filter.roleIds(),
            filter.applicationStatusIdTokens(),
            filter.professionIds(),
            filter.orderBy()
        );

        var spec = CastingApplicationSpecs.fromEmployerFilter(f);
        var pageResult = castingApplicationRepository.findAll(spec, pageable);
        List<CastingApplicationEntity> apps = pageResult.getContent();
        List<UUID> appIds = apps.stream().map(CastingApplicationEntity::getId).toList();

        // 1) Submissions
        Map<UUID, List<ApplicantRequirementSubmissionRow>> subsByAppId =
            appIds.isEmpty() ? Map.of() : groupSubmissions(appIds);

        // 2) Professions
        Map<UUID, List<SiteMetadataObject>> professionsByAppId;
        if (!appIds.isEmpty()) {
            var profRows = castingApplicationRepository.findProfessionsByApplicationIds(appIds);

            professionsByAppId = profRows.stream()
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
        } else {
            professionsByAppId = Map.of();
        }

        var items = apps.stream()
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
    // Internal grouping
    // ======================
    private Map<UUID, List<ApplicantRequirementSubmissionRow>> groupSubmissions(List<UUID> applicationIds) {
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
