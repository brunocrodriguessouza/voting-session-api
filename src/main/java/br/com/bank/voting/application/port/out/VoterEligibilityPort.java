package br.com.bank.voting.application.port.out;

public interface VoterEligibilityPort {
    boolean isEligibleToVote(String cpf);
}

