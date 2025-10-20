package com.padimasso.autocasting.application.talent.service;

import com.padimasso.autocasting.application.talent.dto.request.ContactPatchRequest;
import com.padimasso.autocasting.application.talent.dto.response.ContactResponse;

public interface ContactService {
    ContactResponse patchMyContact(ContactPatchRequest request);
}
