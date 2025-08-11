package com.padimasso.autocasting.application.profile.controller;


import com.padimasso.autocasting.application.profile.dto.request.BasicInfoPatchRequest;
import com.padimasso.autocasting.application.profile.dto.response.BasicInfoResponse;
import com.padimasso.autocasting.application.profile.dto.response.ProfileResponse;
import com.padimasso.autocasting.application.profile.dto.response.PublicProfileResponse;
import com.padimasso.autocasting.application.profile.service.BasicInfoService;
import com.padimasso.autocasting.application.profile.service.ProfileService;
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
@Tag(name = "Perfil", description = "Operaciones relacionadas al perfil de usuario")
@SuppressWarnings("unused")
public class ProfileController {

    private final ProfileService profileService;
    private final BasicInfoService basicInfoService;

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
//    Social Media
//    Media
//    Skills
//    Credits
//    Education
}
