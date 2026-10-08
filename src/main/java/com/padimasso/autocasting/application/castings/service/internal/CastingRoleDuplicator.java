package com.padimasso.autocasting.application.castings.service.internal;

import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.shared.util.TextNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/** Builds the copy of a role that employers and admins both get when they duplicate one. */
@Component
@RequiredArgsConstructor
public class CastingRoleDuplicator {

    private final CastingDataApplier castingDataApplier;

    public CastingRoleEntity duplicate(CastingRoleEntity sourceRole, String roleName) {
        String duplicatedRoleName = TextNormalizer.normalizeNullable(roleName);

        CastingRoleEntity duplicatedRole = CastingRoleEntity.builder()
            .casting(sourceRole.getCasting())
            .roleName(duplicatedRoleName != null ? duplicatedRoleName : sourceRole.getRoleName())
            .roleType(sourceRole.getRoleType())
            .gender(sourceRole.getGender())
            .ageMin(sourceRole.getAgeMin())
            .ageMax(sourceRole.getAgeMax())
            .description(sourceRole.getDescription())
            .payRateType(sourceRole.getPayRateType())
            .currency(sourceRole.getCurrency())
            .amount(sourceRole.getAmount())
            .remunerationNotes(sourceRole.getRemunerationNotes())
            .requiresAudio(sourceRole.isRequiresAudio())
            .requiresVideo(sourceRole.isRequiresVideo())
            .requirementDescription(sourceRole.getRequirementDescription())
            .ethnicity(sourceRole.getEthnicity())
            .tattoo(sourceRole.getTattoo())
            .passport(sourceRole.getPassport())
            .drivingLicense(sourceRole.getDrivingLicense())
            // Deliberately NOT copied: two roles must never share the same Supabase object.
            // referencePhotoUrl points at a single file, and deleting/replacing it from either
            // role would delete it out from under the other. The duplicated role starts with
            // no photo — the employer re-uploads (even the same image) to get its own URL.
            .referencePhotoUrl(null)
            .professions(new HashSet<>(sourceRole.getProfessions() == null ? Set.of() : sourceRole.getProfessions()))
            .skills(new HashSet<>(sourceRole.getSkills() == null ? Set.of() : sourceRole.getSkills()))
            .build();

        castingDataApplier.validateRole(duplicatedRole);
        return duplicatedRole;
    }
}
