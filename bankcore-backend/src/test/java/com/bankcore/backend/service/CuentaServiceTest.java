package com.bankcore.backend.service;

import com.bankcore.backend.domain.EstadoCuenta;
import com.bankcore.backend.domain.TipoMovimiento;
import com.bankcore.backend.domain.model.Cuenta;
import com.bankcore.backend.domain.model.Transaccion;
import com.bankcore.backend.domain.port.CuentaRepositoryPort;
import com.bankcore.backend.domain.port.TransaccionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas Unitarias de CuentaService (Fachada GoF).
 * Cobertura de búsquedas, aperturas y excepciones de cuentas inexistentes.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Servicio: CuentaService")
class CuentaServiceTest {

    @Mock
    private CuentaRepositoryPort cuentaRepositoryPort;

    @Mock
    private TransaccionRepositoryPort transaccionRepositoryPort;

    private CuentaService cuentaService;

    @BeforeEach
    void setUp() {
        cuentaService = new CuentaService(cuentaRepositoryPort, transaccionRepositoryPort);
    }

    @Nested
    @DisplayName("Consultas de Cuentas")
    class ConsultaRules {

        @Test
        @DisplayName("DADO un ID existente, CUANDO se consulta por ID, ENTONCES retorna la cuenta")
        void debeRetornarCuentaCuandoExistePorId() {
            Cuenta cuenta = new Cuenta(1L, "001-TEST", "Titular", BigDecimal.TEN, EstadoCuenta.ACTIVA, LocalDateTime.now());
            when(cuentaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(cuenta));

            Cuenta resultado = cuentaService.obtenerPorId(1L);

            assertThat(resultado).isEqualTo(cuenta);
        }

