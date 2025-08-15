package com.padimasso.autocasting.application.sitemetadata.service;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataResponse;
import com.padimasso.autocasting.application.sitemetadata.dto.response.VersionResponse;

public interface SiteMetadataService {
    SiteMetadataResponse getSiteMetadata();

    VersionResponse getVersionOnly();
}
