package com.bankcore.backend.infrastructure.persistence.mapper;

import com.bankcore.backend.domain.model.Transaccion;
import com.bankcore.backend.infrastructure.persistence.entity.CuentaJpaEntity;
import com.bankcore.backend.infrastructure.persistence.entity.TransaccionJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper Bidireccional de Persistencia para Transacciones (Patrón GoF Data Mapper).
 * Traduce entre la Entidad de Dominio Puro (Transaccion) y la Entidad Relacional (TransaccionJpaEntity).
 */
@Component
public class TransaccionPersistenceMapper {

    public Transaccion toDomain(TransaccionJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        Long cuentaId = entity.getCuenta() != null ? entity.getCuenta().getId() : null;
        return new Transaccion(
                entity.getId(),
                cuentaId,
                entity.getMonto(),
                entity.getTipo(),
                entity.getConcepto(),
                entity.getFecha(),
                entity.getEstado()
        );
    }

    public TransaccionJpaEntity toEntity(Transaccion domain, CuentaJpaEntity cuentaEntity) {
        if (domain == null) {
            return null;
        }
        return TransaccionJpaEntity.builder()
                .id(domain.getId())
                .cuenta(cuentaEntity)
                .monto(domain.getMonto())
                .tipo(domain.getTipo())
                .concepto(domain.getConcepto())
                .fecha(domain.getFecha())
                .estado(domain.getEstado())
                .build();
    }
}
