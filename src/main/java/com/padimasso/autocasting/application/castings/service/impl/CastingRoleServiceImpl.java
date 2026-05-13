package com.padimasso.autocasting.application.castings.service.impl;

import com.padimasso.autocasting.application.castings.dto.EmployerCastingRoleFilter;
import com.padimasso.autocasting.application.castings.dto.request.CastingRoleRequest;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleResponse;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingRoleEmployerCardResponse;
import com.padimasso.autocasting.application.castings.mapper.CastingMapper;
import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRepository;
import com.padimasso.autocasting.application.castings.repository.CastingRoleRepository;
import com.padimasso.autocasting.application.castings.repository.specification.CastingRoleSpecs;
import com.padimasso.autocasting.application.castings.service.CastingRoleService;
import com.padimasso.autocasting.application.common.dto.LastModifiedResponse;
import com.padimasso.autocasting.application.shared.util.TextNormalizer;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.CURRENCY_ARS;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_DRAFT;
import static com.padimasso.autocasting.config.AppConstants.MAX_PAGE_SIZE;
import static com.padimasso.autocasting.config.AppConstants.PAY_RATE_TYPE_UNPAID;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.*;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class CastingRoleServiceImpl implements CastingRoleService {

    private final CastingRoleRepository castingRoleRepository;
    private final CastingRepository castingRepository;
    private final SiteMetadataResolver siteMetadataResolver;
    private final CastingMapper castingMapper;

    @Override
    @Transactional
    public CastingRoleResponse createCastingRole(CastingRoleRequest request) {
        CastingEntity casting = castingRepository.findByIdAndDeletedFalse(request.castingId())
            .orElseThrow(() -> new IllegalArgumentException(CASTINGS_NOT_FOUND));
        assertDraftEditable(casting);

        CastingRoleEntity role = CastingRoleEntity.builder()
            .casting(casting)
            .build();

        applyRoleData(role, request);
        return castingMapper.toRoleResponse(castingRoleRepository.save(role));
    }

    @Override
    @Transactional
    public List<CastingRoleEmployerCardResponse> getCastingRolesByCastingId(EmployerCastingRoleFilter filter, int page, int size) {
        var pageable = PageRequest.of(
            page,
            Math.min(Math.max(size, 1), MAX_PAGE_SIZE),
            Sort.by(Sort.Direction.DESC, "createdAt", "id")
        );

        return castingRoleRepository.findAll(CastingRoleSpecs.fromEmployerFilter(filter), pageable)
            .getContent()
            .stream()
            .map(castingMapper::toEmployerRoleCardResponse)
            .toList();
    }

    @Override
    @Transactional
    public CastingRoleResponse updateCastingRole(UUID roleId, CastingRoleRequest request) {
        CastingRoleEntity role = castingRoleRepository.findByIdAndDeletedFalse(roleId)
            .orElseThrow(() -> new IllegalArgumentException(CASTING_ROLE_NOT_FOUND));

        if (role.getCasting() == null || !role.getCasting().getId().equals(request.castingId())) {
            throw new IllegalArgumentException(CASTINGS_ROLE_MISMATCH);
        }
        assertDraftEditable(role.getCasting());

        applyRoleData(role, request);
        return castingMapper.toRoleResponse(castingRoleRepository.save(role));
    }

    @Override
    @Transactional
    public LastModifiedResponse deleteCastingRole(UUID roleId) {
        CastingRoleEntity role = castingRoleRepository.findByIdAndDeletedFalse(roleId)
            .orElseThrow(() -> new IllegalArgumentException(CASTING_ROLE_NOT_FOUND));
        assertDraftEditable(role.getCasting());

        UUID castingId = role.getCasting() != null ? role.getCasting().getId() : null;
        castingRoleRepository.softDelete(role);

        return new LastModifiedResponse(
            castingId == null
                ? null
                : castingRepository.findByIdAndDeletedFalse(castingId).map(CastingEntity::getModifiedAt).orElse(null)
        );
    }

    @Override
    public CastingRoleResponse getById(UUID roleId) {
        return castingRoleRepository.findByIdAndDeletedFalse(roleId)
            .map(castingMapper::toRoleResponse)
            .orElseThrow(() -> new IllegalArgumentException(CASTING_ROLE_NOT_FOUND));
    }

    private void applyRoleData(CastingRoleEntity role, CastingRoleRequest request) {
        role.setRoleName(TextNormalizer.normalizeNullable(request.roleName()));
        role.setRoleType(siteMetadataResolver.resolveRoleTypeOrThrow(request.roleTypeId()));
        role.setGender(siteMetadataResolver.resolveGenderOrThrow(request.genderId()));
        role.setAgeMin(request.ageMin());
        role.setAgeMax(request.ageMax());
        role.setDescription(TextNormalizer.normalizeNullable(request.description()));

        Set<UUID> professionIds = request.professionIds() == null ? Set.of() : request.professionIds();
        role.setProfessions(new HashSet<>(siteMetadataResolver.resolveProfessionsOrThrow(professionIds)));

        Set<UUID> skillIds = request.skillIds() == null ? Set.of() : request.skillIds();
        role.setSkills(new HashSet<>(siteMetadataResolver.resolveSkillsOrThrow(skillIds)));

        role.setPayRateType(siteMetadataResolver.resolvePayRateTypeOrThrow(request.payRateTypeId()));
        role.setCurrency(request.currencyId() != null ? siteMetadataResolver.resolveCurrencyOrThrow(request.currencyId()) : null);
        role.setAmount(request.amount());
        role.setRemunerationNotes(TextNormalizer.normalizeNullable(request.remunerationNotes()));

        role.setRequiresAudio(Boolean.TRUE.equals(request.requiresAudio()));
        role.setRequiresVideo(Boolean.TRUE.equals(request.requiresVideo()));
        role.setRequirementDescription(TextNormalizer.normalizeNullable(request.requirementDescription()));

        role.setEthnicity(request.ethnicityId() != null ? siteMetadataResolver.resolveEthnicityOrThrow(request.ethnicityId()) : null);
        role.setTattoo(request.tattoo());
        role.setPassport(request.passport());
        role.setDrivingLicense(request.drivingLicense());

        validateRole(role);
    }

    private void validateRole(CastingRoleEntity role) {
        if (role.getAgeMin() != null && role.getAgeMax() != null && role.getAgeMin() > role.getAgeMax()) {
            throw new IllegalArgumentException(CASTING_AGE_RANGE_INVALID);
        }

        if (role.getAmount() != null && role.getAmount().signum() < 0) {
            throw new IllegalArgumentException(CASTING_AMOUNT_NEGATIVE);
        }

        String payRateCode = role.getPayRateType() != null ? role.getPayRateType().getStringCode() : null;
        boolean isUnpaidLike = PAY_RATE_TYPE_UNPAID.equals(payRateCode)
            || endsWith(payRateCode, ".collaborative")
            || endsWith(payRateCode, ".cooperative");

        if (isUnpaidLike) {
            role.setAmount(null);
            if ((endsWith(payRateCode, ".collaborative") || endsWith(payRateCode, ".cooperative")) && role.getCurrency() == null) {
                role.setCurrency(siteMetadataResolver.resolveCurrencyByCodeOrThrow(CURRENCY_ARS));
            }
            return;
        }

        if (role.getAmount() == null) {
            throw new IllegalArgumentException(CASTING_AMOUNT_REQUIRED);
        }

        if (role.getCurrency() == null) {
            role.setCurrency(siteMetadataResolver.resolveCurrencyByCodeOrThrow(CURRENCY_ARS));
        }
    }

    private boolean endsWith(String value, String suffix) {
        return value != null && value.endsWith(suffix);
    }

    private void assertDraftEditable(CastingEntity casting) {
        String statusCode = casting != null && casting.getStatus() != null ? casting.getStatus().getStringCode() : null;
        if (!CASTING_STATUS_DRAFT.equals(statusCode)) {
            throw new IllegalStateException(CASTINGS_ONLY_DRAFT_EDITABLE);
        }
    }
}
