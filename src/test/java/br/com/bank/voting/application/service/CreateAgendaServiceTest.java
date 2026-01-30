package br.com.bank.voting.application.service;

import br.com.bank.voting.application.dto.command.CreateAgendaCommand;
import br.com.bank.voting.application.dto.result.AgendaCreatedResult;
import br.com.bank.voting.application.port.out.AgendaRepositoryPort;
import br.com.bank.voting.domain.model.Agenda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateAgendaService Tests")
class CreateAgendaServiceTest {

    @Mock
    private AgendaRepositoryPort agendaRepository;

    @InjectMocks
    private CreateAgendaService createAgendaService;

    private CreateAgendaCommand command;
    private Agenda savedAgenda;

    @BeforeEach
    void setUp() {
        UUID agendaId = UUID.randomUUID();
        String title = "Pauta sobre orçamento anual";
        LocalDateTime createdAt = LocalDateTime.now();

        command = new CreateAgendaCommand(title);
        savedAgenda = new Agenda(agendaId, title, createdAt);
    }

    @Test
    @DisplayName("Deve criar uma pauta com sucesso")
    void shouldCreateAgendaSuccessfully() {
        when(agendaRepository.save(any(Agenda.class))).thenReturn(savedAgenda);

        AgendaCreatedResult result = createAgendaService.create(command);

        assertNotNull(result);
        assertEquals(savedAgenda.getId(), result.id());
        assertEquals(savedAgenda.getTitle(), result.title());
        assertEquals(savedAgenda.getCreatedAt(), result.createdAt());
        verify(agendaRepository, times(1)).save(any(Agenda.class));
    }

    @Test
    @DisplayName("Deve criar pauta com título válido")
    void shouldCreateAgendaWithValidTitle() {
        when(agendaRepository.save(any(Agenda.class))).thenReturn(savedAgenda);

        AgendaCreatedResult result = createAgendaService.create(command);

        assertNotNull(result.title());
        assertFalse(result.title().isBlank());
        assertEquals(command.title(), result.title());
    }

    @Test
    @DisplayName("Deve gerar ID ao criar pauta")
    void shouldGenerateIdWhenCreatingAgenda() {
        when(agendaRepository.save(any(Agenda.class))).thenReturn(savedAgenda);

        AgendaCreatedResult result = createAgendaService.create(command);

        assertNotNull(result.id());
        verify(agendaRepository).save(argThat(agenda -> agenda.getId() == null));
    }

    @Test
    @DisplayName("Deve definir createdAt ao criar pauta")
    void shouldSetCreatedAtWhenCreatingAgenda() {
        when(agendaRepository.save(any(Agenda.class))).thenReturn(savedAgenda);

        AgendaCreatedResult result = createAgendaService.create(command);

        assertNotNull(result.createdAt());
        verify(agendaRepository).save(argThat(agenda -> agenda.getCreatedAt() != null));
    }

    @Test
    @DisplayName("Deve criar pauta com diferentes títulos")
    void shouldCreateAgendaWithDifferentTitles() {
        CreateAgendaCommand command1 = new CreateAgendaCommand("Pauta 1");
        CreateAgendaCommand command2 = new CreateAgendaCommand("Pauta 2");
        
        Agenda agenda1 = new Agenda(UUID.randomUUID(), "Pauta 1", LocalDateTime.now());
        Agenda agenda2 = new Agenda(UUID.randomUUID(), "Pauta 2", LocalDateTime.now());
        
        when(agendaRepository.save(any(Agenda.class)))
                .thenReturn(agenda1)
                .thenReturn(agenda2);

        AgendaCreatedResult result1 = createAgendaService.create(command1);
        AgendaCreatedResult result2 = createAgendaService.create(command2);

        assertEquals("Pauta 1", result1.title());
        assertEquals("Pauta 2", result2.title());
        verify(agendaRepository, times(2)).save(any(Agenda.class));
    }
}


