package com.padimasso.autocasting.application.employer.controller;

import com.padimasso.autocasting.application.employer.dto.request.EmployerBasicInfoPatchRequest;
import com.padimasso.autocasting.application.employer.dto.response.EmployerBasicInfoResponse;
import com.padimasso.autocasting.application.employer.dto.response.EmployerProfileResponse;
import com.padimasso.autocasting.application.employer.service.EmployerBasicInfoService;
import com.padimasso.autocasting.application.employer.service.EmployerProfileService;
import com.padimasso.autocasting.application.talent.dto.request.SocialMediaPatchRequest;
import com.padimasso.autocasting.application.talent.dto.response.SocialMediaResponse;
import com.padimasso.autocasting.application.talent.service.SocialMediaService;
import com.padimasso.autocasting.config.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Employer Profile", description = "Endpoints for employer profile management.")
@SuppressWarnings("unused")
public class EmployerProfileController {

    private final EmployerProfileService employerProfileService;
    private final EmployerBasicInfoService employerBasicInfoService;
    private final SocialMediaService socialMediaService;

    //    Profile
    @Operation(
        summary = "Get my employer profile",
        description = "Returns the full employer profile for the authenticated employer user.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(AppConstants.EMPLOYER_PROFILE_API_URL)
    public ResponseEntity<EmployerProfileResponse> getMyProfile() {
        return ResponseEntity.ok(employerProfileService.getMyProfile());
    }

    //    Basic Info
    @PatchMapping(AppConstants.EMPLOYER_PROFILE_API_URL + "/basic-info")
    @Operation(
        summary = "Patch my basic info",
        description = "Partially updates the authenticated employer's basic profile information.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<EmployerBasicInfoResponse> patchMyBasicInfo(@Valid @RequestBody EmployerBasicInfoPatchRequest request) {
        return ResponseEntity.ok(employerBasicInfoService.patchMyBasicInfo(request));
    }

    //    Social Media
    @PatchMapping(AppConstants.EMPLOYER_PROFILE_API_URL + "/social-media")
    @Operation(
        summary = "Patch my social media",
        description = "Partially updates social media links for the authenticated employer.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<SocialMediaResponse> patchMySocialMedia(
        @Valid @RequestBody SocialMediaPatchRequest request
    ) {
        return ResponseEntity.ok(socialMediaService.patchMyEmployerSocialMedia(request));
    }
}
