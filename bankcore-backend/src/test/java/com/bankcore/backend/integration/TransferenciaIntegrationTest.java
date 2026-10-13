package com.bankcore.backend.integration;

import com.bankcore.backend.dto.CrearCuentaRequestDto;
import com.bankcore.backend.dto.TransferenciaRequestDto;
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
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de Integración para el Motor Transaccional e Idempotencia.
 * Sigue la estructura estricta BDD (Given - When - Then).
 * @Transactional aísla cada escenario revirtiendo las transacciones al finalizar.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Motor Transaccional: Pruebas de Transferencias e Idempotencia")
class TransferenciaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private void prepararCuenta(String numeroCuenta, String titular, BigDecimal saldo) throws Exception {
        CrearCuentaRequestDto request = new CrearCuentaRequestDto(numeroCuenta, titular, saldo);
        mockMvc.perform(post("/api/v1/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("DADO dos cuentas activas, CUANDO se transfiere dinero, ENTONCES los saldos se actualizan atómicamente y emite 201 Created")
    void debeEjecutarTransferenciaAtomicaExitosa() throws Exception {
        // GIVEN: Cuentas origen y destino con saldo suficiente
        prepararCuenta("001-TRAN-0001", "Origen Valido", new BigDecimal("1000.00"));
        prepararCuenta("001-TRAN-0002", "Destino Valido", new BigDecimal("250.00"));

        TransferenciaRequestDto request = new TransferenciaRequestDto(
                "001-TRAN-0001",
                "001-TRAN-0002",
                new BigDecimal("300.00"),
                "Transferencia exitosa BDD"
        );

        // WHEN: Se realiza la transferencia
        mockMvc.perform(post("/api/v1/transferencias")
                        .header("Idempotency-Key", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // THEN: Responde 201 Created con comprobante exitoso
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idTransaccion").isNotEmpty())
                .andExpect(jsonPath("$.cuentaOrigen").value("001-TRAN-0001"))
                .andExpect(jsonPath("$.cuentaDestino").value("001-TRAN-0002"))
                .andExpect(jsonPath("$.monto").value(300.00))
                .andExpect(jsonPath("$.estado").value("EXITOSA"));

        // AND: Saldos verificados atómicamente (1000 - 300 = 700; 250 + 300 = 550)
        mockMvc.perform(get("/api/v1/cuentas/numero/{numero}", "001-TRAN-0001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(700.00));

        mockMvc.perform(get("/api/v1/cuentas/numero/{numero}", "001-TRAN-0002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(550.00));
    }

    @Test
    @DisplayName("DADO un intento repetido con la misma Idempotency-Key, ENTONCES retorna el comprobante previo sin duplicar el débito")
    void debeGarantizarIdempotenciaAnteReintentos() throws Exception {
        // GIVEN: Cuentas preparadas y una clave de idempotencia fija
        prepararCuenta("001-IDEM-0001", "Titular Idempotencia", new BigDecimal("1000.00"));
        prepararCuenta("001-IDEM-0002", "Receptor Idempotencia", new BigDecimal("200.00"));
        String idempotencyKey = UUID.randomUUID().toString();

        TransferenciaRequestDto request = new TransferenciaRequestDto(
                "001-IDEM-0001",
                "001-IDEM-0002",
                new BigDecimal("400.00"),
                "Pago único protegido"
        );

        // WHEN 1: Primera llamada procesa la transacción
        mockMvc.perform(post("/api/v1/transferencias")
                        .header("Idempotency-Key", idempotencyKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.monto").value(400.00));

        // WHEN 2: Segundo intento exacto con la misma clave (simulando timeout/reintento de red)
        mockMvc.perform(post("/api/v1/transferencias")
                        .header("Idempotency-Key", idempotencyKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // THEN: Devuelve el mismo comprobante
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.monto").value(400.00));

        // AND: El saldo fue debitado UNA SOLA VEZ (1000 - 400 = 600, NO 200)
        mockMvc.perform(get("/api/v1/cuentas/numero/{numero}", "001-IDEM-0001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(600.00));
    }

    @Test
    @DisplayName("DADO una transferencia ejecutada, CUANDO se auditan movimientos, ENTONCES registra débito en origen y crédito en destino")
    void debeRegistrarPartidaDobleContable() throws Exception {
        // GIVEN: Cuentas y transferencia completada
        prepararCuenta("001-PART-0001", "Auditoria Origen", new BigDecimal("500.00"));
        prepararCuenta("001-PART-0002", "Auditoria Destino", new BigDecimal("100.00"));

        TransferenciaRequestDto request = new TransferenciaRequestDto(
                "001-PART-0001",
                "001-PART-0002",
                new BigDecimal("150.00"),
                "Prueba Partida Doble"
        );

        mockMvc.perform(post("/api/v1/transferencias")
                        .header("Idempotency-Key", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // WHEN & THEN: Obtener cuenta origen para conocer su ID y validar libro contable
        String jsonOrigen = mockMvc.perform(get("/api/v1/cuentas/numero/{numero}", "001-PART-0001"))
                .andReturn().getResponse().getContentAsString();
        Long idOrigen = objectMapper.readTree(jsonOrigen).get("id").asLong();

        mockMvc.perform(get("/api/v1/cuentas/{id}/movimientos", idOrigen))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tipo").value("TRANSFERENCIA_SALIENTE"))
                .andExpect(jsonPath("$[0].monto").value(150.00));
    }

    @Test
    @DisplayName("DADO saldo insuficiente, CUANDO se solicita transferir, ENTONCES rechaza con HTTP 422 y formato RFC 7807")
    void debeRechazarPorSaldoInsuficiente() throws Exception {
        // GIVEN: Cuenta con fondos limitados ($200.00)
        prepararCuenta("001-FOND-0001", "Cliente Sin Saldo", new BigDecimal("200.00"));
        prepararCuenta("001-FOND-0002", "Destino Sobregiro", new BigDecimal("100.00"));

        TransferenciaRequestDto requestExceso = new TransferenciaRequestDto(
                "001-FOND-0001",
                "001-FOND-0002",
                new BigDecimal("9999.00"),
                "Transferencia sin fondos"
        );

        // WHEN: Se intenta transferir un monto superior al saldo
        mockMvc.perform(post("/api/v1/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestExceso)))
                // THEN: Falla con 422 Unprocessable Entity bajo RFC 7807
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Regla de Negocio No Cumplida"))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.type").value(containsString("business-rule-violation")))
                .andExpect(jsonPath("$.detail").value(containsString("insuficientes")));
    }

    @Test
    @DisplayName("DADO un monto negativo, CUANDO se valida el contrato perimetral, ENTONCES rechaza con HTTP 400 y formato RFC 7807")
    void debeRechazarPorFallaDeValidacionDefensiva() throws Exception {
        // GIVEN: Solicitud con violación de contrato Bean Validation (@Positive)
        TransferenciaRequestDto requestInvalido = new TransferenciaRequestDto(
                "001-VAL-0001",
                "001-VAL-0002",
                new BigDecimal("-75.00"),
                "Monto negativo prohibido"
        );

        // WHEN & THEN: Falla con 400 Bad Request antes de entrar a la capa de negocio
        mockMvc.perform(post("/api/v1/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Error de Validación de Entrada"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.type").value(containsString("validation-error")))
                .andExpect(jsonPath("$.errores.monto").exists());
    }
}
