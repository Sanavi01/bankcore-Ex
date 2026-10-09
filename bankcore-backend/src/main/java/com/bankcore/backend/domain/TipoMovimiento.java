package com.bankcore.backend.domain;

/**
 * Tipos de movimientos contables soportados en BankCore.
 * Modela las operaciones transaccionales permitidas en el libro mayor.
 */
public enum TipoMovimiento {
    DEPOSITO,
    RETIRO,
    TRANSFERENCIA_ENTRANTE,
    TRANSFERENCIA_SALIENTE
}
