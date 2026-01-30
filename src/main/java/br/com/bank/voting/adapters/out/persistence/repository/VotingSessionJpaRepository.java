package br.com.bank.voting.adapters.out.persistence.repository;

import br.com.bank.voting.adapters.out.persistence.entity.VotingSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VotingSessionJpaRepository extends JpaRepository<VotingSessionEntity, UUID> {
    Optional<VotingSessionEntity> findByAgendaId(UUID agendaId);
}


