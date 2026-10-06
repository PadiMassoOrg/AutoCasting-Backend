package com.padimasso.autocasting.application.talent.service;

import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.dto.request.ContactPatchRequest;
import com.padimasso.autocasting.application.talent.dto.response.ContactResponse;

public interface ContactService {
    ContactResponse patchMyContact(ContactPatchRequest request);

    ContactResponse patchContact(TalentProfileEntity profile, ContactPatchRequest request);
}
