package com.padimasso.autocasting.application.applications.service.impl;

import com.padimasso.autocasting.application.applications.dto.request.CastingApplicationRequest;
import com.padimasso.autocasting.application.applications.model.CastingApplicationEntity;
import com.padimasso.autocasting.application.applications.model.CastingApplicationRequirementSubmissionEntity;
import com.padimasso.autocasting.application.applications.repository.CastingApplicationRepository;
import com.padimasso.autocasting.application.applications.repository.CastingApplicationRequirementSubmissionRepository;
import com.padimasso.autocasting.application.applications.service.CastingApplicationService;
import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.castings.model.CastingRequirementEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRequirementRepository;
import com.padimasso.autocasting.application.castings.repository.CastingRoleRepository;
import com.padimasso.autocasting.application.sitemetadata.model.CastingApplicationStatusOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.repository.CastingApplicationStatusOptionRepository;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.padimasso.autocasting.config.AppConstants.CASTING_APPLICATION_STATUS_BLANK;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class CastingApplicationServiceImpl implements CastingApplicationService {

    private final AuthContext authContext;
    private final TalentProfileRepository talentProfileRepository;
    private final CastingApplicationRepository castingApplicationRepository;
    private final CastingApplicationRequirementSubmissionRepository castingApplicationRequirementSubmissionRepository;
    private final CastingRoleRepository castingRoleRepository;
    private final CastingRequirementRepository castingRequirementRepository;
    private final CastingApplicationStatusOptionRepository statusOptionRepository;

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

        // Requirements for role
        List<CastingRequirementEntity> requirements =
            castingRequirementRepository.findAllByCastingRole_IdAndDeletedFalse(roleId);

        List<CastingApplicationRequest.RequirementSubmission> submissions =
            (request != null && request.submissions() != null) ? request.submissions() : List.of();

        // Validación “IDs existen y pertenecen al role”
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

        // Create application
        CastingApplicationEntity app = CastingApplicationEntity.builder()
            .castingRole(role)
            .talentProfile(profile)
            .status(blankStatus)
            .message(request != null ? request.message() : null)
            .build();

        app = castingApplicationRepository.save(app);

        // Save submissions (si hay requirements)
        if (!requirements.isEmpty()) {
            Map<UUID, CastingRequirementEntity> reqById = new HashMap<>();
            for (CastingRequirementEntity r : requirements) reqById.put(r.getId(), r);

            for (CastingApplicationRequest.RequirementSubmission s : submissions) {
                if (s == null || s.castingRequirementId() == null) continue;

                CastingRequirementEntity req = reqById.get(s.castingRequirementId());
                if (req == null) {
                    throw new IllegalArgumentException("applications.requirement_invalid");
                }

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
}
