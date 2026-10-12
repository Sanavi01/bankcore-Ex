package com.bankcore.backend.infrastructure.persistence.repository;

import com.bankcore.backend.infrastructure.persistence.entity.CuentaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA de bajo nivel (Detalle de Infraestructura).
 * Maneja directamente el acceso a la tabla 'cuentas' en PostgreSQL.
 */
@Repository
public interface SpringDataCuentaRepository extends JpaRepository<CuentaJpaEntity, Long> {

    Optional<CuentaJpaEntity> findByNumeroCuenta(String numeroCuenta);
}
