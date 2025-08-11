package com.padimasso.autocasting.application.profile.service;

import com.padimasso.autocasting.application.profile.dto.request.CreditRequest;
import com.padimasso.autocasting.application.profile.dto.response.CreditResponse;

public interface CreditService {

    CreditResponse createCredit(CreditRequest request);
}
