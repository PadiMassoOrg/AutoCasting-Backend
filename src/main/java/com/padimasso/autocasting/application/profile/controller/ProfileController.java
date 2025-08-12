package com.padimasso.autocasting.application.profile.controller;


import com.padimasso.autocasting.application.profile.dto.request.*;
import com.padimasso.autocasting.application.profile.dto.response.*;
import com.padimasso.autocasting.application.profile.service.*;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import com.padimasso.autocasting.config.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Perfil", description = "Operaciones relacionadas al perfil de usuario")
@SuppressWarnings("unused")
public class ProfileController {

    private final ProfileService profileService;
    private final BasicInfoService basicInfoService;
    private final ContactService contactService;
    private final SocialMediaService socialMediaService;
    private final MediaService mediaService;
    private final CharacteristicsService characteristicsService;

    //    Profile
    @Operation(summary = "Obtener mi perfil", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(AppConstants.PROFILE_API_URL)
    public ResponseEntity<ProfileResponse> getMyProfile() {
        return ResponseEntity.ok(profileService.getMyProfile());
    }

    @Operation(summary = "Ver perfil público", description = "Obtiene información pública del perfil por slug")
    @GetMapping(AppConstants.PROFILE_API_URL + "/{slug}")
    public ResponseEntity<PublicProfileResponse> getPublicProfile(@PathVariable String slug) {
        return ResponseEntity.ok(profileService.getProfileBySlug(slug));
    }

    //    Basic Info
    @PatchMapping(AppConstants.PROFILE_API_URL + "/basic-info")
    @Operation(summary = "PATCH BasicInfo (parcial)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<BasicInfoResponse> patchMyBasicInfo(@Valid @RequestBody BasicInfoPatchRequest request) {
        return ResponseEntity.ok(basicInfoService.patchMyBasicInfo(request));
    }

    //    Contact
    @PatchMapping(AppConstants.PROFILE_API_URL + "/contact")
    @Operation(summary = "PATCH Contact (parcial)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ContactResponse> patchMyContact(@Valid @RequestBody ContactPatchRequest request) {
        return ResponseEntity.ok(contactService.patchMyContact(request));
    }

    //    Social Media
    @PatchMapping(AppConstants.PROFILE_API_URL + "/social-media")
    @Operation(summary = "PATCH Social Media (parcial)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SocialMediaResponse> patchMySocialMedia(@Valid @RequestBody SocialMediaPatchRequest request) {
        return ResponseEntity.ok(socialMediaService.patchMySocialMedia(request));
    }

    //    Media
    @PatchMapping(AppConstants.PROFILE_API_URL + "/media")
    @Operation(summary = "PATCH Media (parcial)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<MediaResponse> patchMyMedia(@Valid @RequestBody MediaPatchRequest request) {
        return ResponseEntity.ok(mediaService.patchMyMedia(request));
    }

    //    Characteristics
    @PatchMapping(AppConstants.PROFILE_API_URL + "/characteristics")
    @Operation(summary = "PATCH Characteristics (parcial)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CharacteristicsResponse> patchMyCharacteristics(@Valid @RequestBody CharacteristicsPatchRequest request) {
        return ResponseEntity.ok(characteristicsService.patchMyCharacteristics(request));
    }

    //    Skills
    @PatchMapping(AppConstants.PROFILE_API_URL + "/skills")
    @Operation(summary = "PATCH Skills (parcial)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Set<SiteMetadataObject>> patchMySkills(@Valid @RequestBody SkillsPatchRequest request) {
        return ResponseEntity.ok(profileService.patchMySkills(request));
    }
}
