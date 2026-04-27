package com.padimasso.autocasting.application.legal.controller;

import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.legal.dto.response.LegalDocumentResponse;
import com.padimasso.autocasting.application.legal.dto.response.LegalRequirementsResponse;
import com.padimasso.autocasting.application.legal.model.LegalDocument;
import com.padimasso.autocasting.application.legal.model.LegalDocumentType;
import com.padimasso.autocasting.application.legal.service.LegalDocumentRenderer;
import com.padimasso.autocasting.application.legal.service.LegalPdfUrlService;
import com.padimasso.autocasting.application.legal.service.LegalService;
import com.padimasso.autocasting.config.AppConstants;
import com.padimasso.autocasting.exception.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Legal", description = "Endpoints for legal documents and user acceptances.")
public class LegalController {

    private final LegalService legalService;
    private final LegalDocumentRenderer renderer;
    private final LegalPdfUrlService legalPdfUrlService;
    private final AuthContext authContext;

    @Operation(
        summary = "Get current legal document",
        description = "Returns the currently active legal document for the requested type and locale."
    )
    @GetMapping(AppConstants.LEGAL_CURRENT_DOCUMENT_API_URL)
    public ResponseEntity<LegalDocumentResponse> current(
        @Parameter(description = "Legal document type.") @RequestParam LegalDocumentType type,
        @Parameter(description = "Locale code (for example: en, es, es-MX).") @RequestParam String locale
    ) {
        LegalDocument doc = legalService.getCurrent(type, locale)
            .orElseThrow(() -> ApiException.notFound("legal.current_document.not_found", type, locale));

        String html = renderer.renderHtml(doc);
        String pdfDownloadUrl = legalPdfUrlService.buildSignedDownloadUrl(doc).orElse(null);
        String pdfObjectKey = legalPdfUrlService.buildObjectKey(doc);

        LegalDocumentResponse dto = new LegalDocumentResponse(
            doc.getId(), doc.getType(), doc.getLocale(), doc.getVersion(),
            doc.getTitle(), doc.getSlug(), doc.getEffectiveAt(), html, doc.getContentHash(),
            pdfDownloadUrl, pdfObjectKey
        );
        return ResponseEntity.ok(dto);
    }

    public record AcceptReq(UUID documentId) {}
    public record AcceptCurrentReq(String locale, Boolean agreeTerms, Boolean agreePrivacy) {}

    @Operation(
        summary = "Get legal acceptance requirements",
        description = "Returns current required legal documents and whether the authenticated user accepted them.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(AppConstants.LEGAL_REQUIREMENTS_API_URL)
    public ResponseEntity<LegalRequirementsResponse> requirements(
        @Parameter(description = "Locale code (for example: en, es, es-MX).") @RequestParam(required = false) String locale
    ) {
        UUID userId = authContext.getCurrentUserOrThrow().getId();
        String normalizedLocale = normalizeLocale(locale);
        var requiredDocuments = legalService.getCurrentRequirements(userId, normalizedLocale);
        boolean acceptedCurrent = requiredDocuments.stream().allMatch(r -> r.acceptedCurrent());
        return ResponseEntity.ok(new LegalRequirementsResponse(normalizedLocale, acceptedCurrent, requiredDocuments));
    }

    @Operation(
        summary = "Accept legal document",
        description = "Registers acceptance of a legal document by the authenticated user.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(AppConstants.LEGAL_ACCEPT_DOCUMENT_API_URL)
    public ResponseEntity<Void> accept(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Legal acceptance payload containing the document ID.",
            required = true
        )
        @RequestBody AcceptReq req,
        HttpServletRequest http
    ) {
        UUID userId = authContext.getCurrentUserOrThrow().getId();

        // If you are behind a proxy, enable forward headers.
        String ip = clientIp(http);
        String ua = http.getHeader("User-Agent");

        legalService.accept(userId, req.documentId(), ip, ua);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Accept current required legal documents",
        description = "Registers acceptance of currently required legal documents (TERMS and PRIVACY) for the authenticated user.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(AppConstants.LEGAL_ACCEPT_CURRENT_API_URL)
    public ResponseEntity<LegalRequirementsResponse> acceptCurrent(
        @RequestBody(required = false) AcceptCurrentReq req,
        HttpServletRequest http
    ) {
        if (req == null || !Boolean.TRUE.equals(req.agreeTerms()) || !Boolean.TRUE.equals(req.agreePrivacy())) {
            throw ApiException.badRequest("legal.acceptance.explicit_required");
        }
        UUID userId = authContext.getCurrentUserOrThrow().getId();
        String locale = normalizeLocale(req.locale());
        String ip = clientIp(http);
        String ua = http.getHeader("User-Agent");

        legalService.acceptCurrentRequired(userId, locale, ip, ua);
        var requiredDocuments = legalService.getCurrentRequirements(userId, locale);
        boolean acceptedCurrent = requiredDocuments.stream().allMatch(r -> r.acceptedCurrent());
        return ResponseEntity.ok(new LegalRequirementsResponse(locale, acceptedCurrent, requiredDocuments));
    }

    private String clientIp(HttpServletRequest req) {
        String h = req.getHeader("X-Forwarded-For");
        if (h != null && !h.isBlank()) return h.split(",")[0].trim();
        return req.getRemoteAddr();
    }

    private String normalizeLocale(String locale) {
        if (locale == null || locale.isBlank()) return "es";
        String normalized = locale.trim().toLowerCase();
        if (normalized.startsWith("es")) return "es";
        return "es";
    }
}
