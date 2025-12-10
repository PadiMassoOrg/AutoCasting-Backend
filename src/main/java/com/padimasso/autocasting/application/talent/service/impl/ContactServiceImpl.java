package com.padimasso.autocasting.application.talent.service.impl;

import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.talent.dto.request.ContactPatchRequest;
import com.padimasso.autocasting.application.talent.dto.response.ContactResponse;
import com.padimasso.autocasting.application.talent.mapper.TalentProfileMapper;
import com.padimasso.autocasting.application.talent.model.ContactEntity;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.repository.ContactRepository;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import com.padimasso.autocasting.application.talent.service.ContactService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class ContactServiceImpl implements ContactService {

    private final AuthContext authContext;
    private final TalentProfileRepository talentProfileRepository;
    private final ContactRepository contactRepository;
    private final TalentProfileMapper talentProfileMapper;

    @Transactional
    @Override
    public ContactResponse patchMyContact(ContactPatchRequest request) {
        UserEntity user = authContext.getCurrentUserOrThrow();
        TalentProfileEntity profile = talentProfileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("profile.not_found"));
        ContactEntity contact = contactRepository.findByTalentProfileId(profile.getId())
            .orElseGet(() -> contactRepository.save(ContactEntity.builder().talentProfile(profile).build()));

        if (request.phoneNumber() != null) {
            contact.setPhoneNumber(request.phoneNumber());
        }

        return talentProfileMapper.toContactResponse(contactRepository.save(contact));
    }
}
