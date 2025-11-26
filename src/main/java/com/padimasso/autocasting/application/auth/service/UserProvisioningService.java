package com.padimasso.autocasting.application.auth.service;

import com.padimasso.autocasting.application.auth.model.OnboardingStatus;
import com.padimasso.autocasting.application.auth.model.RoleEntity;
import com.padimasso.autocasting.application.auth.model.UserAccountProvider;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.repository.RoleRepository;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.plan.model.PlanEntity;
import com.padimasso.autocasting.application.plan.repository.PlanRepository;
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

@Component
@RequiredArgsConstructor
class UserProvisioningService {

    public static final String PLAN_FREE = "FREE";
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final TalentProfileRepository talentProfileRepository;
    private final PlanRepository planRepository;

    @Transactional
    void ensureUser(String email, String name) {
        if (email == null) throw new OAuth2AuthenticationException("auth.user_not_found");

        Set<RoleEntity> baseRoles = new HashSet<>(
            roleRepository.findAllByCodeIn(List.of(TALENT.name(), EMPLOYER.name()))
                .orElseThrow(() -> new IllegalArgumentException("auth.invalid_role"))
        );

        final PlanEntity freePlan = planRepository.findByCode(PLAN_FREE).orElseThrow(() -> new IllegalStateException("auth.invalid_plan"));

        UserEntity user = userRepository.findByEmail(email).orElseGet(() -> UserEntity.builder()
            .email(email)
            .password(null)
            .userAccountProvider(UserAccountProvider.OTHER)
            .activeMode(null)
            .talentOnboardingStatus(OnboardingStatus.NOT_STARTED)
            .employerOnboardingStatus(OnboardingStatus.NOT_STARTED)
            .build());

        normalizeUser(user, baseRoles);
        user = userRepository.save(user);

        UserEntity finalUser = user;

        TalentProfileEntity profile = talentProfileRepository.findByUserId(user.getId()).orElseGet(() -> {
            TalentProfileEntity p = TalentProfileEntity.builder().user(finalUser).plan(freePlan).build();
            return talentProfileRepository.save(p);
        });

        if (profile.getBasicInfo() == null) {
            BasicInfoEntity basicInfo = BasicInfoEntity.builder()
                .stageName(name)
                .talentProfile(profile)
                .build();
            profile.setBasicInfo(basicInfo);
        } else {
            if ((profile.getBasicInfo().getStageName() == null || profile.getBasicInfo().getStageName().isBlank())
                && name != null && !name.isBlank()) {
                profile.getBasicInfo().setStageName(name);
            }
        }

        if (profile.getContact() == null) {
            ContactEntity contact = ContactEntity.builder()
                .email(email)
                .talentProfile(profile)
                .build();
            profile.setContact(contact);
        } else {
            if (profile.getContact().getEmail() == null || profile.getContact().getEmail().isBlank()) {
                profile.getContact().setEmail(email);
            }
        }

        if (profile.getSocialMedia() == null) {
            SocialMediaEntity socialMedia = SocialMediaEntity.builder()
                .talentProfile(profile)
                .build();
            profile.setSocialMedia(socialMedia);
        }

        if (profile.getMedia() == null) {
            MediaEntity media = MediaEntity.builder()
                .talentProfile(profile)
                .build();
            profile.setMedia(media);
        }

        if (profile.getCharacteristics() == null) {
            CharacteristicsEntity characteristics = CharacteristicsEntity.builder()
                .talentProfile(profile)
                .build();
            profile.setCharacteristics(characteristics);
        }

        talentProfileRepository.save(profile);
    }
}
