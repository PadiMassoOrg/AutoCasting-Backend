package com.padimasso.autocasting.application.sitemetadata.controller;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataResponse;
import com.padimasso.autocasting.application.sitemetadata.dto.response.VersionResponse;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataService;
import com.padimasso.autocasting.config.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Site Metadata", description = "Site-wide metadata and version endpoints.")
@SuppressWarnings("unused")
public class SiteMetadataController {

    private final SiteMetadataService siteMetadataService;

    @Operation(
        summary = "Get site metadata",
        description = "Returns full site metadata. Supports ETag revalidation via If-None-Match.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(AppConstants.SITE_METADATA_URL)
    public ResponseEntity<SiteMetadataResponse> getMetadata(
        @Parameter(description = "Optional ETag value for conditional requests.") @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch
    ) {
        // ETag-based revalidation (version)
        VersionResponse version = siteMetadataService.getVersionOnly();
        String etag = "\"" + version.version() + "\"";

        if (etag.equals(ifNoneMatch)) {
            // Client already has this version -> 304 Not Modified
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                .eTag(etag)
                .cacheControl(CacheControl.noCache()) // force ETag revalidation
                .build();
        }

        SiteMetadataResponse body = siteMetadataService.getSiteMetadata();
        return ResponseEntity.ok()
            .eTag(etag)
            .cacheControl(CacheControl.noCache())
            .body(body);
    }

    @Operation(
        summary = "Get site metadata version",
        description = "Returns only the current site metadata version token.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(AppConstants.SITE_METADATA_VERSION_URL)
    public VersionResponse getVersion() {
        return siteMetadataService.getVersionOnly();
    }

}
