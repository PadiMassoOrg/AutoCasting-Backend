package com.padimasso.autocasting.application.auth.service.impl;

import com.padimasso.autocasting.application.auth.dto.request.*;
import com.padimasso.autocasting.application.auth.dto.response.AuthResponse;
import com.padimasso.autocasting.application.auth.dto.response.ForgotPasswordResponse;
import com.padimasso.autocasting.application.auth.model.RoleEntity;
import com.padimasso.autocasting.application.auth.model.UserAccountProvider;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.repository.RoleRepository;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.auth.service.AuthContext;
import com.padimasso.autocasting.application.auth.service.AuthService;
import com.padimasso.autocasting.application.auth.service.EmailService;
import com.padimasso.autocasting.application.auth.service.JwtService;
import com.padimasso.autocasting.application.plan.model.PlanEntity;
import com.padimasso.autocasting.application.plan.repository.PlanRepository;
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

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class AuthServiceImpl implements AuthService {

    private final SpringTemplateEngine templateEngine;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PlanRepository planRepository;
    private final TalentProfileRepository talentProfileRepository;
    private final BasicInfoRepository basicInfoRepository;
    private final ContactRepository contactRepository;
    private final SocialMediaRepository socialMediaRepository;
    private final MediaRepository mediaRepository;
    private final CharacteristicsRepository characteristicsRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final AppProperties appProperties;
    private final MessageSource messageSource;
    private final AuthContext authContext;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        boolean exists = userRepository.existsByEmail(request.email());
        if (exists) {
            throw new IllegalArgumentException("auth.user_exists");
        }

        final RoleEntity foundRole = roleRepository.findByCode(request.role())
            .orElseThrow(() -> new IllegalArgumentException("auth.invalid_role"));
        final PlanEntity freePlan = planRepository.findByCode("FREE")
            .orElseThrow(() -> new IllegalStateException("auth.invalid_plan"));

        UserEntity user = UserEntity.builder()
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .userAccountProvider(UserAccountProvider.LOCAL)
            .role(foundRole)
            .build();
        userRepository.save(user);

        var profile = TalentProfileEntity.builder()
            .user(user)
            .plan(freePlan)
            .build();
        talentProfileRepository.save(profile);

        var basicInfo = BasicInfoEntity.builder()
            .stageName(request.name())
            .talentProfile(profile)
            .build();
        basicInfoRepository.save(basicInfo);

        var contact = ContactEntity.builder()
            .email(request.email())
            .talentProfile(profile)
            .build();
        contactRepository.save(contact);

        var socialMedia = SocialMediaEntity.builder()
            .talentProfile(profile)
            .build();
        socialMediaRepository.save(socialMedia);

        var media = MediaEntity.builder()
            .talentProfile(profile)
            .build();
        mediaRepository.save(media);

        var characteristics = CharacteristicsEntity.builder()
            .talentProfile(profile)
            .build();
        characteristicsRepository.save(characteristics);

        String jwt = jwtService.generateTokenWithCustomExpirationTime(user, AppConstants.EXPIRATION_TIME);
        return new AuthResponse(jwt);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new IllegalArgumentException("auth.invalid_credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("auth.invalid_credentials");
        }

        String jwt = jwtService.generateTokenWithCustomExpirationTime(user, AppConstants.EXPIRATION_TIME);
        return new AuthResponse(jwt);
    }

    @Override
    public ForgotPasswordResponse sendResetPasswordEmail(ForgotPasswordRequest request) {
        var user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new IllegalArgumentException("auth.user_not_found"));

        if (user.getUserAccountProvider() != UserAccountProvider.LOCAL) {
            throw new IllegalArgumentException("auth.password_reset_external");
        }

        String token = jwtService.generateTokenWithCustomExpirationTime(
            user, AppConstants.RESET_PASSWORD_EXPIRATION_TIME
        );

        String resetUrl = appProperties.getFrontendUrl() + "/reset-password?token=" + token;
        var locale = LocaleContextHolder.getLocale();

        Context ctx = new Context(locale);

        String assetsBase = appProperties.getBackendUrl() + "/email";  // ✅ correcto
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
            .orElseThrow(() -> new IllegalArgumentException("auth.user_not_found|" + email));

        if (!jwtService.isTokenValid(request.token(), user)) {
            throw new IllegalArgumentException("auth.invalid_token");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        UserEntity user = authContext.getCurrentUserOrThrow();
        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("auth.invalid_credentials");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

}

