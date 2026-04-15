package com.padimasso.autocasting.application.auth.service;

import com.padimasso.autocasting.application.auth.model.OnboardingStatus;
import com.padimasso.autocasting.application.auth.model.RoleEntity;
import com.padimasso.autocasting.application.auth.model.UserAccountProvider;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.repository.RoleRepository;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.employer.model.EmployerBasicInfoEntity;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.employer.repository.EmployerProfileRepository;
import com.padimasso.autocasting.application.plan.model.PlanEntity;
import com.padimasso.autocasting.application.plan.repository.PlanRepository;
import com.padimasso.autocasting.application.sitemetadata.model.GenderOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import com.padimasso.autocasting.application.talent.model.*;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.padimasso.autocasting.application.auth.model.UserMode.EMPLOYER;
import static com.padimasso.autocasting.application.auth.model.UserMode.TALENT;
import static com.padimasso.autocasting.application.auth.service.impl.AuthServiceImpl.normalizeUser;
import static com.padimasso.autocasting.config.AppConstants.GENDER_OPTION_INDISTINCT;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.*;

@Component
@RequiredArgsConstructor
class UserProvisioningService {

    public static final String PLAN_FREE = "FREE";

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final TalentProfileRepository talentProfileRepository;
    private final EmployerProfileRepository employerProfileRepository;
    private final PlanRepository planRepository;
    private final SiteMetadataResolver siteMetadataResolver;

    @Transactional
    void ensureUser(String email, String name) {
        if (email == null) {
            throw new OAuth2AuthenticationException(AUTH_USER_NOT_FOUND);
        }

        // Roles base: TALENT + EMPLOYER
        Set<RoleEntity> baseRoles = new HashSet<>(
            roleRepository.findAllByCodeIn(List.of(TALENT.name(), EMPLOYER.name()))
                .orElseThrow(() -> new IllegalArgumentException(AUTH_INVALID_ROLE))
        );

        final PlanEntity freePlan = planRepository.findByCode(PLAN_FREE)
            .orElseThrow(() -> new IllegalStateException(AUTH_INVALID_PLAN));

        // User (nuevo o existente)
        UserEntity user = userRepository.findByEmail(email).orElseGet(() -> UserEntity.builder()
            .email(email)
            .password(null)
            .userAccountProvider(UserAccountProvider.OTHER)
            .activeMode(null)
            .talentOnboardingStatus(OnboardingStatus.NOT_STARTED)
            .employerOnboardingStatus(OnboardingStatus.NOT_STARTED)
            .build()
        );

        normalizeUser(user, baseRoles);
        user = userRepository.save(user);

        UserEntity finalUser = user;

        // Profiles
        TalentProfileEntity talentProfile = talentProfileRepository.findByUserId(user.getId())
            .orElseGet(() -> {
                TalentProfileEntity p = TalentProfileEntity.builder()
                    .user(finalUser)
                    .plan(freePlan)
                    .build();
                return talentProfileRepository.save(p);
            });

        EmployerProfileEntity employerProfile = employerProfileRepository.findByUserId(user.getId())
            .orElseGet(() -> {
                EmployerProfileEntity p = EmployerProfileEntity.builder()
                    .user(finalUser)
                    .plan(freePlan)
                    .build();
                return employerProfileRepository.save(p);
            });


        // Basic Info
        GenderOptionEntity indistinctGender =
            siteMetadataResolver.resolveGenderByCodeOrThrow(GENDER_OPTION_INDISTINCT);

        if (talentProfile.getBasicInfo() == null) {
            BasicInfoEntity basicInfo = BasicInfoEntity.builder()
                .stageName(name) // podemos usar el nombre de OAuth como nombre artístico inicial
                .talentProfile(talentProfile)
                .gender(indistinctGender)
                .build();
            talentProfile.setBasicInfo(basicInfo);
        } else {
            if ((talentProfile.getBasicInfo().getStageName() == null || talentProfile.getBasicInfo().getStageName().isBlank())
                && name != null && !name.isBlank()) {
                talentProfile.getBasicInfo().setStageName(name);
            }
        }

        if (employerProfile.getBasicInfo() == null) {
            EmployerBasicInfoEntity employerBasicInfo = EmployerBasicInfoEntity.builder()
                .employerProfile(employerProfile)
                .build();
            employerProfile.setBasicInfo(employerBasicInfo);
        }

        // Contact
        if (talentProfile.getContact() == null) {
            ContactEntity contact = ContactEntity.builder()
                .email(email)
                .talentProfile(talentProfile)
                .build();
            talentProfile.setContact(contact);
        } else {
            if (talentProfile.getContact().getEmail() == null || talentProfile.getContact().getEmail().isBlank()) {
                talentProfile.getContact().setEmail(email);
            }
        }

        // Media
        if (talentProfile.getMedia() == null) {
            MediaEntity media = MediaEntity.builder()
                .talentProfile(talentProfile)
                .build();
            talentProfile.setMedia(media);
        }

        // Characteristics
        if (talentProfile.getCharacteristics() == null) {
            CharacteristicsEntity characteristics = CharacteristicsEntity.builder()
                .talentProfile(talentProfile)
                .build();
            talentProfile.setCharacteristics(characteristics);
        }

        talentProfileRepository.save(talentProfile);
        employerProfileRepository.save(employerProfile);
    }
}
