package com.bankcore.backend.domain.port;

import com.bankcore.backend.domain.model.Cuenta;
import java.util.Optional;

/**
 * Puerto de Salida del Dominio (DIP - Dependency Inversion Principle).
 * Contrato puro Java que declara las operaciones requeridas por el negocio,
 * trabajando EXCLUSIVAMENTE con la entidad de dominio puro 'Cuenta'.
 */
public interface CuentaRepositoryPort {

    Optional<Cuenta> buscarPorId(Long id);

    Optional<Cuenta> buscarPorNumeroCuenta(String numeroCuenta);

    Cuenta guardar(Cuenta cuenta);
}
