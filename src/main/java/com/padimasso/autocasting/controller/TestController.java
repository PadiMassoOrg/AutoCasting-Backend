package com.padimasso.autocasting.controller;

import com.padimasso.autocasting.config.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class TestController {

    @GetMapping(AppConstants.TEST_API_URL)
    public String testAll() {
        return "Bienvenido - ANY";
    }

    @GetMapping(AppConstants.TEST_USER_API_URL)
    public String testUser() {
        return "Bienvenido - ROLE_USER";
    }

    @GetMapping(AppConstants.TEST_ADMIN_API_URL)
    public String testAdmin() {
        return "Bienvenido - ROLE_ADMIN";
    }
}