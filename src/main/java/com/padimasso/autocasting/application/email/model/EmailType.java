package com.padimasso.autocasting.application.email.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Emails an admin can bulk send. Each type declares the audience it targets; users outside it are
 * never sent that email.
 */
@Getter
@RequiredArgsConstructor
public enum EmailType {
    TALENT_WELCOME(EmailAudience.TALENT);

    private final EmailAudience audience;
}
