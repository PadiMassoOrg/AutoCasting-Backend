package com.padimasso.autocasting.application.auth.service.impl;

import com.padimasso.autocasting.application.auth.service.EmailService;
import com.padimasso.autocasting.exception.ApiException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            helper.setFrom("noreply@autocasting.com");
            helper.addInline("autocasting-logo", new ClassPathResource("static/email/autocasting_logo.png"));

            mailSender.send(message);
            log.info("Email sent to {}", to);
        } catch (MessagingException e) {
            log.error("Error sending email", e);
            throw ApiException.internal(e, "mail.send_failed");
        }
    }
}
