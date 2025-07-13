package com.padimasso.autocasting.controller;


import com.padimasso.autocasting.config.AppConstants;
import com.padimasso.autocasting.dto.request.PublicProfileRequest;
import com.padimasso.autocasting.dto.response.ProfileResponse;
import com.padimasso.autocasting.dto.response.PublicProfileResponse;
import com.padimasso.autocasting.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping(AppConstants.PROFILE_API_URL)
    @SuppressWarnings("unused")
    public ResponseEntity<ProfileResponse> getMyProfile() {
        return ResponseEntity.ok(profileService.getMyProfile());
    }

    @PostMapping(AppConstants.PUBLIC_PROFILE_API_URL)
    @SuppressWarnings("unused")
    public ResponseEntity<PublicProfileResponse> getPublicProfile(@Valid @RequestBody PublicProfileRequest publicProfileRequest) {
        return ResponseEntity.ok(profileService.getProfileBySlug(publicProfileRequest));
    }
}
