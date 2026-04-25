package com.padimasso.autocasting.application.talent.controller;


import com.padimasso.autocasting.application.common.dto.MatchMode;
import com.padimasso.autocasting.application.shared.web.SliceResponse;
import com.padimasso.autocasting.application.talent.dto.TalentFilter;
import com.padimasso.autocasting.application.talent.dto.request.*;
import com.padimasso.autocasting.application.talent.dto.response.*;
import com.padimasso.autocasting.application.talent.service.*;
import com.padimasso.autocasting.config.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Talent Profile", description = "Endpoints for talent profile management and talent discovery.")
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
    @Operation(
        summary = "Get my talent profile",
        description = "Returns the full talent profile for the authenticated talent user.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(AppConstants.TALENT_PROFILE_API_URL)
    public ResponseEntity<TalentProfileResponse> getMyProfile() {
        return ResponseEntity.ok(talentProfileService.getMyProfile());
    }

    @Operation(summary = "Get public talent profile by slug", description = "Returns the public profile details for the provided talent slug.")
    @GetMapping(AppConstants.TALENT_PROFILE_API_URL + "/{slug}")
    public ResponseEntity<PublicProfileResponse> getPublicProfile(
        @Parameter(description = "Public profile slug.") @PathVariable String slug
    ) {
        return ResponseEntity.ok(talentProfileService.getProfileBySlug(slug));
    }

    //    Talent Database
    @Operation(
        summary = "Search talent database",
        description = "Returns paginated talent cards using advanced filters.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(AppConstants.TALENT_DATABASE_API_URL)
    public SliceResponse<TalentCardResponse> search(
        @Parameter(description = "Page index, starting from 0.") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size.") @RequestParam(defaultValue = "10") int size,
        @Parameter(description = "Stage name filter.") @RequestParam(required = false) String stageName,
        @Parameter(description = "Minimum age filter.") @RequestParam(required = false) Integer ageMin,
        @Parameter(description = "Maximum age filter.") @RequestParam(required = false) Integer ageMax,
        @Parameter(description = "Gender token filters.") @RequestParam(required = false, name = "genderId") List<String> genderIdTokens,
        @Parameter(description = "Ethnicity token filters.") @RequestParam(required = false, name = "ethnicityId") List<String> ethnicityIdTokens,
        @Parameter(description = "Profession IDs filter.") @RequestParam(required = false, name = "professionId") List<UUID> professionIds,
        @Parameter(description = "Match mode for profession IDs.") @RequestParam(required = false, defaultValue = "ANY") MatchMode professionsMode,
        @Parameter(description = "Minimum height in cm.") @RequestParam(required = false) Integer heightMinCm,
        @Parameter(description = "Maximum height in cm.") @RequestParam(required = false) Integer heightMaxCm,
        @Parameter(description = "Hair color IDs filter.") @RequestParam(required = false, name = "hairColorId") List<UUID> hairColorIds,
        @Parameter(description = "Match mode for hair color IDs.") @RequestParam(required = false, defaultValue = "ANY") MatchMode hairColorIdsMode,
        @Parameter(description = "Eye color IDs filter.") @RequestParam(required = false, name = "eyeColorId") List<UUID> eyeColorIds,
        @Parameter(description = "Match mode for eye color IDs.") @RequestParam(required = false, defaultValue = "ANY") MatchMode eyeColorIdsMode,
        @Parameter(description = "Tattoo filter.") @RequestParam(required = false) Boolean tattoo,
        @Parameter(description = "Passport filter.") @RequestParam(required = false) Boolean passport,
        @Parameter(description = "Driving license filter.") @RequestParam(required = false) Boolean drivingLicense,
        @Parameter(description = "Skill IDs filter.") @RequestParam(required = false, name = "skillId") List<UUID> skillIds,
        @Parameter(description = "Match mode for skill IDs.") @RequestParam(required = false, defaultValue = "ANY") MatchMode skillsMode,
        @Parameter(description = "Include profiles without headshot.") @RequestParam(required = false, defaultValue = "false") boolean includeNoHeadshot
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
    @Operation(
        summary = "Patch my basic info",
        description = "Partially updates the authenticated talent's basic profile information.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<BasicInfoResponse> patchMyBasicInfo(@Valid @RequestBody BasicInfoPatchRequest request) {
        return ResponseEntity.ok(basicInfoService.patchMyBasicInfo(request));
    }

    //    Contact
    @PatchMapping(AppConstants.TALENT_PROFILE_API_URL + "/contact")
    @Operation(
        summary = "Patch my contact info",
        description = "Partially updates contact information for the authenticated talent.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ContactResponse> patchMyContact(@Valid @RequestBody ContactPatchRequest request) {
        return ResponseEntity.ok(contactService.patchMyContact(request));
    }

    //    Social Media
    @PatchMapping(AppConstants.TALENT_PROFILE_API_URL + "/social-media")
    @Operation(
        summary = "Patch my social media",
        description = "Partially updates social media links for the authenticated talent.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<SocialMediaResponse> patchMySocialMedia(
        @Valid @RequestBody SocialMediaPatchRequest request
    ) {
        return ResponseEntity.ok(socialMediaService.patchMySocialMedia(request));
    }

    //    Media
    @PatchMapping(AppConstants.TALENT_PROFILE_API_URL + "/media")
    @Operation(
        summary = "Patch my media",
        description = "Partially updates media fields for the authenticated talent profile.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<MediaResponse> patchMyMedia(@Valid @RequestBody MediaPatchRequest request) {
        return ResponseEntity.ok(mediaService.patchMyMedia(request));
    }

    //    Characteristics
    @PatchMapping(AppConstants.TALENT_PROFILE_API_URL + "/characteristics")
    @Operation(
        summary = "Patch my characteristics",
        description = "Partially updates physical and profile characteristics for the authenticated talent.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<CharacteristicsResponse> patchMyCharacteristics(@Valid @RequestBody CharacteristicsPatchRequest request) {
        return ResponseEntity.ok(characteristicsService.patchMyCharacteristics(request));
    }

    //    Skills
    @PatchMapping(AppConstants.TALENT_PROFILE_API_URL + "/skills")
    @Operation(
        summary = "Patch my skills",
        description = "Replaces or updates selected skills for the authenticated talent.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<SkillsResponse> patchMySkills(@Valid @RequestBody SkillsPatchRequest request) {
        return ResponseEntity.ok(talentProfileService.patchMySkills(request));
    }
}
