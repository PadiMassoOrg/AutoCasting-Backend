package com.padimasso.autocasting.controller;


import com.padimasso.autocasting.config.AppConstants;
import com.padimasso.autocasting.dto.response.ProfileResponse;
import com.padimasso.autocasting.dto.response.PublicProfileResponse;
import com.padimasso.autocasting.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping(AppConstants.PUBLIC_PROFILE_API_URL + "/{slug}")
    @SuppressWarnings("unused")
    public ResponseEntity<PublicProfileResponse> getPublicProfile(@PathVariable String slug) {
        return ResponseEntity.ok(profileService.getProfileBySlug(slug));
    }
}
