package com.padimasso.autocasting.application.email.controller;

import com.padimasso.autocasting.application.email.service.EmailPreferencesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.padimasso.autocasting.config.AppConstants.EMAIL_UNSUBSCRIBE_API_URL;

@RestController
@RequiredArgsConstructor
@Tag(name = "Email preferences", description = "Public endpoints reached from the links of marketing emails.")
public class EmailPreferencesController {

    private final EmailPreferencesService emailPreferencesService;

    @Operation(
        summary = "Unsubscribe from marketing emails",
        description = "Opts the link's user out of admin bulk emails. Called by the Frontend unsubscribe route and, as RFC 8058 one-click unsubscribe, directly by mail clients. Idempotent."
    )
    @PostMapping(EMAIL_UNSUBSCRIBE_API_URL)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribe(
        @Parameter(description = "Unsubscribe token from the email link.") @PathVariable String token
    ) {
        emailPreferencesService.unsubscribe(token);
    }
}
