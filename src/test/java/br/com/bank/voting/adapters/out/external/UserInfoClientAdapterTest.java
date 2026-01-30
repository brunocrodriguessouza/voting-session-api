package br.com.bank.voting.adapters.out.external;

import br.com.bank.voting.adapters.out.external.exception.ExternalServiceUnavailableException;
import br.com.bank.voting.adapters.out.external.exception.InvalidCpfException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserInfoClientAdapter Tests")
class UserInfoClientAdapterTest {

    private UserInfoClientAdapter adapter;
    private String baseUrl;
    private String cpf;

    @BeforeEach
    void setUp() {
        baseUrl = "https://user-info.herokuapp.com";
        cpf = "12345678901";
        
        adapter = new UserInfoClientAdapter(baseUrl);
    }

    @Test
    @DisplayName("Deve criar adapter com URL base correta")
    void shouldCreateAdapterWithBaseUrl() {
        assertNotNull(adapter);
    }

    @Test
    @DisplayName("Deve lançar InvalidCpfException quando CPF é inválido")
    void shouldThrowInvalidCpfExceptionWhenCpfIsInvalid() {
        InvalidCpfException exception = assertThrows(InvalidCpfException.class, () -> {
            throw new InvalidCpfException(cpf);
        });
        
        assertTrue(exception.getMessage().contains("Invalid CPF"));
        assertTrue(exception.getMessage().contains("***"));
    }

    @Test
    @DisplayName("Deve mascarar CPF na mensagem de erro")
    void shouldMaskCpfInErrorMessage() {
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

    @Test
    @DisplayName("Deve mascarar CPF corretamente quando CPF é null")
    void shouldMaskCpfCorrectlyWhenCpfIsNull() throws Exception {
        Method maskCpfMethod = UserInfoClientAdapter.class.getDeclaredMethod("maskCpf", String.class);
        maskCpfMethod.setAccessible(true);
        
        String result = (String) maskCpfMethod.invoke(adapter, (String) null);
        
        assertEquals("***", result);
    }

    @Test
    @DisplayName("Deve mascarar CPF corretamente quando CPF tem menos de 4 caracteres")
    void shouldMaskCpfCorrectlyWhenCpfHasLessThan4Characters() throws Exception {
        Method maskCpfMethod = UserInfoClientAdapter.class.getDeclaredMethod("maskCpf", String.class);
        maskCpfMethod.setAccessible(true);
        
        String result = (String) maskCpfMethod.invoke(adapter, "123");
        
        assertEquals("***", result);
    }

    @Test
    @DisplayName("Deve mascarar CPF corretamente quando CPF tem 4 ou mais caracteres")
    void shouldMaskCpfCorrectlyWhenCpfHas4OrMoreCharacters() throws Exception {
        Method maskCpfMethod = UserInfoClientAdapter.class.getDeclaredMethod("maskCpf", String.class);
        maskCpfMethod.setAccessible(true);
        
        String result = (String) maskCpfMethod.invoke(adapter, "12345678901");
        
        assertEquals("***8901", result);
        assertTrue(result.contains("***"));
        assertTrue(result.endsWith("8901"));
    }

    @Test
    @DisplayName("Deve mascarar CPF corretamente quando CPF tem exatamente 4 caracteres")
    void shouldMaskCpfCorrectlyWhenCpfHasExactly4Characters() throws Exception {
        Method maskCpfMethod = UserInfoClientAdapter.class.getDeclaredMethod("maskCpf", String.class);
        maskCpfMethod.setAccessible(true);
        
        String result = (String) maskCpfMethod.invoke(adapter, "1234");
        
        assertEquals("***1234", result);
    }

    @Test
    @DisplayName("Deve mascarar CPF corretamente quando CPF está vazio")
    void shouldMaskCpfCorrectlyWhenCpfIsEmpty() throws Exception {
        Method maskCpfMethod = UserInfoClientAdapter.class.getDeclaredMethod("maskCpf", String.class);
        maskCpfMethod.setAccessible(true);
        
        String result = (String) maskCpfMethod.invoke(adapter, "");
        
        assertEquals("***", result);
    }
}

