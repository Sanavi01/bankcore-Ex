package com.bankcore.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Contrato inmutable de solicitud de transferencia con validación perimetral (Fail-Fast).
 */
public record TransferenciaRequestDto(
    @NotBlank(message = "El número de cuenta de origen es obligatorio")
    @Size(min = 8, max = 20, message = "La cuenta de origen debe tener entre 8 y 20 caracteres")
    String cuentaOrigen,

    @NotBlank(message = "El número de cuenta de destino es obligatorio")
    @Size(min = 8, max = 20, message = "La cuenta de destino debe tener entre 8 y 20 caracteres")
    String cuentaDestino,

    @NotNull(message = "El monto de la transferencia es obligatorio")
    @Positive(message = "El monto debe ser estrictamente mayor a cero")
    BigDecimal monto,

    @Size(max = 100, message = "El concepto no puede superar los 100 caracteres")
    String concepto
) {}
