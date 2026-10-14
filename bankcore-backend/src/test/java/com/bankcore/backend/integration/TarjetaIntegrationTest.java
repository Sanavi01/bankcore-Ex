package com.bankcore.backend.integration;

import com.bankcore.backend.domain.EstadoCuenta;
import com.bankcore.backend.domain.model.Tarjeta;
import com.bankcore.backend.domain.port.TarjetaRepositoryPort;
import com.bankcore.backend.dto.CrearCuentaRequestDto;
import com.bankcore.backend.dto.CrearTarjetaRequestDto;
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

    @Test
    @DisplayName("DADO un ID de tarjeta existente, CUANDO se consulta por ID, ENTONCES retorna el DTO con HTTP 200")
    void debeConsultarTarjetaPorId() throws Exception {
        // GIVEN: Cuenta y tarjeta creada
        Long cuentaId = crearCuentaYObtenerId("001-TARJ-0004");
        Tarjeta tarjeta = new Tarjeta(
                null,
                "4532-9876-5432-1098",
                "VISA",
                "09/29",
                "999",
                new BigDecimal("4000.00"),
                new BigDecimal("4000.00"),
                EstadoCuenta.ACTIVA,
                cuentaId
        );
        Tarjeta guardada = tarjetaRepositoryPort.guardar(tarjeta);

        // WHEN & THEN: Consulta por ID individual
        mockMvc.perform(get("/api/v1/tarjetas/{id}", guardada.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(guardada.getId()))
                .andExpect(jsonPath("$.numeroTarjeta").value("**** **** **** 1098"))
                .andExpect(jsonPath("$.franquicia").value("VISA"))
                .andExpect(jsonPath("$.cuentaId").value(cuentaId));
    }

    @Test
    @DisplayName("DADO payload válido, CUANDO se emite una tarjeta vía POST, ENTONCES retorna 201 CREATED con datos enmascarados")
    void debeEmitirTarjetaExitosa() throws Exception {
        // GIVEN: Cuenta bancaria existente
        Long cuentaId = crearCuentaYObtenerId("001-TARJ-0005");
        CrearTarjetaRequestDto request = new CrearTarjetaRequestDto(
                cuentaId,
                "MASTERCARD",
                new BigDecimal("6000.00")
        );

        // WHEN & THEN: Emisión exitosa vía REST
        mockMvc.perform(post("/api/v1/tarjetas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.franquicia").value("MASTERCARD"))
                .andExpect(jsonPath("$.cupoTotal").value(6000.00))
                .andExpect(jsonPath("$.cupoDisponible").value(6000.00))
                .andExpect(jsonPath("$.estado").value("ACTIVA"))
                .andExpect(jsonPath("$.numeroTarjeta").value(startsWith("**** **** **** ")))
                .andExpect(jsonPath("$.cuentaId").value(cuentaId));
    }

    @Test
    @DisplayName("DADO una tarjeta bloqueada, CUANDO se solicita desbloquear vía PUT, ENTONCES retorna 200 OK y estado ACTIVA")
    void debeDesbloquearTarjetaPreviamenteBloqueada() throws Exception {
        // GIVEN: Cuenta y tarjeta bloqueada
        Long cuentaId = crearCuentaYObtenerId("001-TARJ-0006");
        Tarjeta tarjeta = new Tarjeta(
                null,
                "5412-8888-7777-6666",
                "MASTERCARD",
                "07/28",
                "111",
                new BigDecimal("3000.00"),
                new BigDecimal("3000.00"),
                EstadoCuenta.BLOQUEADA,
                cuentaId
        );
        Tarjeta guardada = tarjetaRepositoryPort.guardar(tarjeta);

        // WHEN & THEN: Desbloqueo exitoso
        mockMvc.perform(put("/api/v1/tarjetas/{id}/desbloquear", guardada.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(guardada.getId()))
                .andExpect(jsonPath("$.estado").value("ACTIVA"));
    }

    @Test
    @DisplayName("DADO una tarjeta activa, CUANDO se intenta desbloquear, ENTONCES rechaza con HTTP 422")
    void debeRechazarDesbloqueoDeTarjetaYaActiva() throws Exception {
        // GIVEN: Tarjeta ya activa
        Long cuentaId = crearCuentaYObtenerId("001-TARJ-0007");
        Tarjeta tarjeta = new Tarjeta(
                null,
                "4532-3333-2222-1111",
                "VISA",
                "03/29",
                "222",
                new BigDecimal("1500.00"),
                new BigDecimal("1500.00"),
                EstadoCuenta.ACTIVA,
                cuentaId
        );
        Tarjeta guardada = tarjetaRepositoryPort.guardar(tarjeta);

        // WHEN & THEN: Falla con 422
        mockMvc.perform(put("/api/v1/tarjetas/{id}/desbloquear", guardada.getId()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Regla de Negocio No Cumplida"))
                .andExpect(jsonPath("$.detail").value(containsString("ya se encuentra activa")));
    }
}
