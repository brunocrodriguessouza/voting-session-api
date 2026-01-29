package br.com.bank.voting.adapters.out.persistence.adapter;

import br.com.bank.voting.adapters.out.persistence.entity.AgendaEntity;
import br.com.bank.voting.adapters.out.persistence.repository.AgendaJpaRepository;
import br.com.bank.voting.application.port.out.AgendaRepositoryPort;
import br.com.bank.voting.domain.model.Agenda;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AgendaJpaAdapter implements AgendaRepositoryPort {

    private final AgendaJpaRepository repository;

    public AgendaJpaAdapter(AgendaJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Agenda save(Agenda agenda) {
        AgendaEntity entity = toEntity(agenda);
        AgendaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Agenda> findById(UUID id) {
        return repository.findById(id)
                .map(this::toDomain);
    }

    private AgendaEntity toEntity(Agenda agenda) {
        AgendaEntity entity = new AgendaEntity();
        // Se o ID for null, deixa o JPA gerar automaticamente
        if (agenda.getId() != null) {
            entity.setId(agenda.getId());
        }
        entity.setTitle(agenda.getTitle());
        entity.setCreatedAt(agenda.getCreatedAt());
        return entity;
    }

    private Agenda toDomain(AgendaEntity entity) {
        return new Agenda(
                entity.getId(),
                entity.getTitle(),
                entity.getCreatedAt()
        );
    }
}

