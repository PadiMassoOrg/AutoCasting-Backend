package com.padimasso.autocasting.application.applications.service;

import com.padimasso.autocasting.application.applications.dto.request.CastingApplicationRequest;

import java.util.UUID;

public interface CastingApplicationService {
    void apply(UUID roleId, CastingApplicationRequest request);
}
