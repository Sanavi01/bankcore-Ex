package com.bankcore.backend.domain.port;

import com.bankcore.backend.domain.model.Transaccion;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de Salida del Dominio para Transacciones Contables (DIP - Dependency Inversion Principle).
 * Contrato puro Java que representa el Libro Mayor Contable (General Ledger).
 */
public interface TransaccionRepositoryPort {

    Transaccion guardar(Transaccion transaccion);

    List<Transaccion> buscarPorCuentaId(Long cuentaId);

    Optional<Transaccion> buscarPorId(Long id);
}
