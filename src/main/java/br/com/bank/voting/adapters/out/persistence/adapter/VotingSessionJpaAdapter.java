package br.com.bank.voting.adapters.out.persistence.adapter;

import br.com.bank.voting.adapters.out.persistence.entity.VotingSessionEntity;
import br.com.bank.voting.adapters.out.persistence.repository.VotingSessionJpaRepository;
import br.com.bank.voting.application.port.out.SessionRepositoryPort;
import br.com.bank.voting.domain.model.VotingSession;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class VotingSessionJpaAdapter implements SessionRepositoryPort {

    private final VotingSessionJpaRepository repository;

    public VotingSessionJpaAdapter(VotingSessionJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public VotingSession save(VotingSession session) {
        VotingSessionEntity entity = toEntity(session);
        VotingSessionEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<VotingSession> findByAgendaId(UUID agendaId) {
        return repository.findByAgendaId(agendaId)
                .map(this::toDomain);
    }

    private VotingSessionEntity toEntity(VotingSession session) {
        VotingSessionEntity entity = new VotingSessionEntity();
        // Se o ID for null, deixa o JPA gerar automaticamente
        if (session.getId() != null) {
            entity.setId(session.getId());
        }
        entity.setAgendaId(session.getAgendaId());
        entity.setOpenedAt(session.getOpenedAt());
        entity.setClosesAt(session.getClosesAt());
        return entity;
    }

    private VotingSession toDomain(VotingSessionEntity entity) {
        return new VotingSession(
                entity.getId(),
                entity.getAgendaId(),
                entity.getOpenedAt(),
                entity.getClosesAt()
        );
    }
}