        @Test
        @DisplayName("DADO un ID inexistente, CUANDO se consulta por ID, ENTONCES lanza IllegalArgumentException")
        void debeLanzarExcepcionCuandoNoExistePorId() {
            when(cuentaRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cuentaService.obtenerPorId(99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Cuenta no encontrada con ID: 99");
        }

        @Test
        @DisplayName("DADO un número existente, CUANDO se consulta por número, ENTONCES retorna la cuenta")
        void debeRetornarCuentaCuandoExistePorNumero() {
            Cuenta cuenta = new Cuenta(1L, "001-TEST", "Titular", BigDecimal.TEN, EstadoCuenta.ACTIVA, LocalDateTime.now());
            when(cuentaRepositoryPort.buscarPorNumeroCuenta("001-TEST")).thenReturn(Optional.of(cuenta));

            Cuenta resultado = cuentaService.obtenerPorNumeroCuenta("001-TEST");

            assertThat(resultado).isEqualTo(cuenta);
        }

        @Test
        @DisplayName("DADO un número inexistente, CUANDO se consulta por número, ENTONCES lanza IllegalArgumentException")
        void debeLanzarExcepcionCuandoNoExistePorNumero() {
            when(cuentaRepositoryPort.buscarPorNumeroCuenta("001-NO-EXISTE")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cuentaService.obtenerPorNumeroCuenta("001-NO-EXISTE"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Cuenta no encontrada con número: 001-NO-EXISTE");
        }
    }

    @Nested
    @DisplayName("Apertura de Cuentas")
    class AperturaRules {

        @Test
        @DisplayName("DADO un número duplicado, CUANDO se intenta crear, ENTONCES lanza IllegalStateException")
        void debeRechazarCreacionConNumeroDuplicado() {
            Cuenta existente = new Cuenta(1L, "001-DUPLICADA", "Titular", BigDecimal.TEN, EstadoCuenta.ACTIVA, LocalDateTime.now());
            when(cuentaRepositoryPort.buscarPorNumeroCuenta("001-DUPLICADA")).thenReturn(Optional.of(existente));

            assertThatThrownBy(() -> cuentaService.crearCuenta("001-DUPLICADA", "Otro", BigDecimal.ONE))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Ya existe una cuenta con el número: 001-DUPLICADA");

            verify(cuentaRepositoryPort, never()).guardar(any());
        }

        @Test
        @DisplayName("DADO datos válidos y número disponible, CUANDO se crea cuenta, ENTONCES persiste y retorna la cuenta")
        void debeCrearYGuardarCuentaValida() {
            when(cuentaRepositoryPort.buscarPorNumeroCuenta("001-NUEVA")).thenReturn(Optional.empty());

            Cuenta guardada = new Cuenta(10L, "001-NUEVA", "Nuevo Titular", new BigDecimal("500.00"), EstadoCuenta.ACTIVA, LocalDateTime.now());
            when(cuentaRepositoryPort.guardar(any(Cuenta.class))).thenReturn(guardada);

            Cuenta resultado = cuentaService.crearCuenta("001-NUEVA", "Nuevo Titular", new BigDecimal("500.00"));

            assertThat(resultado).isEqualTo(guardada);
            verify(cuentaRepositoryPort).guardar(any(Cuenta.class));
        }
    }

    @Nested
    @DisplayName("Movimientos Contables")
    class MovimientosRules {

        @Test
        @DisplayName("DADO cuenta existente, CUANDO se consultan movimientos, ENTONCES retorna lista de transacciones")
        void debeRetornarMovimientosCuandoCuentaExiste() {
            Cuenta cuenta = new Cuenta(1L, "001-TEST", "Titular", BigDecimal.TEN, EstadoCuenta.ACTIVA, LocalDateTime.now());
            when(cuentaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(cuenta));

            List<Transaccion> transacciones = List.of(
                    new Transaccion(1L, 1L, BigDecimal.TEN, TipoMovimiento.DEPOSITO, "Abono", LocalDateTime.now(), "EXITOSA")
            );
            when(transaccionRepositoryPort.buscarPorCuentaId(1L)).thenReturn(transacciones);

            List<Transaccion> resultado = cuentaService.obtenerMovimientosPorCuentaId(1L);

            assertThat(resultado).isEqualTo(transacciones);
        }

        @Test
        @DisplayName("DADO cuenta inexistente, CUANDO se consultan movimientos, ENTONCES lanza IllegalArgumentException")
        void debeFallarAlConsultarMovimientosDeCuentaInexistente() {
            when(cuentaRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cuentaService.obtenerMovimientosPorCuentaId(99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Cuenta no encontrada con ID: 99");

            verifyNoInteractions(transaccionRepositoryPort);
        }

        @Test
        @DisplayName("DADO cuenta existente, CUANDO se realiza depósito, ENTONCES acredita saldo y guarda movimiento")
        void debeDepositarExitosamente() {
            Cuenta cuenta = new Cuenta(1L, "001-TEST", "Titular", new BigDecimal("100.00"), EstadoCuenta.ACTIVA, LocalDateTime.now());
            when(cuentaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(cuenta));
            when(cuentaRepositoryPort.guardar(any(Cuenta.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Cuenta actualizada = cuentaService.depositar(1L, new BigDecimal("50.00"), "Abono nómina");

            assertThat(actualizada.getSaldo()).isEqualByComparingTo(new BigDecimal("150.00"));
            verify(cuentaRepositoryPort).guardar(any(Cuenta.class));
            verify(transaccionRepositoryPort).guardar(any(Transaccion.class));
        }

        @Test
        @DisplayName("DADO concepto nulo o vacío, CUANDO se deposita, ENTONCES asigna concepto por defecto")
        void debeDepositarConConceptoPorDefecto() {
            Cuenta cuenta = new Cuenta(1L, "001-TEST", "Titular", new BigDecimal("100.00"), EstadoCuenta.ACTIVA, LocalDateTime.now());
            when(cuentaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(cuenta));
            when(cuentaRepositoryPort.guardar(any(Cuenta.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Cuenta actualizada = cuentaService.depositar(1L, new BigDecimal("20.00"), null);

            assertThat(actualizada.getSaldo()).isEqualByComparingTo(new BigDecimal("120.00"));
            verify(transaccionRepositoryPort, atLeastOnce()).guardar(any(Transaccion.class));
        }
    }
}
