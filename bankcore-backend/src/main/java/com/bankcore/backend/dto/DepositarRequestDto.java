package com.bankcore.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Contrato inmutable (Record) para operaciones de depósito / abono directo a cuenta.
 */
@Schema(description = "Solicitud de depósito en efectivo o abono directo a cuenta")
public record DepositarRequestDto(
    @Schema(description = "Monto a depositar", example = "500.00")
    @NotNull(message = "El monto a depositar es obligatorio")
    @Positive(message = "El monto debe ser estrictamente mayor a cero")
    BigDecimal monto,

    @Schema(description = "Concepto o motivo del depósito", example = "Depósito en ventanilla sucursal principal")
    @NotBlank(message = "El concepto es obligatorio")
    @Size(max = 100, message = "El concepto no puede superar los 100 caracteres")
    String concepto
) {}
