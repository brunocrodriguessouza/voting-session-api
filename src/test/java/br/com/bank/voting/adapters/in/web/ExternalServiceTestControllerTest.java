package br.com.bank.voting.adapters.in.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ExternalServiceTestController Tests")
class ExternalServiceTestControllerTest {

    private ExternalServiceTestController controller;

    @BeforeEach
    void setUp() {
        controller = new ExternalServiceTestController();
    }

    @Test
    @DisplayName("Deve criar controller com sucesso")
    void shouldCreateControllerSuccessfully() {
        ExternalServiceTestController newController = new ExternalServiceTestController();
        assertNotNull(newController);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"1", "12", "123"})
    @DisplayName("Deve mascarar CPF corretamente quando CPF é null, vazio ou tem menos de 4 caracteres")
    void shouldMaskCpfCorrectlyWhenCpfIsNullOrEmptyOrHasLessThan4Characters(String cpf) throws Exception {
        Method maskCpfMethod = ExternalServiceTestController.class.getDeclaredMethod("maskCpf", String.class);
        maskCpfMethod.setAccessible(true);
        
        String result = (String) maskCpfMethod.invoke(controller, cpf);
        
        assertEquals("***", result);
    }

    @Test
    @DisplayName("Deve mascarar CPF corretamente quando CPF tem 4 ou mais caracteres")
    void shouldMaskCpfCorrectlyWhenCpfHas4OrMoreCharacters() throws Exception {
        Method maskCpfMethod = ExternalServiceTestController.class.getDeclaredMethod("maskCpf", String.class);
        maskCpfMethod.setAccessible(true);
        
        String result = (String) maskCpfMethod.invoke(controller, "12345678901");
        
        assertEquals("***8901", result);
        assertTrue(result.contains("***"));
        assertTrue(result.endsWith("8901"));
    }


    @Test
    @DisplayName("Deve mascarar CPF corretamente quando CPF tem exatamente 4 caracteres")
    void shouldMaskCpfCorrectlyWhenCpfHasExactly4Characters() throws Exception {
        Method maskCpfMethod = ExternalServiceTestController.class.getDeclaredMethod("maskCpf", String.class);
        maskCpfMethod.setAccessible(true);
        
        String result = (String) maskCpfMethod.invoke(controller, "1234");
        
        assertEquals("***1234", result);
    }
}

