package com.padimasso.autocasting.application.auth.service;

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

@Component
@RequiredArgsConstructor
class UserProvisioningService {

    public static final String PLAN_FREE = "FREE";
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final TalentProfileRepository talentProfileRepository;
    private final PlanRepository planRepository;

    @Transactional
    void ensureUser(String email, String roleCode, String name) {
        if (email == null) throw new OAuth2AuthenticationException("auth.user_not_found");
        if (roleCode == null) throw new OAuth2AuthenticationException("oauth.role_missing");

        final RoleEntity foundRole = roleRepository.findByCode(roleCode.toUpperCase()).orElseThrow(() -> new IllegalArgumentException("oauth.role_missing"));
        final PlanEntity freePlan = planRepository.findByCode(PLAN_FREE).orElseThrow(() -> new IllegalStateException("auth.invalid_plan"));

        UserEntity user = userRepository.findByEmail(email).orElseGet(() -> {
            UserEntity newUser = UserEntity.builder().email(email).password(null).userAccountProvider(UserAccountProvider.OTHER).role(foundRole).build();
            return userRepository.save(newUser);
        });

        TalentProfileEntity profile = talentProfileRepository.findByUserId(user.getId()).orElseGet(() -> {
            TalentProfileEntity p = TalentProfileEntity.builder().user(user).plan(freePlan).build();
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
