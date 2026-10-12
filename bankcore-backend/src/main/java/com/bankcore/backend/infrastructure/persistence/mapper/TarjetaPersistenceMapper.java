package com.bankcore.backend.infrastructure.persistence.mapper;

import com.bankcore.backend.domain.model.Tarjeta;
import com.bankcore.backend.infrastructure.persistence.entity.CuentaJpaEntity;
import com.bankcore.backend.infrastructure.persistence.entity.TarjetaJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper Bidireccional de Persistencia para Tarjetas (Patrón GoF Data Mapper).
 * Traduce entre el Modelo de Dominio Puro (Tarjeta) y la Entidad Relacional (TarjetaJpaEntity).
 */
@Component
public class TarjetaPersistenceMapper {

    public Tarjeta toDomain(TarjetaJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        Long cuentaId = entity.getCuenta() != null ? entity.getCuenta().getId() : null;
        return new Tarjeta(
                entity.getId(),
                entity.getNumeroTarjeta(),
                entity.getFranquicia(),
                entity.getFechaVencimiento(),
                entity.getCvv(),
                entity.getCupoTotal(),
                entity.getCupoDisponible(),
                entity.getEstado(),
                cuentaId
        );
    }

    public TarjetaJpaEntity toEntity(Tarjeta domain, CuentaJpaEntity cuentaEntity) {
        if (domain == null) {
            return null;
        }
        return TarjetaJpaEntity.builder()
                .id(domain.getId())
                .numeroTarjeta(domain.getNumeroTarjeta())
                .franquicia(domain.getFranquicia())
                .fechaVencimiento(domain.getFechaVencimiento())
                .cvv(domain.getCvv())
                .cupoTotal(domain.getCupoTotal())
                .cupoDisponible(domain.getCupoDisponible())
                .estado(domain.getEstado())
                .cuenta(cuentaEntity)
                .build();
    }
}
