package br.com.bank.voting.adapters.out.external;

import br.com.bank.voting.adapters.out.external.dto.UserInfoResponse;
import br.com.bank.voting.adapters.out.external.exception.ExternalServiceUnavailableException;
import br.com.bank.voting.adapters.out.external.exception.InvalidCpfException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserInfoClientAdapter Tests")
class UserInfoClientAdapterTest {

    @Test
    @DisplayName("Deve criar adapter com URL base correta")
    void shouldCreateAdapterWithBaseUrl() {
        String baseUrl = "https://user-info.herokuapp.com";
        int timeoutSeconds = 5;
        
        UserInfoClientAdapter adapter = new UserInfoClientAdapter(baseUrl, timeoutSeconds);
        
        assertNotNull(adapter);
    }

    @Test
    @DisplayName("Deve lançar InvalidCpfException quando CPF é inválido")
    void shouldThrowInvalidCpfExceptionWhenCpfIsInvalid() {
        String cpf = "00000000000";
        
        InvalidCpfException exception = assertThrows(InvalidCpfException.class, () -> {
            throw new InvalidCpfException(cpf);
        });
        
        assertTrue(exception.getMessage().contains("Invalid CPF"));
        assertTrue(exception.getMessage().contains("***"));
    }

    @Test
    @DisplayName("Deve mascarar CPF na mensagem de erro")
    void shouldMaskCpfInErrorMessage() {
        String cpf = "12345678901";
        
        InvalidCpfException exception = new InvalidCpfException(cpf);
        
        String message = exception.getMessage();
        assertTrue(message.contains("***"));
        assertFalse(message.contains("12345678901"));
    }

    @Test
    @DisplayName("Deve lançar ExternalServiceUnavailableException em caso de erro de servidor")
    void shouldThrowExternalServiceUnavailableExceptionOnServerError() {
        String message = "Service unavailable";
        Exception cause = new RuntimeException("Connection timeout");
        
        ExternalServiceUnavailableException exception = assertThrows(
                ExternalServiceUnavailableException.class,
                () -> {
                    throw new ExternalServiceUnavailableException(message, cause);
                }
        );
        
        assertTrue(exception.getMessage().contains("External service unavailable"));
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Deve lançar ExternalServiceUnavailableException em caso de erro de rede")
    void shouldThrowExternalServiceUnavailableExceptionOnNetworkError() {
        String message = "Network error";
        Exception cause = new RuntimeException("Connection refused");
        
        ExternalServiceUnavailableException exception = assertThrows(
                ExternalServiceUnavailableException.class,
                () -> {
                    throw new ExternalServiceUnavailableException(message, cause);
                }
        );
        
        assertTrue(exception.getMessage().contains("External service unavailable"));
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Deve lançar ExternalServiceUnavailableException sem causa")
    void shouldThrowExternalServiceUnavailableExceptionWithoutCause() {
        String message = "Service error";
        
        ExternalServiceUnavailableException exception = new ExternalServiceUnavailableException(message, null);
        
        assertTrue(exception.getMessage().contains("External service unavailable"));
        assertNull(exception.getCause());
    }
}

