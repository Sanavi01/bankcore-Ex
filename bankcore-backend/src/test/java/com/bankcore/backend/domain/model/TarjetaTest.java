package com.bankcore.backend.domain.model;

import com.bankcore.backend.domain.EstadoCuenta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

/**
 * Pruebas Unitarias Puras del Agregado de Dominio Tarjeta (DDD).
 * Cobertura exhaustiva de ramas y casos de borde de reglas de negocio contables y de seguridad.
 * No requiere Spring context (ejecución ultra-rápida en memoria).
 */
@DisplayName("Dominio Puro: Pruebas Unitarias de Tarjeta")
class TarjetaTest {

    @Nested
    @DisplayName("Reglas de Construcción y Valores por Defecto")
    class ConstructorRules {

        @Test
        @DisplayName("DADO cupoDisponible y estado nulos, ENTONCES asigna cupoTotal y estado ACTIVA por defecto")
        void debeAsignarValoresPorDefecto() {
            // GIVEN & WHEN: Creación con cupoDisponible y estado nulos
            Tarjeta tarjeta = new Tarjeta(
                    1L,
                    "4532-XXXX-XXXX-1234",
                    "VISA",
                    "12/28",
                    "123",
                    new BigDecimal("5000.00"),
                    null,
                    null,
                    10L
            );

            // THEN: cupoDisponible hereda cupoTotal, estado es ACTIVA y los getters devuelven los valores esperados
            assertThat(tarjeta.getCupoDisponible()).isEqualByComparingTo(new BigDecimal("5000.00"));
            assertThat(tarjeta.getEstado()).isEqualTo(EstadoCuenta.ACTIVA);
            assertThat(tarjeta.getId()).isEqualTo(1L);
            assertThat(tarjeta.getNumeroTarjeta()).isEqualTo("4532-XXXX-XXXX-1234");
            assertThat(tarjeta.getFranquicia()).isEqualTo("VISA");
            assertThat(tarjeta.getFechaVencimiento()).isEqualTo("12/28");
            assertThat(tarjeta.getCvv()).isEqualTo("123");
            assertThat(tarjeta.getCupoTotal()).isEqualByComparingTo(new BigDecimal("5000.00"));
            assertThat(tarjeta.getCuentaId()).isEqualTo(10L);
        }

        @Test
        @DisplayName("DADO valores explícitos no nulos, ENTONCES respeta los valores provistos")
        void debeRespetarValoresExplicitos() {
            Tarjeta tarjeta = new Tarjeta(
                    2L,
                    "5412-XXXX-XXXX-5678",
                    "MASTERCARD",
                    "05/27",
                    "456",
                    new BigDecimal("3000.00"),
                    new BigDecimal("1500.00"),
                    EstadoCuenta.BLOQUEADA,
                    20L
            );

            assertThat(tarjeta.getCupoDisponible()).isEqualByComparingTo(new BigDecimal("1500.00"));
            assertThat(tarjeta.getEstado()).isEqualTo(EstadoCuenta.BLOQUEADA);
        }
    }

    @Nested
    @DisplayName("Reglas de Negocio: puedeConsumir")
    class PuedeConsumirRules {

        @Test
        @DisplayName("DADO cupo suficiente y tarjeta activa, ENTONCES permite consumo")
        void debePermitirConsumoConCupoSuficiente() {
            Tarjeta tarjeta = new Tarjeta(
                    1L, "4532-XXXX-XXXX-1234", "VISA", "12/28", "123",
                    new BigDecimal("5000.00"), new BigDecimal("2000.00"), EstadoCuenta.ACTIVA, 10L
            );

            boolean puede = tarjeta.puedeConsumir(new BigDecimal("1000.00"));

            assertThat(puede).isTrue();
        }

        @Test
        @DisplayName("DADO consumo por el monto exacto del cupo disponible, ENTONCES permite consumo")
        void debePermitirConsumoPorCupoExacto() {
            Tarjeta tarjeta = new Tarjeta(
                    1L, "4532-XXXX-XXXX-1234", "VISA", "12/28", "123",
                    new BigDecimal("5000.00"), new BigDecimal("2000.00"), EstadoCuenta.ACTIVA, 10L
            );

            boolean puede = tarjeta.puedeConsumir(new BigDecimal("2000.00"));

            assertThat(puede).isTrue();
        }

