package com.padimasso.autocasting.application.employer.controller;

import com.padimasso.autocasting.application.employer.dto.request.EmployerBasicInfoPatchRequest;
import com.padimasso.autocasting.application.employer.dto.response.EmployerBasicInfoResponse;
import com.padimasso.autocasting.application.employer.dto.response.EmployerProfileResponse;
import com.padimasso.autocasting.application.employer.service.EmployerBasicInfoService;
import com.padimasso.autocasting.application.employer.service.EmployerProfileService;
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
@Tag(name = "Perfil Empleador", description = "Operaciones relacionadas al perfil de usuario")
@SuppressWarnings("unused")
public class EmployerProfileController {

    private final EmployerProfileService employerProfileService;
    private final EmployerBasicInfoService employerBasicInfoService;

    //    Profile
    @Operation(summary = "Obtener mi perfil", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(AppConstants.EMPLOYER_PROFILE_API_URL)
    public ResponseEntity<EmployerProfileResponse> getMyProfile() {
        return ResponseEntity.ok(employerProfileService.getMyProfile());
    }

    //    Basic Info
    @PatchMapping(AppConstants.EMPLOYER_PROFILE_API_URL + "/basic-info")
    @Operation(summary = "PATCH BasicInfo (parcial)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<EmployerBasicInfoResponse> patchMyBasicInfo(@Valid @RequestBody EmployerBasicInfoPatchRequest request) {
        return ResponseEntity.ok(employerBasicInfoService.patchMyBasicInfo(request));
    }
    
}
