package com.padimasso.autocasting.application.admin.mapper;

import com.padimasso.autocasting.application.admin.dto.response.AdminCastingDetailsResponse;
import com.padimasso.autocasting.application.admin.dto.response.AdminCastingRoleRowResponse;
import com.padimasso.autocasting.application.admin.dto.response.AdminCastingRowResponse;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleResponse;
import com.padimasso.autocasting.application.castings.mapper.CastingMapper;
import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.talent.mapper.TalentProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminCastingMapper {

    private final CastingMapper castingMapper;

    public AdminCastingRowResponse toRowResponse(CastingEntity casting) {
        var employerProfile = casting.getEmployerProfile();
        var employerBasicInfo = employerProfile != null ? employerProfile.getBasicInfo() : null;

        return new AdminCastingRowResponse(
            casting.getId(),
            casting.getDefaultCode(),
            employerBasicInfo != null ? employerBasicInfo.getCompanyName() : null,
            casting.getTitle(),
            TalentProfileMapper.mapToSiteMetadataObject(casting.getStatus()),
            casting.getApplicationDeadline(),
            casting.getCreatedAt(),
            casting.getCreatedBy(),
            casting.getModifiedAt(),
            casting.getModifiedBy(),
            employerProfile != null && employerProfile.getUser() != null && employerProfile.getUser().isSuspended(),
            casting.isDeleted()
        );
    }

    public AdminCastingDetailsResponse toDetailsResponse(CastingEntity casting) {
        var employerProfile = casting.getEmployerProfile();
        var employerBasicInfo = employerProfile != null ? employerProfile.getBasicInfo() : null;

        var roles = casting.getRoles() == null
            ? List.<AdminCastingRoleRowResponse>of()
            : casting.getRoles().stream()
                .filter(role -> role != null && !role.isDeleted())
                .sorted(Comparator
                    .comparing((CastingRoleEntity role) -> role.getRoleName() == null ? "" : role.getRoleName().toLowerCase())
                    .thenComparing(CastingRoleEntity::getId))
                .map(this::toRoleRowResponse)
                .toList();

        return new AdminCastingDetailsResponse(
            casting.getId(),
            casting.getDefaultCode(),
            TalentProfileMapper.mapToSiteMetadataObject(casting.getStatus()),
            employerBasicInfo != null ? employerBasicInfo.getCompanyName() : null,
            casting.getTitle(),
            TalentProfileMapper.mapToSiteMetadataObject(casting.getProjectType()),
            TalentProfileMapper.mapToSiteMetadataObject(casting.getCastingModality()),
            casting.getLocationText(),
            casting.getApplicationDeadline(),
            casting.getHasWardrobeFitting(),
            casting.getWardrobeFittingText(),
            casting.getShootingStartDate(),
            casting.getShootingEndDate(),
            casting.getDescription(),
            roles,
            casting.getCreatedAt(),
            casting.getCreatedBy(),
            casting.getModifiedAt(),
            casting.getModifiedBy(),
            casting.isDeleted()
        );
    }

    public CastingRoleResponse toRoleResponse(CastingRoleEntity role) {
        return castingMapper.toRoleResponse(role);
    }

    private AdminCastingRoleRowResponse toRoleRowResponse(CastingRoleEntity role) {
        return new AdminCastingRoleRowResponse(
            role.getId(),
            role.getRoleName()
        );
    }
}
