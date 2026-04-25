package com.padimasso.autocasting.application.legal.controller;

import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.legal.dto.response.LegalDocumentResponse;
import com.padimasso.autocasting.application.legal.model.LegalDocument;
import com.padimasso.autocasting.application.legal.model.LegalDocumentType;
import com.padimasso.autocasting.application.legal.service.LegalDocumentRenderer;
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

        LegalDocumentResponse dto = new LegalDocumentResponse(
            doc.getId(), doc.getType(), doc.getLocale(), doc.getVersion(),
            doc.getTitle(), doc.getSlug(), doc.getEffectiveAt(), html, doc.getContentHash()
        );
        return ResponseEntity.ok(dto);
    }

    public record AcceptReq(UUID documentId) {}

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

    private String clientIp(HttpServletRequest req) {
        String h = req.getHeader("X-Forwarded-For");
        if (h != null && !h.isBlank()) return h.split(",")[0].trim();
        return req.getRemoteAddr();
    }
}
