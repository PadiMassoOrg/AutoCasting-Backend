package com.padimasso.autocasting.application.legal.controller;

import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.legal.dto.response.LegalDocumentResponse;
import com.padimasso.autocasting.application.legal.model.LegalDocument;
import com.padimasso.autocasting.application.legal.model.LegalDocumentType;
import com.padimasso.autocasting.application.legal.service.LegalDocumentRenderer;
import com.padimasso.autocasting.application.legal.service.LegalService;
import com.padimasso.autocasting.config.AppConstants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class LegalController {

    private final LegalService legalService;
    private final LegalDocumentRenderer renderer;
    private final AuthContext authContext;

    @GetMapping(AppConstants.LEGAL_CURRENT_DOCUMENT_API_URL)
    public ResponseEntity<LegalDocumentResponse> current(
        @RequestParam LegalDocumentType type,
        @RequestParam String locale
    ) {
        LegalDocument doc = legalService.getCurrent(type, locale)
            .orElseThrow(() -> new IllegalStateException("No hay documento vigente para " + type + " / " + locale));

        String html = renderer.renderHtml(doc);

        LegalDocumentResponse dto = new LegalDocumentResponse(
            doc.getId(), doc.getType(), doc.getLocale(), doc.getVersion(),
            doc.getTitle(), doc.getSlug(), doc.getEffectiveAt(), html, doc.getContentHash()
        );
        return ResponseEntity.ok(dto);
    }

    public record AcceptReq(UUID documentId) {}

    @PostMapping(AppConstants.LEGAL_ACCEPT_DOCUMENT_API_URL)
    public ResponseEntity<Void> accept(@RequestBody AcceptReq req, HttpServletRequest http) {
        UUID userId = authContext.getCurrentUserOrThrow().getId();

        // Si estás detrás de proxy, habilita forward-headers (ver nota abajo)
        String ip = clientIp(http);
        String ua = http.getHeader("User-Agent");

        legalService.accept(userId, req.documentId(), ip, ua);
        return ResponseEntity.noContent().build();
    }

    private String clientIp(HttpServletRequest req) {
        String h = req.getHeader("X-Forwarded-For");
        if (h != null && !h.isBlank()) return h.split(",")[0].trim();
        return req.getRemoteAddr();
    }
}
