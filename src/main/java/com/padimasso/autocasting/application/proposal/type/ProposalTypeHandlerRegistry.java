package com.padimasso.autocasting.application.proposal.type;

import com.padimasso.autocasting.application.proposal.model.ProposalEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.padimasso.autocasting.exception.ErrorMessageKeys.PROPOSALS_TYPE_NOT_FOUND;

@Component
public class ProposalTypeHandlerRegistry {

    private final Map<String, ProposalTypeHandler> handlersByTypeCode;

    public ProposalTypeHandlerRegistry(List<ProposalTypeHandler> handlers) {
        this.handlersByTypeCode = handlers.stream()
            .collect(Collectors.toUnmodifiableMap(ProposalTypeHandler::typeCode, Function.identity()));
    }

    public ProposalTypeHandler forProposal(ProposalEntity proposal) {
        String typeCode = proposal.getType() != null ? proposal.getType().getStringCode() : null;
        ProposalTypeHandler handler = typeCode != null ? handlersByTypeCode.get(typeCode) : null;
        if (handler == null) {
            throw new IllegalStateException(PROPOSALS_TYPE_NOT_FOUND);
        }
        return handler;
    }
}
