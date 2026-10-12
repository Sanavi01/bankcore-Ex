package com.bankcore.backend.infrastructure.persistence.repository;

import com.bankcore.backend.infrastructure.persistence.entity.TarjetaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para la tabla 'tarjetas' en PostgreSQL.
 */
@Repository
public interface SpringDataTarjetaRepository extends JpaRepository<TarjetaJpaEntity, Long> {

    List<TarjetaJpaEntity> findByCuentaId(Long cuentaId);
}
