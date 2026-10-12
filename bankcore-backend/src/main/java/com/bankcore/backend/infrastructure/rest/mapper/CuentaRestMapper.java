package com.bankcore.backend.infrastructure.rest.mapper;

import com.bankcore.backend.domain.model.Cuenta;
import com.bankcore.backend.dto.CuentaResponseDto;
import org.springframework.stereotype.Component;

/**
 * Mapper de Presentación (SRP).
 * Traduce del Dominio Puro hacia el DTO que viaja por HTTP hacia Angular.
 */
@Component
public class CuentaRestMapper {

    public CuentaResponseDto toResponseDto(Cuenta cuenta) {
        if (cuenta == null) return null;
        return new CuentaResponseDto(
                cuenta.getId(),
                cuenta.getNumeroCuenta(),
                cuenta.getTitular(),
                cuenta.getSaldo(),
                cuenta.getEstado()
        );
    }
}
