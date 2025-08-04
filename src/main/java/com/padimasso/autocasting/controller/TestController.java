package com.padimasso.autocasting.controller;

import com.padimasso.autocasting.config.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "2. Test", description = "Endpoints de prueba de roles y autenticación")
@SuppressWarnings("unused")
public class TestController {

    @Operation(summary = "Prueba abierta", description = "Endpoint accesible sin autenticación")
    @GetMapping(AppConstants.TEST_API_URL)
    public String testAll() {
        return "Bienvenido - ANY";
    }

    @Operation(summary = "Prueba usuario", description = "Solo accesible para ROLE_USER")
    @GetMapping(AppConstants.TEST_USER_API_URL)
    public String testUser() {
        return "Bienvenido - ROLE_USER";
    }

    @Operation(summary = "Prueba admin", description = "Solo accesible para ROLE_ADMIN")
    @GetMapping(AppConstants.TEST_ADMIN_API_URL)
    public String testAdmin() {
        return "Bienvenido - ROLE_ADMIN";
    }
}
