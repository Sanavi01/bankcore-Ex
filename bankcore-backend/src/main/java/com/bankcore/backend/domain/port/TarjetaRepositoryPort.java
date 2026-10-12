package com.bankcore.backend.domain.port;

import com.bankcore.backend.domain.model.Tarjeta;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de Salida del Dominio para Tarjetas (DIP - Dependency Inversion Principle).
 * Contrato puro Java independiente de bases de datos o frameworks.
 */
public interface TarjetaRepositoryPort {

    Optional<Tarjeta> buscarPorId(Long id);

    List<Tarjeta> buscarPorCuentaId(Long cuentaId);

    Tarjeta guardar(Tarjeta tarjeta);
}
