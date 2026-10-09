package com.bankcore.backend.domain;

/**
 * Estados del ciclo de vida de una cuenta bancaria.
 * Regla de negocio: Una cuenta BLOQUEADA o CANCELADA no puede emitir transferencias.
 * 
 * Cumple con Type Safety: Previene cadenas mágicas (Magic Strings) o números arbitrarios.
 */
public enum EstadoCuenta {
    ACTIVA,
    BLOQUEADA,
    CANCELADA
}
