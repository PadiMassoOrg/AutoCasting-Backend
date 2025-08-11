package com.padimasso.autocasting.application.profile.service;

import com.padimasso.autocasting.application.profile.dto.request.ContactPatchRequest;
import com.padimasso.autocasting.application.profile.dto.response.ContactResponse;

public interface ContactService {
    ContactResponse patchMyContact(ContactPatchRequest request);
}
