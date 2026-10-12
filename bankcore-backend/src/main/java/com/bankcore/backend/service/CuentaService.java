package com.bankcore.backend.service;

import com.bankcore.backend.domain.model.Cuenta;
import com.bankcore.backend.domain.port.CuentaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

/**
 * Caso de Uso / Servicio de Aplicación (Clean Architecture).
 * Orquesta las transacciones bancarias sobre cuentas.
 * Solo conoce el Puerto del Dominio (DIP) y el Modelo Puro (Cuenta).
 */
@Service
@RequiredArgsConstructor
public class CuentaService {

    private final CuentaRepositoryPort cuentaRepositoryPort;

    @Transactional(readOnly = true)
    public Cuenta obtenerPorId(Long id) {
        return cuentaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada con ID: " + id));
    }

    @Transactional(readOnly = true)
    public Cuenta obtenerPorNumeroCuenta(String numeroCuenta) {
        return cuentaRepositoryPort.buscarPorNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada con número: " + numeroCuenta));
    }

    @Transactional
    public Cuenta crearCuenta(String numeroCuenta, String titular, BigDecimal saldoInicial) {
        cuentaRepositoryPort.buscarPorNumeroCuenta(numeroCuenta).ifPresent(c -> {
            throw new IllegalStateException("Ya existe una cuenta con el número: " + numeroCuenta);
        });

        Cuenta nuevaCuenta = new Cuenta(
                null,
                numeroCuenta,
                titular,
                saldoInicial,
                null,
                null
        );

        return cuentaRepositoryPort.guardar(nuevaCuenta);
    }
}
