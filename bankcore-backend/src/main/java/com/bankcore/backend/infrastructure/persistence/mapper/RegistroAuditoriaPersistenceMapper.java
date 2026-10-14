package com.bankcore.backend.infrastructure.persistence.mapper;

import com.bankcore.backend.domain.model.RegistroAuditoria;
import com.bankcore.backend.infrastructure.persistence.entity.RegistroAuditoriaJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper Bidireccional de Persistencia para Auditoría Forense (Patrón GoF Data Mapper).
 * Traduce entre la Entidad de Dominio Puro (RegistroAuditoria) y la Entidad JPA (RegistroAuditoriaJpaEntity).
 */
@Component
public class RegistroAuditoriaPersistenceMapper {

    public RegistroAuditoria toDomain(RegistroAuditoriaJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new RegistroAuditoria(
                entity.getId(),
                entity.getIdTransaccion(),
                entity.getCuentaOrigen(),
                entity.getCuentaDestino(),
                entity.getMonto(),
                entity.getClasificacionRiesgo(),
                entity.getFechaEvento(),
                entity.getFechaAuditoria(),
                entity.getCanal()
        );
    }

    public RegistroAuditoriaJpaEntity toEntity(RegistroAuditoria domain) {
        if (domain == null) {
            return null;
        }
        return RegistroAuditoriaJpaEntity.builder()
                .id(domain.getId())
                .idTransaccion(domain.getIdTransaccion())
                .cuentaOrigen(domain.getCuentaOrigen())
                .cuentaDestino(domain.getCuentaDestino())
                .monto(domain.getMonto())
                .clasificacionRiesgo(domain.getClasificacionRiesgo())
                .fechaEvento(domain.getFechaEvento())
                .fechaAuditoria(domain.getFechaAuditoria())
                .canal(domain.getCanal())
                .build();
    }
}
