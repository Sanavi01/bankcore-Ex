package com.bankcore.backend.domain.model;

import com.bankcore.backend.domain.TipoMovimiento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas Unitarias Puras del Agregado de Dominio Transaccion (DDD).
 * Cobertura del 100% de ramas y valores por defecto.
 */
@DisplayName("Dominio Puro: Pruebas Unitarias de Transaccion")
class TransaccionTest {

    @Nested
    @DisplayName("Reglas de Construcción y Valores por Defecto")
    class ConstructorRules {

        @Test
        @DisplayName("DADO fecha y estado nulos, ENTONCES asigna LocalDateTime.now() y estado 'EXITOSA' por defecto")
        void debeAsignarValoresPorDefecto() {
            // GIVEN & WHEN
            Transaccion transaccion = new Transaccion(
                    1L,
                    10L,
                    new BigDecimal("150.00"),
                    TipoMovimiento.RETIRO,
                    "Retiro Cajero",
                    null,
                    null
            );

            // THEN
            assertThat(transaccion.getId()).isEqualTo(1L);
            assertThat(transaccion.getCuentaId()).isEqualTo(10L);
            assertThat(transaccion.getMonto()).isEqualByComparingTo(new BigDecimal("150.00"));
            assertThat(transaccion.getTipo()).isEqualTo(TipoMovimiento.RETIRO);
            assertThat(transaccion.getConcepto()).isEqualTo("Retiro Cajero");
            assertThat(transaccion.getFecha()).isNotNull();
            assertThat(transaccion.getEstado()).isEqualTo("EXITOSA");
        }

        @Test
        @DisplayName("DADO fecha y estado explícitos, ENTONCES respeta los valores provistos")
        void debeRespetarValoresExplicitos() {
            LocalDateTime fecha = LocalDateTime.of(2026, 1, 15, 10, 30);

            Transaccion transaccion = new Transaccion(
                    2L,
                    20L,
                    new BigDecimal("500.00"),
                    TipoMovimiento.DEPOSITO,
                    "Abono Nómina",
                    fecha,
                    "PENDIENTE"
            );

            assertThat(transaccion.getFecha()).isEqualTo(fecha);
            assertThat(transaccion.getEstado()).isEqualTo("PENDIENTE");
            assertThat(transaccion.getTipo()).isEqualTo(TipoMovimiento.DEPOSITO);
        }
    }
}
