package com.bankcore.backend.service;

import com.bankcore.backend.domain.model.Cuenta;
import com.bankcore.backend.domain.model.Transaccion;
import com.bankcore.backend.domain.port.CuentaRepositoryPort;
import com.bankcore.backend.domain.port.TransaccionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio de Aplicación / Fachada para el Subdominio de Cuentas (GoF Façade Pattern).
 * Actúa como centro de control cohesivo del agregado Cuenta.
 * 
 * Responsabilidad:
 * - Proveer un punto de entrada unificado exclusivamente para operaciones sobre cuentas.
 */
@Service
@RequiredArgsConstructor
public class CuentaService {

    private final CuentaRepositoryPort cuentaRepositoryPort;
    private final TransaccionRepositoryPort transaccionRepositoryPort;

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

    @Transactional(readOnly = true)
    public List<Transaccion> obtenerMovimientosPorCuentaId(Long id) {
        obtenerPorId(id); // Valida que la cuenta exista
        return transaccionRepositoryPort.buscarPorCuentaId(id);
    }

    /**
     * Realiza un depósito o abono directo de fondos en la cuenta bancaria.
     */
    @Transactional
    public Cuenta depositar(Long id, BigDecimal monto, String concepto) {
        Cuenta cuenta = obtenerPorId(id);
        cuenta.acreditar(monto);
        Cuenta cuentaGuardada = cuentaRepositoryPort.guardar(cuenta);

        Transaccion movimiento = new Transaccion(
                null,
                cuenta.getId(),
                monto,
                com.bankcore.backend.domain.TipoMovimiento.DEPOSITO,
                concepto != null && !concepto.isBlank() ? concepto : "Depósito directo de fondos",
                java.time.LocalDateTime.now(),
                "EXITOSA"
        );
        transaccionRepositoryPort.guardar(movimiento);

        return cuentaGuardada;
    }
}
