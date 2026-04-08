package com.padimasso.autocasting.application.legal.service;

import com.padimasso.autocasting.application.legal.model.LegalDocument;
import com.padimasso.autocasting.exception.ApiException;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class LegalDocumentRenderer {

    private final TemplateEngine engine;

    public LegalDocumentRenderer() {
        var r = new StringTemplateResolver();
        r.setTemplateMode(TemplateMode.HTML);
        r.setCacheable(false);
        this.engine = new TemplateEngine();
        this.engine.setTemplateResolver(r);
    }

    public String renderHtml(LegalDocument doc) {
        return doc.getBodyTemplate();
    }

    public String sha256(String data) {
        try {
            var md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : d) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw ApiException.internal(e, "legal.hash_generation_failed");
        }
    }
}
