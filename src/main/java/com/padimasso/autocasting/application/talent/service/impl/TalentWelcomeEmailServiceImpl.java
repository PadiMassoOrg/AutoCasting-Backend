package com.padimasso.autocasting.application.talent.service.impl;

import com.padimasso.autocasting.application.auth.service.EmailService;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.service.TalentWelcomeEmailService;
import com.padimasso.autocasting.application.talent.util.TalentCatalogVisibility;
import com.padimasso.autocasting.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static com.padimasso.autocasting.config.AppConstants.SUPPORT_EMAIL;

@Slf4j
@Service
@RequiredArgsConstructor
public class TalentWelcomeEmailServiceImpl implements TalentWelcomeEmailService {

    private static final String COMPLETE_PROFILE_PATH = "/dashboard/talent";

    private final EmailService emailService;
    private final SpringTemplateEngine templateEngine;
    private final MessageSource messageSource;
    private final AppProperties appProperties;

    @Override
    public boolean sendWelcomeEmail(TalentProfileEntity profile) {
        var basicInfo = profile.getBasicInfo();
        String talentName = basicInfo != null ? basicInfo.getStageName() : null;
        String email = profile.getUser() != null ? profile.getUser().getEmail() : null;

        if (talentName == null || email == null) {
            log.warn("Skipping talent welcome email for profileId {}: missing stage name or email", profile.getId());
            return false;
        }

        boolean visibleInCatalog = TalentCatalogVisibility.isVisibleInCatalog(profile);
        if (visibleInCatalog && profile.getPublicSlug() == null) {
            log.warn("Skipping talent welcome email for profileId {}: missing public slug", profile.getId());
            return false;
        }

        try {
            var locale = LocaleContextHolder.getLocale();
            String assetsBase = appProperties.getBackendUrl() + "/email";

            Context ctx = new Context(locale);
            ctx.setVariable("logoPngUrl", assetsBase + "/autocasting_logo.png");
            ctx.setVariable("instagramPngUrl", assetsBase + "/insta_icon.png");
            ctx.setVariable("linkedinPngUrl", assetsBase + "/linkedin_icon.png");
            ctx.setVariable("talentName", talentName);
            ctx.setVariable("supportEmail", SUPPORT_EMAIL);

            String subject;
            String templateName;

            if (visibleInCatalog) {
                ctx.setVariable("profileUrl", appProperties.getFrontendUrl() + "/profile/" + profile.getPublicSlug());
                subject = messageSource.getMessage("mail.talentWelcome.subject", null, locale);
                templateName = "email/talent_onboarding_welcome_email";
            } else {
                ctx.setVariable("completeProfileUrl", appProperties.getFrontendUrl() + COMPLETE_PROFILE_PATH);
                subject = messageSource.getMessage("mail.talentIncomplete.subject", null, locale);
                templateName = "email/talent_onboarding_incomplete_email";
            }

            String htmlBody = templateEngine.process(templateName, ctx);
            emailService.sendHtmlEmail(email, subject, htmlBody);
            return true;
        } catch (Exception e) {
            log.error("Failed to send talent welcome email for profileId {}", profile.getId(), e);
            return false;
        }
    }
}
