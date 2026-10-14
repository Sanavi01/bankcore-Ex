package com.bankcore.backend.domain.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Evento de Dominio Inmutable (Domain Event).
 * Representa el hecho inmutable de que una transferencia bancaria fue ejecutada con éxito.
 * 
 * Cumple pureza de Clean Architecture (Cero dependencias de frameworks).
 */
public record TransaccionRealizadaEvent(
        String idTransaccion,
        String cuentaOrigen,
        String cuentaDestino,
        BigDecimal monto,
        String tipo,
        LocalDateTime fechaHora,
        String estado,
        String concepto
) {
}
