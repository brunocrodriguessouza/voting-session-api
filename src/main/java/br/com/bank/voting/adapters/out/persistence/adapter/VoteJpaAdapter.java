package br.com.bank.voting.adapters.out.persistence.adapter;

import br.com.bank.voting.adapters.out.persistence.entity.VoteEntity;
import br.com.bank.voting.adapters.out.persistence.repository.VoteJpaRepository;
import br.com.bank.voting.application.port.out.VoteRepositoryPort;
import br.com.bank.voting.domain.model.Vote;
import br.com.bank.voting.domain.model.enums.VoteChoice;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class VoteJpaAdapter implements VoteRepositoryPort {

    private final VoteJpaRepository repository;

    public VoteJpaAdapter(VoteJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Vote save(Vote vote) {
        VoteEntity entity = toEntity(vote);
        VoteEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Vote> findByAgendaIdAndCpf(UUID agendaId, String cpf) {
        return repository.findByAgendaIdAndCpf(agendaId, cpf)
                .map(this::toDomain);
    }

    @Override
    public List<Vote> findAllByAgendaId(UUID agendaId) {
        return repository.findAllByAgendaId(agendaId).stream()
                .map(this::toDomain)
                .toList();
    }

    private VoteEntity toEntity(Vote vote) {
        VoteEntity entity = new VoteEntity();
        if (vote.getId() != null) {
            entity.setId(vote.getId());
        }
        entity.setAgendaId(vote.getAgendaId());
        entity.setCpf(vote.getCpf());
        entity.setChoice(vote.getChoice().name());
        entity.setCreatedAt(vote.getCreatedAt());
        return entity;
    }

    private Vote toDomain(VoteEntity entity) {
        return new Vote(
                entity.getId(),
                entity.getAgendaId(),
                entity.getCpf(),
                VoteChoice.valueOf(entity.getChoice()),
                entity.getCreatedAt()
        );
    }
}

