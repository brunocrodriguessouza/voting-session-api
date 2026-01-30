package br.com.bank.voting.adapters.in.web.dto;

import br.com.bank.voting.application.dto.result.VotingResultResult;

import java.time.LocalDateTime;

/**
 * DTO de resposta para resultado publicado na fila.
 */
public record PublishedResultResponse(
        VotingResultResult result,
        LocalDateTime publishedAt
) {
}

