package com.bankcore.backend.dto;

import com.bankcore.backend.domain.EstadoCuenta;
import java.math.BigDecimal;

/**
 * Vista pública inmutable de una cuenta bancaria para el frontend.
 * Utiliza el enum domain EstadoCuenta garantizando type safety.
 */
public record CuentaResponseDto(
    Long id,
    String numeroCuenta,
    String titular,
    BigDecimal saldo,
    EstadoCuenta estado
) {}
