package com.bankcore.backend.integration;

import com.bankcore.backend.domain.model.RegistroAuditoria;
import com.bankcore.backend.domain.port.AuditoriaRepositoryPort;
import com.bankcore.backend.service.AuditoriaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de Integración para el Módulo de Auditoría Forense y Cumplimiento AML.
 * Valida los adaptadores primarios REST contra la base de datos persistida.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Auditoría AML: Pruebas de Integración y Consultas Forenses")
class AuditoriaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuditoriaRepositoryPort auditoriaRepositoryPort;

    @Test
    @DisplayName("DADO un ID de transacción con registro de auditoría, CUANDO se consulta por REST, ENTONCES responde 200 OK con el DTO forense")
    void debeConsultarAuditoriaPorIdTransaccion() throws Exception {
        // GIVEN: Registro de auditoría persistido
        RegistroAuditoria registro = new RegistroAuditoria(
                null,
                "tx-test-aml-101",
                "001-ORIGEN",
                "002-DESTINO",
                new BigDecimal("7500000.00"),
                AuditoriaService.RIESGO_ALTO,
                LocalDateTime.now(),
                LocalDateTime.now(),
                AuditoriaService.CANAL_RABBITMQ
        );
        auditoriaRepositoryPort.guardar(registro);

        // WHEN & THEN: Consulta REST por ID de transacción
        mockMvc.perform(get("/api/v1/auditoria/transacciones/{idTransaccion}", "tx-test-aml-101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTransaccion").value("tx-test-aml-101"))
                .andExpect(jsonPath("$.cuentaOrigen").value("001-ORIGEN"))
                .andExpect(jsonPath("$.cuentaDestino").value("002-DESTINO"))
                .andExpect(jsonPath("$.monto").value(7500000.00))
                .andExpect(jsonPath("$.clasificacionRiesgo").value(AuditoriaService.RIESGO_ALTO))
                .andExpect(jsonPath("$.canal").value(AuditoriaService.CANAL_RABBITMQ));
    }

    @Test
    @DisplayName("DADO registros clasificados con ALERTA_AML_REVISION, CUANDO se consulta por perfil de riesgo, ENTONCES responde 200 OK y lista filtrada")
    void debeConsultarAuditoriaPorClasificacionRiesgo() throws Exception {
        // GIVEN: Registros con diferentes clasificaciones
        RegistroAuditoria altoRiesgo = new RegistroAuditoria(
                null,
                "tx-alto-riesgo-1",
                "001-ORIGEN",
                "002-DESTINO",
                new BigDecimal("12000000.00"),
                AuditoriaService.RIESGO_ALTO,
                LocalDateTime.now(),
                LocalDateTime.now(),
                AuditoriaService.CANAL_RABBITMQ
        );
        RegistroAuditoria bajoRiesgo = new RegistroAuditoria(
                null,
                "tx-bajo-riesgo-1",
                "001-ORIGEN",
                "002-DESTINO",
                new BigDecimal("20000.00"),
                AuditoriaService.RIESGO_BAJO,
                LocalDateTime.now(),
                LocalDateTime.now(),
                AuditoriaService.CANAL_RABBITMQ
        );
        auditoriaRepositoryPort.guardar(altoRiesgo);
        auditoriaRepositoryPort.guardar(bajoRiesgo);

        // WHEN & THEN: Filtro por ALERTA_AML_REVISION retorna solo transacciones de riesgo alto
        mockMvc.perform(get("/api/v1/auditoria/riesgo/{clasificacionRiesgo}", AuditoriaService.RIESGO_ALTO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.idTransaccion == 'tx-alto-riesgo-1')].clasificacionRiesgo").value(AuditoriaService.RIESGO_ALTO));
    }
}
