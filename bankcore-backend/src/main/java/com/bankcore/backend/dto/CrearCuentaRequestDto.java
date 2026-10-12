package com.bankcore.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Contrato de entrada (Fail-Fast) para la apertura de una nueva cuenta bancaria.
 */
public record CrearCuentaRequestDto(
    @NotBlank(message = "El número de cuenta es obligatorio")
    @Size(min = 8, max = 20, message = "El número de cuenta debe tener entre 8 y 20 caracteres")
    String numeroCuenta,

    @NotBlank(message = "El titular es obligatorio")
    String titular,

    @NotNull(message = "El saldo inicial es obligatorio")
    @PositiveOrZero(message = "El saldo inicial no puede ser negativo")
    BigDecimal saldoInicial
) {}
