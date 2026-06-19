package com.padimasso.autocasting.application.admin.service;

import com.padimasso.autocasting.application.admin.dto.response.AdminCastingsPageResponse;

import java.util.List;

public interface AdminCastingService {
    AdminCastingsPageResponse listCastings(int page, int size, String q, List<String> statusIdTokens);
}
