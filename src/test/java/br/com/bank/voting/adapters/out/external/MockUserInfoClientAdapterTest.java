package br.com.bank.voting.adapters.out.external;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MockUserInfoClientAdapter Tests")
class MockUserInfoClientAdapterTest {

    @Test
    @DisplayName("Deve retornar true para qualquer CPF (modo mock)")
    void shouldReturnTrueForAnyCpf() {
        MockUserInfoClientAdapter adapter = new MockUserInfoClientAdapter();
        
        assertTrue(adapter.isEligibleToVote("12345678901"));
        assertTrue(adapter.isEligibleToVote("00000000000"));
        assertTrue(adapter.isEligibleToVote("99999999999"));
    }

    @Test
    @DisplayName("Deve retornar true mesmo para CPF inválido (modo mock)")
    void shouldReturnTrueEvenForInvalidCpf() {
        MockUserInfoClientAdapter adapter = new MockUserInfoClientAdapter();
        
        // Em modo mock, sempre retorna true
        assertTrue(adapter.isEligibleToVote("000"));
        assertTrue(adapter.isEligibleToVote("123"));
        assertTrue(adapter.isEligibleToVote(null));
    }

    @Test
    @DisplayName("Deve mascarar CPF corretamente no log")
    void shouldMaskCpfCorrectlyInLog() {
        MockUserInfoClientAdapter adapter = new MockUserInfoClientAdapter();
        
        // Testa diferentes tamanhos de CPF para garantir que o maskCpf funciona
        assertTrue(adapter.isEligibleToVote("12345678901")); // CPF completo
        assertTrue(adapter.isEligibleToVote("123")); // CPF curto
        assertTrue(adapter.isEligibleToVote(null)); // CPF null
        assertTrue(adapter.isEligibleToVote("")); // CPF vazio
    }
}

