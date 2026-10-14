package com.bankcore.backend.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas Unitarias Puras del Agregado de Dominio RegistroAuditoria.
 * Cobertura del 100% de ramas y valores por defecto (SARLAFT/AML).
 */
@DisplayName("Dominio Puro: Pruebas Unitarias de RegistroAuditoria")
class RegistroAuditoriaTest {

    @Nested
    @DisplayName("Reglas de Construcción y Valores por Defecto")
    class ConstructorRules {

        @Test
        @DisplayName("DADO valores nulos para campos opcionales, ENTONCES asigna defaults seguros")
        void debeAsignarValoresPorDefecto() {
            RegistroAuditoria registro = new RegistroAuditoria(
                    1L,
                    "tx-12345",
                    "001",
                    "002",
                    null,
                    null,
                    null,
                    null,
                    null
            );

            assertThat(registro.getId()).isEqualTo(1L);
            assertThat(registro.getIdTransaccion()).isEqualTo("tx-12345");
            assertThat(registro.getCuentaOrigen()).isEqualTo("001");
            assertThat(registro.getCuentaDestino()).isEqualTo("002");
            assertThat(registro.getMonto()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(registro.getClasificacionRiesgo()).isEqualTo("BAJO_RIESGO_CONFORME");
            assertThat(registro.getFechaEvento()).isNotNull();
            assertThat(registro.getFechaAuditoria()).isNotNull();
            assertThat(registro.getCanal()).isEqualTo("RABBITMQ_ASYNC");
        }

        @Test
        @DisplayName("DADO valores explícitos, ENTONCES respeta exactamente cada valor provisto")
        void debeRespetarValoresExplicitos() {
            LocalDateTime fechaEvento = LocalDateTime.of(2026, 3, 10, 14, 0);
            LocalDateTime fechaAuditoria = LocalDateTime.of(2026, 3, 10, 14, 1);

            RegistroAuditoria registro = new RegistroAuditoria(
                    99L,
                    "tx-99999",
                    "1001",
                    "1002",
                    new BigDecimal("7500000.00"),
                    "ALERTA_AML_REVISION",
                    fechaEvento,
                    fechaAuditoria,
                    "RABBITMQ_ASYNC"
            );

            assertThat(registro.getId()).isEqualTo(99L);
            assertThat(registro.getIdTransaccion()).isEqualTo("tx-99999");
            assertThat(registro.getCuentaOrigen()).isEqualTo("1001");
            assertThat(registro.getCuentaDestino()).isEqualTo("1002");
            assertThat(registro.getMonto()).isEqualByComparingTo(new BigDecimal("7500000.00"));
            assertThat(registro.getClasificacionRiesgo()).isEqualTo("ALERTA_AML_REVISION");
            assertThat(registro.getFechaEvento()).isEqualTo(fechaEvento);
            assertThat(registro.getFechaAuditoria()).isEqualTo(fechaAuditoria);
            assertThat(registro.getCanal()).isEqualTo("RABBITMQ_ASYNC");
        }
    }
}
