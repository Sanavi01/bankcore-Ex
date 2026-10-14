package com.bankcore.backend.config;

import com.bankcore.backend.domain.EstadoCuenta;
import com.bankcore.backend.domain.TipoMovimiento;
import com.bankcore.backend.domain.model.Cuenta;
import com.bankcore.backend.domain.model.Tarjeta;
import com.bankcore.backend.domain.model.Transaccion;
import com.bankcore.backend.domain.port.CuentaRepositoryPort;
import com.bankcore.backend.domain.port.TarjetaRepositoryPort;
import com.bankcore.backend.domain.port.TransaccionRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Inicializador de datos semilla (Seed Data) para desarrollo local y demostración fullstack.
 * Crea las cuentas y tarjetas iniciales si la base de datos en memoria está vacía.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CuentaRepositoryPort cuentaRepositoryPort;
    private final TarjetaRepositoryPort tarjetaRepositoryPort;
    private final TransaccionRepositoryPort transaccionRepositoryPort;

    @Override
    public void run(String... args) {
        if (cuentaRepositoryPort.buscarPorNumeroCuenta("001-ORIGEN").isPresent()) {
            return;
        }

        log.info("Sembrando datos iniciales en H2 para BankCore Digital...");

        Cuenta cuentaOrigen = cuentaRepositoryPort.guardar(
                new Cuenta(null, "001-ORIGEN", "CARLOS MENDOZA", new BigDecimal("19850.50"), EstadoCuenta.ACTIVA, LocalDateTime.now())
        );

        cuentaRepositoryPort.guardar(
                new Cuenta(null, "002-DESTINO", "EMPRESA DE SERVICIOS", new BigDecimal("5000.00"), EstadoCuenta.ACTIVA, LocalDateTime.now())
        );

        tarjetaRepositoryPort.guardar(
                new Tarjeta(null, "4532111122224589", "VISA", "12/28", "123", new BigDecimal("10000.00"), new BigDecimal("6850.00"), EstadoCuenta.ACTIVA, cuentaOrigen.getId())
        );

        tarjetaRepositoryPort.guardar(
                new Tarjeta(null, "5412333344449921", "MASTERCARD", "10/29", "456", new BigDecimal("15000.00"), new BigDecimal("14200.00"), EstadoCuenta.ACTIVA, cuentaOrigen.getId())
        );

        transaccionRepositoryPort.guardar(
                new Transaccion(null, cuentaOrigen.getId(), new BigDecimal("3500.00"), TipoMovimiento.DEPOSITO, "Depósito de Nómina Quincenal", LocalDateTime.now().minusDays(2), "EXITOSA")
        );

        transaccionRepositoryPort.guardar(
                new Transaccion(null, cuentaOrigen.getId(), new BigDecimal("120.50"), TipoMovimiento.RETIRO, "Pago de Servicio Eléctrico", LocalDateTime.now().minusDays(1), "EXITOSA")
        );

        transaccionRepositoryPort.guardar(
                new Transaccion(null, cuentaOrigen.getId(), new BigDecimal("850.00"), TipoMovimiento.TRANSFERENCIA_SALIENTE, "Transferencia Interbancaria SPEI", LocalDateTime.now(), "EXITOSA")
        );

        log.info("Datos sembrados con éxito. Cuenta ID [{}] disponible en http://localhost:8080/api/v1/cuentas/{}", cuentaOrigen.getId(), cuentaOrigen.getId());
    }
}
