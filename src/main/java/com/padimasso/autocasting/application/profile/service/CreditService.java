package com.padimasso.autocasting.application.profile.service;

import com.padimasso.autocasting.application.profile.dto.request.CreditRequest;
import com.padimasso.autocasting.application.profile.dto.response.CreditResponse;

import java.util.List;
import java.util.UUID;

public interface CreditService {

    CreditResponse createCredit(CreditRequest request);

    List<CreditResponse> listMyCredits();

    CreditResponse getMyCredit(UUID id);

    CreditResponse patchMyCredit(UUID id, CreditRequest request);

    void deleteMyCredit(UUID id);

}
