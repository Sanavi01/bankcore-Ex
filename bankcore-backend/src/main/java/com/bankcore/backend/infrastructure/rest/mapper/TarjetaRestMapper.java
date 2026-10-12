package com.bankcore.backend.infrastructure.rest.mapper;

import com.bankcore.backend.domain.model.Tarjeta;
import com.bankcore.backend.dto.TarjetaResponseDto;
import org.springframework.stereotype.Component;

/**
 * Mapper de Presentación REST para Tarjetas (Patrón GoF Data Mapper).
 * Traduce del Modelo de Dominio Puro (Tarjeta) hacia el DTO público para Angular.
 */
@Component
public class TarjetaRestMapper {

    public TarjetaResponseDto toResponseDto(Tarjeta tarjeta) {
        if (tarjeta == null) {
            return null;
        }

        // Enmascaramiento defensivo de seguridad bancaria (PCI-DSS)
        String numero = tarjeta.getNumeroTarjeta();
        String enmascarado = (numero != null && numero.length() >= 4)
                ? "**** **** **** " + numero.substring(numero.length() - 4)
                : numero;

        return new TarjetaResponseDto(
                tarjeta.getId(),
                enmascarado,
                tarjeta.getFranquicia(),
                tarjeta.getFechaVencimiento(),
                tarjeta.getCupoTotal(),
                tarjeta.getCupoDisponible(),
                tarjeta.getEstado().name(),
                tarjeta.getCuentaId()
        );
    }
}
