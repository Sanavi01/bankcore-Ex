package com.bankcore.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Contrato inmutable para representar un movimiento contable en la interfaz de Angular.
 */
public record MovimientoResponseDto(
        Long id,
        BigDecimal monto,
        String tipo,
        String concepto,
        LocalDateTime fecha,
        String estado
) {}
