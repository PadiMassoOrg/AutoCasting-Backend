package com.padimasso.autocasting.application.profile.service.impl;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.service.AuthContext;
import com.padimasso.autocasting.application.profile.dto.request.ContactPatchRequest;
import com.padimasso.autocasting.application.profile.dto.response.ContactResponse;
import com.padimasso.autocasting.application.profile.mapper.ProfileMapper;
import com.padimasso.autocasting.application.profile.model.ContactEntity;
import com.padimasso.autocasting.application.profile.model.ProfileEntity;
import com.padimasso.autocasting.application.profile.repository.ContactRepository;
import com.padimasso.autocasting.application.profile.repository.ProfileRepository;
import com.padimasso.autocasting.application.profile.service.ContactService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class ContactServiceImpl implements ContactService {

    private final AuthContext authContext;
    private final ProfileRepository profileRepository;
    private final ContactRepository contactRepository;
    private final ProfileMapper profileMapper;

    @Transactional
    @Override
    public ContactResponse patchMyContact(ContactPatchRequest request) {
        UserEntity user = authContext.getCurrentUserOrThrow();
        ProfileEntity profile = profileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("profile.not_found"));
        ContactEntity contact = contactRepository.findByProfileId(profile.getId())
            .orElseGet(() -> contactRepository.save(ContactEntity.builder().profile(profile).build()));

        if (request.phoneNumber() != null) {
            contact.setPhoneNumber(request.phoneNumber());
        }

        return profileMapper.toContactResponse(contactRepository.save(contact));
    }
}
