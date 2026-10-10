package com.bankcore.backend.dto;

import java.math.BigDecimal;

/**
 * Contrato inmutable de solicitud de transferencia.
 * Sella los datos enviados por el cliente desde el momento de la petición HTTP.
 */
public record TransferenciaRequestDto(
    String cuentaOrigen,
    String cuentaDestino,
    BigDecimal monto,
    String concepto
) {}
