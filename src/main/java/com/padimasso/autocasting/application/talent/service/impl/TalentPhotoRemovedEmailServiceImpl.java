package com.padimasso.autocasting.application.talent.service.impl;

import com.padimasso.autocasting.application.auth.service.EmailService;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.service.TalentPhotoRemovedEmailService;
import com.padimasso.autocasting.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TalentPhotoRemovedEmailServiceImpl implements TalentPhotoRemovedEmailService {

    static final String TEMPLATE_NAME = "email/talent_photo_removed_email";
    static final List<String> EXAMPLE_PHOTO_FILES = List.of("talent_photo_example_1.jpg", "talent_photo_example_2.jpg");

    private final EmailService emailService;
    private final SpringTemplateEngine templateEngine;
    private final MessageSource messageSource;
    private final AppProperties appProperties;

    @Override
    public boolean sendPhotoRemovedEmail(TalentProfileEntity profile) {
        var basicInfo = profile.getBasicInfo();
        String talentName = basicInfo != null ? basicInfo.getStageName() : null;
        String email = profile.getUser() != null ? profile.getUser().getEmail() : null;

        if (talentName == null || email == null) {
            log.warn("Skipping talent photo removed email for profileId {}: missing stage name or email", profile.getId());
            return false;
        }

        try {
            var locale = LocaleContextHolder.getLocale();
            Context ctx = new Context(locale);
            ctx.setVariable("talentName", talentName);
            ctx.setVariable("editProfileUrl", appProperties.getFrontendUrl() + TalentWelcomeEmailServiceImpl.COMPLETE_PROFILE_PATH);
            Map<String, Resource> examplePhotos = examplePhotoResources();
            ctx.setVariable("examplePhotoCids", List.copyOf(examplePhotos.keySet()));

            String subject = messageSource.getMessage("mail.talentPhotoRemoved.subject", null, locale);
            String htmlBody = templateEngine.process(TEMPLATE_NAME, ctx);
            emailService.sendHtmlEmail(email, subject, htmlBody, examplePhotos);
            return true;
        } catch (Exception e) {
            log.error("Failed to send talent photo removed email for profileId {}", profile.getId(), e);
            return false;
        }
    }

    private static Map<String, Resource> examplePhotoResources() {
        Map<String, Resource> photos = new LinkedHashMap<>();
        for (int i = 0; i < EXAMPLE_PHOTO_FILES.size(); i++) {
            var resource = new ClassPathResource("static/email/" + EXAMPLE_PHOTO_FILES.get(i));
            if (resource.exists()) photos.put("talent-photo-example-" + (i + 1), resource);
        }
        return photos;
    }
}
