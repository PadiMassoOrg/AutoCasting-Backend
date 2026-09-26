package com.padimasso.autocasting.application.proposal.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record ProposalInternalReferenceRequest(
    @Size(max = 255, message = "proposals.contact_name_max_length")
    String contactName,
    @Email(message = "proposals.contact_email_invalid")
    @Size(max = 255, message = "proposals.contact_email_max_length")
    String contactEmail,
    @Size(max = 50, message = "proposals.contact_whatsapp_max_length")
    String contactWhatsapp,
    @Size(max = 255, message = "proposals.company_name_hint_max_length")
    String companyNameHint,
    @Size(max = 3000, message = "proposals.internal_notes_max_length")
    String internalNotes
) {
}
