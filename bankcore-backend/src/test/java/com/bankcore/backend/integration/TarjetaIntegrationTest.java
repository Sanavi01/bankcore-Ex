package com.bankcore.backend.integration;

import com.bankcore.backend.domain.EstadoCuenta;
import com.bankcore.backend.domain.model.Tarjeta;
import com.bankcore.backend.domain.port.TarjetaRepositoryPort;
import com.bankcore.backend.dto.CrearCuentaRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de Integración para el Subdominio de Tarjetas Bancarias.
 * Valida el ciclo de vida, consulta y operaciones de seguridad (bloqueo preventivo).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Subdominio Tarjetas: Pruebas de Integración y Ciclo de Seguridad")
class TarjetaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TarjetaRepositoryPort tarjetaRepositoryPort;

    private Long crearCuentaYObtenerId(String numeroCuenta) throws Exception {
        CrearCuentaRequestDto request = new CrearCuentaRequestDto(
                numeroCuenta,
                "Titular Tarjeta",
                new BigDecimal("3000.00")
        );
        String response = mockMvc.perform(post("/api/v1/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    @DisplayName("DADO una tarjeta activa, CUANDO se bloquea por seguridad, ENTONCES cambia a BLOQUEADA con HTTP 200")
    void debeBloquearTarjetaPorSeguridad() throws Exception {
        // GIVEN: Cuenta y tarjeta activa vinculada
        Long cuentaId = crearCuentaYObtenerId("001-TARJ-0001");
        Tarjeta tarjeta = new Tarjeta(
                null,
                "4532-1111-2222-3333",
                "VISA",
                "11/29",
                "456",
                new BigDecimal("5000.00"),
                new BigDecimal("5000.00"),
                EstadoCuenta.ACTIVA,
                cuentaId
        );
        Tarjeta guardada = tarjetaRepositoryPort.guardar(tarjeta);

        // WHEN: Se solicita el bloqueo preventivo
        mockMvc.perform(put("/api/v1/tarjetas/{id}/bloquear", guardada.getId()))
                // THEN: Responde 200 OK con la tarjeta bloqueada
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(guardada.getId()))
                .andExpect(jsonPath("$.estado").value("BLOQUEADA"));
    }

    @Test
    @DisplayName("DADO una tarjeta ya bloqueada, CUANDO se intenta bloquear de nuevo, ENTONCES rechaza con HTTP 422")
    void debeRechazarBloqueoDeTarjetaYaBloqueada() throws Exception {
        // GIVEN: Tarjeta ya bloqueada
        Long cuentaId = crearCuentaYObtenerId("001-TARJ-0002");
        Tarjeta tarjeta = new Tarjeta(
                null,
                "5412-2222-3333-4444",
                "MASTERCARD",
                "08/28",
                "789",
                new BigDecimal("2500.00"),
                new BigDecimal("2500.00"),
                EstadoCuenta.BLOQUEADA,
                cuentaId
        );
        Tarjeta guardada = tarjetaRepositoryPort.guardar(tarjeta);

        // WHEN & THEN: Falla con 422 Unprocessable Entity
        mockMvc.perform(put("/api/v1/tarjetas/{id}/bloquear", guardada.getId()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Regla de Negocio No Cumplida"))
                .andExpect(jsonPath("$.detail").value(containsString("ya se encuentra bloqueada")));
    }

    @Test
    @DisplayName("DADO una cuenta con tarjetas, CUANDO se consulta por ID de cuenta, ENTONCES retorna el listado con HTTP 200")
    void debeConsultarTarjetasPorCuentaId() throws Exception {
        // GIVEN: Cuenta con una tarjeta registrada
        Long cuentaId = crearCuentaYObtenerId("001-TARJ-0003");
        Tarjeta tarjeta = new Tarjeta(
                null,
                "4000-1234-5678-9010",
                "VISA",
                "05/30",
                "321",
                new BigDecimal("1000.00"),
                new BigDecimal("1000.00"),
                EstadoCuenta.ACTIVA,
                cuentaId
        );
        tarjetaRepositoryPort.guardar(tarjeta);

        // WHEN & THEN: Consulta exitosa de tarjetas asociadas con enmascaramiento PCI-DSS
        mockMvc.perform(get("/api/v1/tarjetas/cuenta/{cuentaId}", cuentaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].numeroTarjeta").value("**** **** **** 9010"))
                .andExpect(jsonPath("$[0].franquicia").value("VISA"));
    }
}
