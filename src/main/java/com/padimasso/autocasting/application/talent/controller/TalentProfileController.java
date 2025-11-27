package com.padimasso.autocasting.application.talent.controller;


import com.padimasso.autocasting.application.common.dto.MatchMode;
import com.padimasso.autocasting.application.shared.web.SliceResponse;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import com.padimasso.autocasting.application.talent.dto.TalentFilter;
import com.padimasso.autocasting.application.talent.dto.request.*;
import com.padimasso.autocasting.application.talent.dto.response.*;
import com.padimasso.autocasting.application.talent.service.*;
import com.padimasso.autocasting.config.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Perfil", description = "Operaciones relacionadas al perfil de usuario")
@SuppressWarnings("unused")
public class TalentProfileController {

    private final TalentProfileService talentProfileService;
    private final BasicInfoService basicInfoService;
    private final ContactService contactService;
    private final SocialMediaService socialMediaService;
    private final MediaService mediaService;
    private final CharacteristicsService characteristicsService;
    private final TalentSearchService talentSearchService;

    //    Profile
    @Operation(summary = "Obtener mi perfil", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(AppConstants.TALENT_PROFILE_API_URL)
    public ResponseEntity<TalentProfileResponse> getMyProfile() {
        return ResponseEntity.ok(talentProfileService.getMyProfile());
    }

    @Operation(summary = "Ver perfil público", description = "Obtiene información pública del perfil por slug")
    @GetMapping(AppConstants.TALENT_PROFILE_API_URL + "/{slug}")
    public ResponseEntity<PublicProfileResponse> getPublicProfile(@PathVariable String slug) {
        return ResponseEntity.ok(talentProfileService.getProfileBySlug(slug));
    }

    //    Talent Database
    @GetMapping(AppConstants.TALENT_DATABASE_API_URL)
    public SliceResponse<TalentCardResponse> search(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String stageName,
        @RequestParam(required = false) Integer ageMin,
        @RequestParam(required = false) Integer ageMax,
        @RequestParam(required = false, name = "genderId") List<String> genderIdTokens,
        @RequestParam(required = false, name = "ethnicityId") List<String> ethnicityIdTokens,
        @RequestParam(required = false, name = "professionId") List<UUID> professionIds,
        @RequestParam(required = false, defaultValue = "ANY") MatchMode professionsMode,
        @RequestParam(required = false) Integer heightMinCm,
        @RequestParam(required = false) Integer heightMaxCm,
        @RequestParam(required = false, name = "hairColorId") List<UUID> hairColorIds,
        @RequestParam(required = false, defaultValue = "ANY") MatchMode hairColorIdsMode,
        @RequestParam(required = false, name = "eyeColorId") List<UUID> eyeColorIds,
        @RequestParam(required = false, defaultValue = "ANY") MatchMode eyeColorIdsMode,
        @RequestParam(required = false) Boolean tattoo,
        @RequestParam(required = false) Boolean passport,
        @RequestParam(required = false) Boolean drivingLicense,
        @RequestParam(required = false, name = "skillId") List<UUID> skillIds,
        @RequestParam(required = false, defaultValue = "ANY") MatchMode skillsMode,
        @RequestParam(required = false, defaultValue = "false") boolean includeNoHeadshot
    ) {
        var filter = new TalentFilter(
            stageName, ageMin, ageMax, genderIdTokens, ethnicityIdTokens,
            professionIds, professionsMode,
            heightMinCm, heightMaxCm,
            hairColorIds, hairColorIdsMode, eyeColorIds, eyeColorIdsMode,
            tattoo, passport, drivingLicense,
            skillIds, skillsMode,
            includeNoHeadshot
        );
        return talentSearchService.search(filter, page, size);
    }


    //    Basic Info
    @PatchMapping(AppConstants.TALENT_PROFILE_API_URL + "/basic-info")
    @Operation(summary = "PATCH BasicInfo (parcial)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<BasicInfoResponse> patchMyBasicInfo(@Valid @RequestBody BasicInfoPatchRequest request) {
        return ResponseEntity.ok(basicInfoService.patchMyBasicInfo(request));
    }

    //    Contact
    @PatchMapping(AppConstants.TALENT_PROFILE_API_URL + "/contact")
    @Operation(summary = "PATCH Contact (parcial)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ContactResponse> patchMyContact(@Valid @RequestBody ContactPatchRequest request) {
        return ResponseEntity.ok(contactService.patchMyContact(request));
    }

    //    Social Media
    @PatchMapping(AppConstants.TALENT_PROFILE_API_URL + "/social-media")
    @Operation(summary = "PATCH Social Media (parcial)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SocialMediaResponse> patchMySocialMedia(@Valid @RequestBody SocialMediaPatchRequest request) {
        return ResponseEntity.ok(socialMediaService.patchMySocialMedia(request));
    }

    //    Media
    @PatchMapping(AppConstants.TALENT_PROFILE_API_URL + "/media")
    @Operation(summary = "PATCH Media (parcial)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<MediaResponse> patchMyMedia(@Valid @RequestBody MediaPatchRequest request) {
        return ResponseEntity.ok(mediaService.patchMyMedia(request));
    }

    //    Characteristics
    @PatchMapping(AppConstants.TALENT_PROFILE_API_URL + "/characteristics")
    @Operation(summary = "PATCH Characteristics (parcial)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CharacteristicsResponse> patchMyCharacteristics(@Valid @RequestBody CharacteristicsPatchRequest request) {
        return ResponseEntity.ok(characteristicsService.patchMyCharacteristics(request));
    }

    //    Skills
    @PatchMapping(AppConstants.TALENT_PROFILE_API_URL + "/skills")
    @Operation(summary = "PATCH Skills (parcial)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Set<SiteMetadataObject>> patchMySkills(@Valid @RequestBody SkillsPatchRequest request) {
        return ResponseEntity.ok(talentProfileService.patchMySkills(request));
    }
}
