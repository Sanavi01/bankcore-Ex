package com.bankcore.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Comprobante inmutable emitido tras procesar la transferencia bancaria.
 */
public record TransferenciaResponseDto(
    String idTransaccion,
    String cuentaOrigen,
    String cuentaDestino,
    BigDecimal monto,
    String estado,
    LocalDateTime fechaHora,
    String mensaje
) {}
