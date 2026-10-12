package com.bankcore.backend.infrastructure.rest.mapper;

import com.bankcore.backend.domain.model.Transaccion;
import com.bankcore.backend.dto.MovimientoResponseDto;
import org.springframework.stereotype.Component;

/**
 * Mapper de Presentación REST para Movimientos Contables (Patrón GoF Data Mapper).
 */
@Component
public class MovimientoRestMapper {

    public MovimientoResponseDto toResponseDto(Transaccion transaccion) {
        if (transaccion == null) {
            return null;
        }

        return new MovimientoResponseDto(
                transaccion.getId(),
                transaccion.getMonto(),
                transaccion.getTipo() != null ? transaccion.getTipo().name() : null,
                transaccion.getConcepto(),
                transaccion.getFecha(),
                transaccion.getEstado()
        );
    }
}
