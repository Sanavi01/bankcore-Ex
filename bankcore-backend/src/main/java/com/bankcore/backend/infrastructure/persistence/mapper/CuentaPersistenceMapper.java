package com.bankcore.backend.infrastructure.persistence.mapper;

import com.bankcore.backend.domain.model.Cuenta;
import com.bankcore.backend.infrastructure.persistence.entity.CuentaJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper Bidireccional de Persistencia (SRP - Single Responsibility Principle).
 * Responsabilidad Única: Traducir entre la Entidad de Dominio Puro (Cuenta)
 * y la Entidad de Persistencia Relacional (CuentaJpaEntity).
 */
@Component
public class CuentaPersistenceMapper {

    // 1. De la Base de Datos (PostgreSQL) hacia el Dominio Puro
    public Cuenta toDomain(CuentaJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Cuenta(
            entity.getId(),
            entity.getNumeroCuenta(),
            entity.getTitular(),
            entity.getSaldo(),
            entity.getEstado(),
            entity.getFechaCreacion()
        );
    }

    // 2. Del Dominio Puro hacia la Base de Datos (PostgreSQL)
    public CuentaJpaEntity toEntity(Cuenta domain) {
        if (domain == null) {
            return null;
        }
        return CuentaJpaEntity.builder()
            .id(domain.getId())
            .numeroCuenta(domain.getNumeroCuenta())
            .titular(domain.getTitular())
            .saldo(domain.getSaldo())
            .estado(domain.getEstado())
            .fechaCreacion(domain.getFechaCreacion())
            .build();
    }
}
