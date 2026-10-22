package com.padimasso.autocasting.application.email.service.impl;

import com.padimasso.autocasting.application.email.service.EmailService;
import com.padimasso.autocasting.application.email.service.PasswordResetEmailService;
import com.padimasso.autocasting.config.AppConstants;
import com.padimasso.autocasting.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class PasswordResetEmailServiceImpl implements PasswordResetEmailService {

    static final String TEMPLATE_NAME = "email/reset_password_email";

    private final EmailService emailService;
    private final SpringTemplateEngine templateEngine;
    private final MessageSource messageSource;
    private final AppProperties appProperties;

    @Override
    public void sendResetPasswordEmail(String to, String resetToken) {
        String resetUrl = appProperties.getFrontendUrl() + "/reset-password?token=" + resetToken;
        var locale = LocaleContextHolder.getLocale();

        Context ctx = new Context(locale);

        String assetsBase = appProperties.getBackendUrl() + "/email";
        ctx.setVariable("logoPngUrl", assetsBase + "/autocasting_logo.png");
        ctx.setVariable("instagramPngUrl", assetsBase + "/insta_icon.png");
        ctx.setVariable("linkedinPngUrl", assetsBase + "/linkedin_icon.png");
        ctx.setVariable("resetUrl", resetUrl);
        ctx.setVariable("minutes", AppConstants.RESET_PASSWORD_EXPIRATION_TIME_MIN);

        String htmlBody = templateEngine.process(TEMPLATE_NAME, ctx);
        String subject = messageSource.getMessage("mail.reset.subject", null, locale);
        emailService.sendHtmlEmail(to, subject, htmlBody);
    }
}
