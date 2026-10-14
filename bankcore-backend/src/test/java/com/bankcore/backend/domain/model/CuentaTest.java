package com.bankcore.backend.domain.model;

import com.bankcore.backend.domain.EstadoCuenta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Pruebas Unitarias Puras del Agregado de Dominio Cuenta (DDD).
 * Cobertura exhaustiva de ramas y casos de borde de reglas de negocio contables.
 * No requiere Spring context (ejecución ultra-rápida en memoria).
 */
@DisplayName("Dominio Puro: Pruebas Unitarias de Cuenta")
class CuentaTest {

    @Nested
    @DisplayName("Reglas de Construcción y Valores por Defecto")
    class ConstructorRules {

        @Test
        @DisplayName("DADO parámetros nulos opcionales, ENTONCES asigna valores por defecto seguros")
        void debeAsignarValoresPorDefecto() {
            // GIVEN & WHEN: Creación con saldo, estado y fecha nulos
            Cuenta cuenta = new Cuenta(1L, "001-TEST", "Santiago Navas", null, null, null);

            // THEN: Saldo es ZERO, estado es ACTIVA y fechaCreacion no es nula
            assertThat(cuenta.getSaldo()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(cuenta.getEstado()).isEqualTo(EstadoCuenta.ACTIVA);
            assertThat(cuenta.getFechaCreacion()).isNotNull();
            assertThat(cuenta.getId()).isEqualTo(1L);
            assertThat(cuenta.getNumeroCuenta()).isEqualTo("001-TEST");
            assertThat(cuenta.getTitular()).isEqualTo("Santiago Navas");
        }
    }

    @Nested
    @DisplayName("Reglas de Negocio: puedeTransferir")
    class PuedeTransferirRules {

        @Test
        @DisplayName("DADO saldo suficiente y cuenta activa, ENTONCES permite transferir")
        void debePermitirTransferirCuandoCumpleCondiciones() {
            Cuenta cuenta = new Cuenta(1L, "001-TEST", "Titular", new BigDecimal("1000.00"), EstadoCuenta.ACTIVA, LocalDateTime.now());

            boolean resultado = cuenta.puedeTransferir(new BigDecimal("500.00"));

            assertThat(resultado).isTrue();
        }

        @Test
        @DisplayName("DADO monto igual al saldo exacto, ENTONCES permite transferir")
        void debePermitirTransferirSaldoExacto() {
            Cuenta cuenta = new Cuenta(1L, "001-TEST", "Titular", new BigDecimal("500.00"), EstadoCuenta.ACTIVA, LocalDateTime.now());

            boolean resultado = cuenta.puedeTransferir(new BigDecimal("500.00"));

            assertThat(resultado).isTrue();
        }

        @Test
        @DisplayName("DADO cuenta inactiva o bloqueada, ENTONCES rechaza transferencia")
        void debeRechazarCuandoCuentaNoEstaActiva() {
            Cuenta cuentaBloqueada = new Cuenta(1L, "001-TEST", "Titular", new BigDecimal("1000.00"), EstadoCuenta.BLOQUEADA, LocalDateTime.now());
            Cuenta cuentaCancelada = new Cuenta(2L, "002-TEST", "Titular", new BigDecimal("1000.00"), EstadoCuenta.CANCELADA, LocalDateTime.now());

            assertThat(cuentaBloqueada.puedeTransferir(new BigDecimal("100.00"))).isFalse();
            assertThat(cuentaCancelada.puedeTransferir(new BigDecimal("100.00"))).isFalse();
        }

        @Test
        @DisplayName("DADO monto nulo, cero o negativo, ENTONCES rechaza transferencia")
        void debeRechazarMontosInvalidos() {
            Cuenta cuenta = new Cuenta(1L, "001-TEST", "Titular", new BigDecimal("1000.00"), EstadoCuenta.ACTIVA, LocalDateTime.now());

            assertThat(cuenta.puedeTransferir(null)).isFalse();
            assertThat(cuenta.puedeTransferir(BigDecimal.ZERO)).isFalse();
            assertThat(cuenta.puedeTransferir(new BigDecimal("-100.00"))).isFalse();
        }

        @Test
        @DisplayName("DADO saldo menor al monto solicitado, ENTONCES rechaza por fondos insuficientes")
        void debeRechazarPorSaldoInsuficiente() {
            Cuenta cuenta = new Cuenta(1L, "001-TEST", "Titular", new BigDecimal("200.00"), EstadoCuenta.ACTIVA, LocalDateTime.now());

            assertThat(cuenta.puedeTransferir(new BigDecimal("500.00"))).isFalse();
        }
    }

    @Nested
    @DisplayName("Reglas de Negocio: debitar")
    class DebitarRules {

        @Test
        @DisplayName("DADO monto válido, ENTONCES descuenta el saldo correctamente")
        void debeDebitarSaldoCorrectamente() {
            Cuenta cuenta = new Cuenta(1L, "001-TEST", "Titular", new BigDecimal("1000.00"), EstadoCuenta.ACTIVA, LocalDateTime.now());

            cuenta.debitar(new BigDecimal("400.00"));

            assertThat(cuenta.getSaldo()).isEqualByComparingTo(new BigDecimal("600.00"));
        }

        @Test
        @DisplayName("DADO condición inválida de transferencia, ENTONCES lanza IllegalStateException")
        void debeLanzarExcepcionCuandoNoPuedeDebitar() {
            Cuenta cuenta = new Cuenta(1L, "001-TEST", "Titular", new BigDecimal("100.00"), EstadoCuenta.ACTIVA, LocalDateTime.now());

            assertThatThrownBy(() -> cuenta.debitar(new BigDecimal("500.00")))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Fondos insuficientes");
        }
    }

    @Nested
    @DisplayName("Reglas de Negocio: acreditar")
    class AcreditarRules {

        @Test
        @DisplayName("DADO monto válido y cuenta activa, ENTONCES incrementa el saldo")
        void debeAcreditarSaldoCorrectamente() {
            Cuenta cuenta = new Cuenta(1L, "001-TEST", "Titular", new BigDecimal("500.00"), EstadoCuenta.ACTIVA, LocalDateTime.now());

            cuenta.acreditar(new BigDecimal("300.00"));

            assertThat(cuenta.getSaldo()).isEqualByComparingTo(new BigDecimal("800.00"));
        }

        @Test
        @DisplayName("DADO cuenta cancelada, ENTONCES prohíbe abonar dinero")
        void debeRechazarAcreditarEnCuentaCancelada() {
            Cuenta cuenta = new Cuenta(1L, "001-TEST", "Titular", new BigDecimal("500.00"), EstadoCuenta.CANCELADA, LocalDateTime.now());

            assertThatThrownBy(() -> cuenta.acreditar(new BigDecimal("100.00")))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("cancelada");
        }

        @Test
        @DisplayName("DADO monto nulo, cero o negativo al acreditar, ENTONCES lanza IllegalArgumentException")
        void debeRechazarMontosInvalidosAlAcreditar() {
            Cuenta cuenta = new Cuenta(1L, "001-TEST", "Titular", new BigDecimal("500.00"), EstadoCuenta.ACTIVA, LocalDateTime.now());

            assertThatThrownBy(() -> cuenta.acreditar(null))
                    .isInstanceOf(IllegalArgumentException.class);

            assertThatThrownBy(() -> cuenta.acreditar(BigDecimal.ZERO))
                    .isInstanceOf(IllegalArgumentException.class);

            assertThatThrownBy(() -> cuenta.acreditar(new BigDecimal("-50.00")))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
