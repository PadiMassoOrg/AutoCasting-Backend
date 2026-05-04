package com.padimasso.autocasting.application.auth.service.impl;

import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.auth.dto.request.*;
import com.padimasso.autocasting.application.auth.dto.response.AuthResponse;
import com.padimasso.autocasting.application.auth.dto.response.ForgotPasswordResponse;
import com.padimasso.autocasting.application.auth.dto.response.MeResponse;
import com.padimasso.autocasting.application.auth.model.OnboardingStatus;
import com.padimasso.autocasting.application.auth.model.RoleEntity;
import com.padimasso.autocasting.application.auth.model.UserAccountProvider;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.repository.RoleRepository;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.auth.service.AuthService;
import com.padimasso.autocasting.application.auth.service.EmailService;
import com.padimasso.autocasting.application.auth.service.JwtService;
import com.padimasso.autocasting.application.employer.model.EmployerBasicInfoEntity;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.employer.repository.EmployerBasicInfoRepository;
import com.padimasso.autocasting.application.employer.repository.EmployerProfileRepository;
import com.padimasso.autocasting.application.plan.model.PlanEntity;
import com.padimasso.autocasting.application.plan.repository.PlanRepository;
import com.padimasso.autocasting.application.sitemetadata.model.GenderOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import com.padimasso.autocasting.application.talent.model.*;
import com.padimasso.autocasting.application.talent.repository.*;
import com.padimasso.autocasting.config.AppConstants;
import com.padimasso.autocasting.config.AppProperties;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.padimasso.autocasting.application.auth.model.UserMode.EMPLOYER;
import static com.padimasso.autocasting.application.auth.model.UserMode.TALENT;
import static com.padimasso.autocasting.config.AppConstants.GENDER_OPTION_INDISTINCT;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.*;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class AuthServiceImpl implements AuthService {

    private final SpringTemplateEngine templateEngine;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PlanRepository planRepository;
    private final TalentProfileRepository talentProfileRepository;
    private final EmployerProfileRepository employerProfileRepository;
    private final BasicInfoRepository basicInfoRepository;
    private final EmployerBasicInfoRepository employerBasicInfoRepository;
    private final ContactRepository contactRepository;
    private final MediaRepository mediaRepository;
    private final CharacteristicsRepository characteristicsRepository;
    private final SiteMetadataResolver siteMetadataResolver;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final AppProperties appProperties;
    private final MessageSource messageSource;
    private final AuthContext authContext;

    /**
     * Normaliza un usuario "legacy" o recién creado:
     * - Assert roles: TALENT & EMPLOYER.
     * - Assert onboarding status not null (NOT_STARTED).
     */
    public static void normalizeUser(UserEntity user, Set<RoleEntity> baseRoles) {
        if (user.getUserAccountProvider() == null) {
            user.setUserAccountProvider(UserAccountProvider.OTHER);
        }

        Set<RoleEntity> currentRoles = user.getRoles();
        boolean hasAdminRole = currentRoles != null
            && currentRoles.stream().anyMatch(role -> "ADMIN".equals(role.getCode()));

        // Usuarios administrativos no deben recibir automáticamente roles de producto.
        if (hasAdminRole) {
            return;
        }

        if (currentRoles == null || currentRoles.isEmpty()) {
            user.setRoles(new HashSet<>(baseRoles));
        } else {
            Set<RoleEntity> merged = new HashSet<>(currentRoles);
            merged.addAll(baseRoles);
            user.setRoles(merged);
        }
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new IllegalArgumentException(AUTH_INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException(AUTH_INVALID_CREDENTIALS);
        }

        if (user.isBlocked()) {
            throw new IllegalArgumentException(AUTH_ACCOUNT_BLOCKED);
        }

        Set<RoleEntity> roles = new HashSet<>(roleRepository.findAllByCodeIn(List.of(TALENT.name(), EMPLOYER.name()))
            .orElseThrow(() -> new IllegalArgumentException(AUTH_INVALID_ROLE)));

        normalizeUser(user, roles);
        userRepository.save(user);

        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse adminLogin(LoginRequest request) {
        var user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new IllegalArgumentException(AUTH_INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException(AUTH_INVALID_CREDENTIALS);
        }

        if (user.isBlocked()) {
            throw new IllegalArgumentException(AUTH_ACCOUNT_BLOCKED);
        }

        boolean isAdmin = user.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getCode()));
        if (!isAdmin) {
            throw new IllegalArgumentException(AUTH_ACCESS_DENIED);
        }

        return buildAuthResponse(user);
    }

    @Override
    public ForgotPasswordResponse sendResetPasswordEmail(ForgotPasswordRequest request) {
        var user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new IllegalArgumentException(AUTH_USER_NOT_FOUND));

        if (user.getUserAccountProvider() != UserAccountProvider.LOCAL) {
            throw new IllegalArgumentException(AUTH_PASSWORD_RESET_EXTERNAL);
        }

        var profileOpt = talentProfileRepository.findByUserId(user.getId());
        String talentProfileSlug = profileOpt.map(TalentProfileEntity::getPublicSlug).orElse(null);

        var employerProfileOpt = employerProfileRepository.findByUserId(user.getId());
        String employerProfileSlug = employerProfileOpt.map(EmployerProfileEntity::getPublicSlug).orElse(null);

        String token = jwtService.generateTokenWithCustomExpirationTime(
            user,
            AppConstants.RESET_PASSWORD_EXPIRATION_TIME,
            talentProfileSlug,
            employerProfileSlug
        );

        String resetUrl = appProperties.getFrontendUrl() + "/reset-password?token=" + token;
        var locale = LocaleContextHolder.getLocale();

        Context ctx = new Context(locale);

        String assetsBase = appProperties.getBackendUrl() + "/email";
        ctx.setVariable("logoPngUrl", assetsBase + "/autocasting_logo.png");
        ctx.setVariable("instagramPngUrl", assetsBase + "/insta_icon.png");
        ctx.setVariable("linkedinPngUrl", assetsBase + "/linkedin_icon.png");
        ctx.setVariable("resetUrl", resetUrl);
        ctx.setVariable("minutes", AppConstants.RESET_PASSWORD_EXPIRATION_TIME_MIN);
        ctx.setVariable("supportEmail", AppConstants.SUPPORT_EMAIL);
        ctx.setVariable("instagramUrl", AppConstants.INSTA_URL);
        ctx.setVariable("linkedinUrl", AppConstants.LINKEDIN_URL);

        String htmlBody = templateEngine.process("email/reset_password_email", ctx);
        String subject = messageSource.getMessage("mail.reset.subject", null, locale);
        emailService.sendHtmlEmail(user.getEmail(), subject, htmlBody);

        return new ForgotPasswordResponse(true, "Enviado");
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        String email = jwtService.extractEmail(request.token());

        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException(AUTH_USER_NOT_FOUND + "|" + email));

        if (!jwtService.isTokenValid(request.token(), user)) {
            throw new IllegalArgumentException(AUTH_INVALID_TOKEN);
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        UserEntity user = authContext.getCurrentUserOrThrow();

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException(AUTH_CURRENT_PASSWORD_MISMATCH);
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        boolean exists = userRepository.existsByEmail(request.email());

        if (exists) {
            throw new IllegalArgumentException(AUTH_USER_EXISTS);
        }

        Set<RoleEntity> roles = new HashSet<>(roleRepository.findAllByCodeIn(List.of(TALENT.name(), EMPLOYER.name()))
            .orElseThrow(() -> new IllegalArgumentException(AUTH_INVALID_ROLE)));

        final PlanEntity freePlan = planRepository.findByCode("FREE")
            .orElseThrow(() -> new IllegalStateException(AUTH_INVALID_PLAN));

        // 1) User
        UserEntity user = UserEntity.builder()
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .userAccountProvider(UserAccountProvider.LOCAL)
            .roles(roles)
            .activeMode(null)
            .talentOnboardingStatus(OnboardingStatus.NOT_STARTED)
            .employerOnboardingStatus(OnboardingStatus.NOT_STARTED)
            .build();
        userRepository.save(user);

        // 2) Profiles
        var talentProfile = TalentProfileEntity.builder()
            .user(user)
            .plan(freePlan)
            .build();
        talentProfileRepository.save(talentProfile);

        var employerProfile = EmployerProfileEntity.builder()
            .user(user)
            .plan(freePlan)
            .build();
        employerProfileRepository.save(employerProfile);

        // 3) Basic Info
        GenderOptionEntity indistinctGender =
            siteMetadataResolver.resolveGenderByCodeOrThrow(GENDER_OPTION_INDISTINCT);

        var basicInfo = BasicInfoEntity.builder()
            .talentProfile(talentProfile)
            .gender(indistinctGender)
            .build();
        basicInfoRepository.save(basicInfo);

        var employerBasicInfo = EmployerBasicInfoEntity.builder()
            .employerProfile(employerProfile)
            .build();
        employerBasicInfoRepository.save(employerBasicInfo);

        // 4) Contact
        var contact = ContactEntity.builder()
            .email(request.email())
            .talentProfile(talentProfile)
            .build();
        contactRepository.save(contact);

        // 5) Media
        var media = MediaEntity.builder()
            .talentProfile(talentProfile)
            .build();
        mediaRepository.save(media);

        // 6) Characteristics
        var characteristics = CharacteristicsEntity.builder()
            .talentProfile(talentProfile)
            .build();
        characteristicsRepository.save(characteristics);

        var profileOpt = talentProfileRepository.findByUserId(user.getId());
        String publicSlug = profileOpt.map(TalentProfileEntity::getPublicSlug).orElse(null);

        var employerProfileOpt = employerProfileRepository.findByUserId(user.getId());
        String employerProfileSlug = employerProfileOpt.map(EmployerProfileEntity::getPublicSlug).orElse(null);

        String jwt = jwtService.generateTokenWithCustomExpirationTime(user, AppConstants.EXPIRATION_TIME, publicSlug, employerProfileSlug);
        return new AuthResponse(jwt);
    }

    @Transactional
    @Override
    public MeResponse updateOnboarding(UserOnboardingRequest request) {
        UserEntity user = authContext.getCurrentUserOrThrow();

        user.setActiveMode(request.activeMode());
        user.setTalentOnboardingStatus(request.talentOnboardingStatus());
        user.setEmployerOnboardingStatus(request.employerOnboardingStatus());

        userRepository.save(user);

        return MeResponse.from(user);
    }

    private AuthResponse buildAuthResponse(UserEntity user) {
        var talentProfileOpt = talentProfileRepository.findByUserId(user.getId());
        String talentProfileSlug = talentProfileOpt.map(TalentProfileEntity::getPublicSlug).orElse(null);

        var employerProfileOpt = employerProfileRepository.findByUserId(user.getId());
        String employerProfileSlug = employerProfileOpt.map(EmployerProfileEntity::getPublicSlug).orElse(null);

        String jwt = jwtService.generateTokenWithCustomExpirationTime(
            user,
            AppConstants.EXPIRATION_TIME,
            talentProfileSlug,
            employerProfileSlug
        );

        return new AuthResponse(jwt);
    }

}
