package com.bankcore.backend.infrastructure.persistence.repository;

import com.bankcore.backend.infrastructure.persistence.entity.RegistroAuditoriaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la tabla 'registros_auditoria'.
 */
@Repository
public interface SpringDataRegistroAuditoriaRepository extends JpaRepository<RegistroAuditoriaJpaEntity, Long> {

    Optional<RegistroAuditoriaJpaEntity> findByIdTransaccion(String idTransaccion);

    List<RegistroAuditoriaJpaEntity> findByClasificacionRiesgo(String clasificacionRiesgo);
}