        @Test
        @DisplayName("DADO tarjeta inactiva o bloqueada, ENTONCES rechaza consumo")
        void debeRechazarConsumoCuandoTarjetaNoEstaActiva() {
            Tarjeta tarjetaBloqueada = new Tarjeta(
                    1L, "4532-XXXX-XXXX-1234", "VISA", "12/28", "123",
                    new BigDecimal("5000.00"), new BigDecimal("2000.00"), EstadoCuenta.BLOQUEADA, 10L
            );
            Tarjeta tarjetaCancelada = new Tarjeta(
                    2L, "4532-XXXX-XXXX-1234", "VISA", "12/28", "123",
                    new BigDecimal("5000.00"), new BigDecimal("2000.00"), EstadoCuenta.CANCELADA, 10L
            );

            assertThat(tarjetaBloqueada.puedeConsumir(new BigDecimal("500.00"))).isFalse();
            assertThat(tarjetaCancelada.puedeConsumir(new BigDecimal("500.00"))).isFalse();
        }

        @Test
        @DisplayName("DADO monto nulo, cero o negativo, ENTONCES rechaza consumo")
        void debeRechazarMontosInvalidos() {
            Tarjeta tarjeta = new Tarjeta(
                    1L, "4532-XXXX-XXXX-1234", "VISA", "12/28", "123",
                    new BigDecimal("5000.00"), new BigDecimal("2000.00"), EstadoCuenta.ACTIVA, 10L
            );

            assertThat(tarjeta.puedeConsumir(null)).isFalse();
            assertThat(tarjeta.puedeConsumir(BigDecimal.ZERO)).isFalse();
            assertThat(tarjeta.puedeConsumir(new BigDecimal("-100.00"))).isFalse();
        }

        @Test
        @DisplayName("DADO monto superior al cupo disponible, ENTONCES rechaza por cupo insuficiente")
        void debeRechazarPorCupoInsuficiente() {
            Tarjeta tarjeta = new Tarjeta(
                    1L, "4532-XXXX-XXXX-1234", "VISA", "12/28", "123",
                    new BigDecimal("5000.00"), new BigDecimal("500.00"), EstadoCuenta.ACTIVA, 10L
            );

            assertThat(tarjeta.puedeConsumir(new BigDecimal("600.00"))).isFalse();
        }
    }

    @Nested
    @DisplayName("Reglas de Negocio: consumir")
    class ConsumirRules {

        @Test
        @DisplayName("DADO monto válido y cupo disponible, ENTONCES descuenta el cupo disponible")
        void debeDescontarCupoDisponibleExitosamente() {
            Tarjeta tarjeta = new Tarjeta(
                    1L, "4532-XXXX-XXXX-1234", "VISA", "12/28", "123",
                    new BigDecimal("5000.00"), new BigDecimal("3000.00"), EstadoCuenta.ACTIVA, 10L
            );

            tarjeta.consumir(new BigDecimal("1200.00"));

            assertThat(tarjeta.getCupoDisponible()).isEqualByComparingTo(new BigDecimal("1800.00"));
        }

        @Test
        @DisplayName("DADO consumo no permitido, ENTONCES lanza IllegalStateException")
        void debeLanzarExcepcionCuandoNoCumpleCondiciones() {
            Tarjeta tarjeta = new Tarjeta(
                    1L, "4532-XXXX-XXXX-1234", "VISA", "12/28", "123",
                    new BigDecimal("5000.00"), new BigDecimal("100.00"), EstadoCuenta.ACTIVA, 10L
            );

            assertThatThrownBy(() -> tarjeta.consumir(new BigDecimal("500.00")))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cupo disponible insuficiente o tarjeta inactiva");
        }
    }

    @Nested
    @DisplayName("Reglas de Negocio: bloquear")
    class BloquearRules {

        @Test
        @DisplayName("DADO tarjeta activa, ENTONCES cambia el estado a BLOQUEADA")
        void debeBloquearTarjetaActiva() {
            Tarjeta tarjeta = new Tarjeta(
                    1L, "4532-XXXX-XXXX-1234", "VISA", "12/28", "123",
                    new BigDecimal("5000.00"), new BigDecimal("3000.00"), EstadoCuenta.ACTIVA, 10L
            );

            tarjeta.bloquear();

            assertThat(tarjeta.getEstado()).isEqualTo(EstadoCuenta.BLOQUEADA);
        }

        @Test
        @DisplayName("DADO tarjeta previamente bloqueada, ENTONCES lanza IllegalStateException")
        void debeRechazarBloqueoSiYaEstabaBloqueada() {
            Tarjeta tarjeta = new Tarjeta(
                    1L, "4532-XXXX-XXXX-1234", "VISA", "12/28", "123",
                    new BigDecimal("5000.00"), new BigDecimal("3000.00"), EstadoCuenta.BLOQUEADA, 10L
            );

            assertThatThrownBy(tarjeta::bloquear)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("La tarjeta ya se encuentra bloqueada");
        }
    }
}
