package com.bankcore.backend.dto;

import java.math.BigDecimal;

/**
 * Contrato inmutable de respuesta para información de Tarjetas bancarias.
 */
public record TarjetaResponseDto(
        Long id,
        String numeroTarjeta,
        String franquicia,
        String fechaVencimiento,
        BigDecimal cupoTotal,
        BigDecimal cupoDisponible,
        String estado,
        Long cuentaId
) {}
