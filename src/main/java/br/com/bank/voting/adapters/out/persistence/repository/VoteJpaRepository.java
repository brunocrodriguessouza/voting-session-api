package br.com.bank.voting.adapters.out.persistence.repository;

import br.com.bank.voting.adapters.out.persistence.entity.VoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VoteJpaRepository extends JpaRepository<VoteEntity, UUID> {
    Optional<VoteEntity> findByAgendaIdAndCpf(UUID agendaId, String cpf);
    List<VoteEntity> findAllByAgendaId(UUID agendaId);
}

