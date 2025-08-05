package com.padimasso.autocasting.application.sitemetadata.controller;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataResponse;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataService;
import com.padimasso.autocasting.config.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Site Metadata", description = "Metadata del sitio")
@SuppressWarnings("unused")
public class SiteMetadataController {

    private final SiteMetadataService siteMetadataService;

    @Operation(summary = "Site Metadata", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(AppConstants.SITE_METADATA_URL)
    public ResponseEntity<SiteMetadataResponse> getMetadata() {
        return ResponseEntity.ok(siteMetadataService.getSiteMetadata());
    }

}
