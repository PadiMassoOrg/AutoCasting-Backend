package com.padimasso.autocasting.application.legal.service;

import com.padimasso.autocasting.application.legal.model.LegalDocument;
import com.padimasso.autocasting.application.legal.model.ContentFormat;
import com.padimasso.autocasting.exception.ApiException;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class LegalDocumentRenderer {

    private final TemplateEngine engine;
    private final Parser markdownParser;
    private final HtmlRenderer markdownRenderer;
    private final Safelist markdownSafelist;

    public LegalDocumentRenderer() {
        var r = new StringTemplateResolver();
        r.setTemplateMode(TemplateMode.HTML);
        r.setCacheable(false);
        this.engine = new TemplateEngine();
        this.engine.setTemplateResolver(r);
        this.markdownParser = Parser.builder().build();
        this.markdownRenderer = HtmlRenderer.builder().build();
        this.markdownSafelist = Safelist.relaxed()
            .addTags("table", "thead", "tbody", "tfoot", "tr", "th", "td")
            .addAttributes("a", "target", "rel")
            .addProtocols("a", "href", "http", "https", "mailto");
    }

    public String renderHtml(LegalDocument doc) {
        if (doc.getFormat() == ContentFormat.MARKDOWN) {
            String markdownHtml = markdownRenderer.render(markdownParser.parse(doc.getBodyTemplate()));
            String safeHtml = Jsoup.clean(markdownHtml, markdownSafelist);
            return "<article class=\"prose prose-neutral max-w-3xl mx-auto\">" + safeHtml + "</article>";
        }
        return doc.getBodyTemplate(); // Backward compatibility with legacy HTML docs
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
