package com.bankcore.backend.integration;

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
 * Pruebas de Integración para el Subdominio de Cuentas Bancarias.
 * Valida el ciclo de apertura y consulta cumpliendo el estándar BDD (Given - When - Then).
 * La anotación @Transactional garantiza aislamiento completo: revierte los cambios en cada prueba.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Subdominio Cuentas: Pruebas de Integración y API REST")
class CuentaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("DADO un payload válido, CUANDO se solicita la apertura de cuenta, ENTONCES se crea con estado ACTIVA y HTTP 201")
    void debeCrearCuentaExitosa() throws Exception {
        // GIVEN: Solicitud con datos válidos
        CrearCuentaRequestDto request = new CrearCuentaRequestDto(
                "001-9999-0001",
                "Santiago Navas",
                new BigDecimal("1500.00")
        );

        // WHEN: Se invoca el endpoint POST de creación
        mockMvc.perform(post("/api/v1/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // THEN: Responde 201 Created con el contrato esperado
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.numeroCuenta").value("001-9999-0001"))
                .andExpect(jsonPath("$.titular").value("Santiago Navas"))
                .andExpect(jsonPath("$.saldo").value(1500.00))
                .andExpect(jsonPath("$.estado").value("ACTIVA"));
    }

    @Test
    @DisplayName("DADO un número de cuenta existente, CUANDO se consulta por número, ENTONCES retorna el DTO con HTTP 200")
    void debeConsultarCuentaPorNumero() throws Exception {
        // GIVEN: Una cuenta creada previamente
        CrearCuentaRequestDto request = new CrearCuentaRequestDto(
                "001-9999-0002",
                "Evaluador CoE",
                new BigDecimal("800.00")
        );
        mockMvc.perform(post("/api/v1/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // WHEN: Se consulta por el número de cuenta
        mockMvc.perform(get("/api/v1/cuentas/numero/{numero}", "001-9999-0002"))
                // THEN: Responde 200 OK con los datos íntegros
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroCuenta").value("001-9999-0002"))
                .andExpect(jsonPath("$.titular").value("Evaluador CoE"))
                .andExpect(jsonPath("$.saldo").value(800.00))
                .andExpect(jsonPath("$.estado").value("ACTIVA"));
    }

    @Test
    @DisplayName("DADO un número de cuenta ya registrado, CUANDO se intenta crear de nuevo, ENTONCES rechaza con HTTP 422 (RFC 7807)")
    void debeRechazarCreacionDeCuentaDuplicada() throws Exception {
        // GIVEN: Cuenta existente
        CrearCuentaRequestDto request = new CrearCuentaRequestDto(
                "001-9999-0003",
                "Titular Original",
                new BigDecimal("500.00")
        );
        mockMvc.perform(post("/api/v1/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // WHEN: Se intenta registrar otra cuenta con el mismo número
        CrearCuentaRequestDto duplicada = new CrearCuentaRequestDto(
                "001-9999-0003",
                "Otro Titular",
                new BigDecimal("300.00")
        );

        // THEN: Falla con 422 Unprocessable Entity por regla de negocio
        mockMvc.perform(post("/api/v1/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicada)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Regla de Negocio No Cumplida"))
                .andExpect(jsonPath("$.type").value(containsString("business-rule-violation")))
                .andExpect(jsonPath("$.detail").value(containsString("Ya existe una cuenta")));
    }

    @Test
    @DisplayName("DADO un ID de cuenta existente, CUANDO se consulta por ID, ENTONCES retorna el DTO con HTTP 200")
    void debeConsultarCuentaPorId() throws Exception {
        // GIVEN: Una cuenta creada
        CrearCuentaRequestDto request = new CrearCuentaRequestDto(
                "001-9999-0004",
                "Cliente ID Test",
                new BigDecimal("1200.00")
        );
        String response = mockMvc.perform(post("/api/v1/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        // WHEN & THEN: Consulta por ID numérico
        mockMvc.perform(get("/api/v1/cuentas/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.numeroCuenta").value("001-9999-0004"))
                .andExpect(jsonPath("$.titular").value("Cliente ID Test"));
    }

    @Test
    @DisplayName("DADO una cuenta existente, CUANDO se consultan sus tarjetas asociadas, ENTONCES responde HTTP 200")
    void debeConsultarTarjetasDeLaCuenta() throws Exception {
        // GIVEN: Una cuenta creada
        CrearCuentaRequestDto request = new CrearCuentaRequestDto(
                "001-9999-0005",
                "Cliente Tarjetas Test",
                new BigDecimal("900.00")
        );
        String response = mockMvc.perform(post("/api/v1/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        // WHEN & THEN: Consulta endpoint de tarjetas asociadas a la cuenta
        mockMvc.perform(get("/api/v1/cuentas/{id}/tarjetas", id))
                .andExpect(status().isOk());
    }
}
