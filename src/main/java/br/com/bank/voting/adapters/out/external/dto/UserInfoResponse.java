package br.com.bank.voting.adapters.out.external.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de resposta da API externa de verificação de elegibilidade do eleitor.
 */
public record UserInfoResponse(
        @JsonProperty("status")
        String status
) {
    public static final String ABLE_TO_VOTE = "ABLE_TO_VOTE";
    public static final String UNABLE_TO_VOTE = "UNABLE_TO_VOTE";
}


