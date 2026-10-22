package com.padimasso.autocasting.application.email.service.impl;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.email.service.EmailPreferencesService;
import com.padimasso.autocasting.application.shared.util.SecureTokenGenerator;
import com.padimasso.autocasting.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.padimasso.autocasting.exception.ErrorMessageKeys.EMAIL_UNSUBSCRIBE_LINK_INVALID;

@Service
@RequiredArgsConstructor
public class EmailPreferencesServiceImpl implements EmailPreferencesService {

    private final UserRepository userRepository;
    private final SecureTokenGenerator secureTokenGenerator;

    @Override
    @Transactional
    public void unsubscribe(String token) {
        if (token == null || token.isBlank() || !userRepository.existsByEmailUnsubscribeToken(token)) {
            throw ApiException.notFound(EMAIL_UNSUBSCRIBE_LINK_INVALID);
        }
        userRepository.markMarketingEmailsUnsubscribed(token, LocalDateTime.now());
    }

    @Override
    @Transactional
    public String ensureUnsubscribeToken(UserEntity user) {
        if (user.getEmailUnsubscribeToken() != null) {
            return user.getEmailUnsubscribeToken();
        }

        String token = secureTokenGenerator.generate();
        if (userRepository.assignEmailUnsubscribeTokenIfMissing(user.getId(), token) == 0) {
            token = userRepository.findEmailUnsubscribeTokenById(user.getId()).orElse(token);
        }
        user.setEmailUnsubscribeToken(token);
        return token;
    }
}
