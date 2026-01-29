package br.com.bank.voting.adapters.out.persistence.repository;

import br.com.bank.voting.adapters.out.persistence.entity.AgendaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AgendaJpaRepository extends JpaRepository<AgendaEntity, UUID> {
}

