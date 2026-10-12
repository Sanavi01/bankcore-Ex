package com.bankcore.backend.infrastructure.persistence.repository;

import com.bankcore.backend.infrastructure.persistence.entity.RegistroIdempotenciaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio Spring Data JPA para la tabla 'registros_idempotencia'.
 * Permite buscar por la clave primaria (UUID) y persistir nuevas claves.
 */
@Repository
public interface SpringDataIdempotenciaRepository extends JpaRepository<RegistroIdempotenciaJpaEntity, String> {
}
