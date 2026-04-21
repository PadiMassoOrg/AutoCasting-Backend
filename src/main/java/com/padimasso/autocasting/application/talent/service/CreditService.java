package com.padimasso.autocasting.application.talent.service;

import com.padimasso.autocasting.application.common.dto.LastModifiedResponse;
import com.padimasso.autocasting.application.talent.dto.request.CreditRequest;
import com.padimasso.autocasting.application.talent.dto.response.CreditResponse;

import java.util.List;
import java.util.UUID;

public interface CreditService {

    CreditResponse createCredit(CreditRequest request);

    List<CreditResponse> listMyCredits();

    CreditResponse getMyCredit(UUID id);

    CreditResponse patchMyCredit(UUID id, CreditRequest request);

    LastModifiedResponse deleteMyCredit(UUID id);

}
