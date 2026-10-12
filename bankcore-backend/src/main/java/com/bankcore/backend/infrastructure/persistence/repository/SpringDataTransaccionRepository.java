package com.bankcore.backend.infrastructure.persistence.repository;

import com.bankcore.backend.infrastructure.persistence.entity.TransaccionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para la tabla 'transacciones' en PostgreSQL.
 */
@Repository
public interface SpringDataTransaccionRepository extends JpaRepository<TransaccionJpaEntity, Long> {

    List<TransaccionJpaEntity> findByCuentaIdOrderByFechaDesc(Long cuentaId);
}
