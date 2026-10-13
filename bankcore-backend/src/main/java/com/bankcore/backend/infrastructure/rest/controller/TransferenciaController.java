package com.bankcore.backend.infrastructure.rest.controller;

import com.bankcore.backend.dto.TransferenciaRequestDto;
import com.bankcore.backend.dto.TransferenciaResponseDto;
import com.bankcore.backend.service.TransferenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Adaptador Primario REST (Driving Adapter).
 * Expone el endpoint HTTP para transferencias bancarias.
 * 
 * Responsabilidad Única (SRP):
 * 1. Desempaquetar la petición HTTP (Headers y Body).
 * 2. Activar la validación defensiva con @Valid (Fail-Fast).
 * 3. Delegar al Servicio de Aplicación (Fachada TransferenciaService).
 * 4. Retornar la respuesta con el código de estado HTTP 201 Created.
 */
@Tag(name = "Transferencias", description = "Motor transaccional atómico (ACID) con soporte de Idempotencia")
@Slf4j
@RestController
@RequestMapping("/api/v1/transferencias")
@RequiredArgsConstructor
public class TransferenciaController {

    private final TransferenciaService transferenciaService;

    /**
     * Procesa una transferencia bancaria atómica e idempotente.
     *
     * @param idempotencyKey Clave UUID enviada en la cabecera 'Idempotency-Key' para evitar dobles cobros.
     * @param request Datos de la transferencia validados defensivamente con Bean Validation.
     * @return 201 Created con el comprobante inmutable de la transacción.
     */
    @Operation(summary = "Realizar transferencia interbancaria", description = "Ejecuta una transferencia atómica entre cuentas aplicando verificación de idempotencia")
    @PostMapping
    public ResponseEntity<TransferenciaResponseDto> transferir(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody TransferenciaRequestDto request) {

        log.info("REST: Solicitud de transferencia recibida. Origen: [{}], Destino: [{}], Monto: [{}], Idempotency-Key: [{}]",
                request.cuentaOrigen(), request.cuentaDestino(), request.monto(), idempotencyKey);

        TransferenciaResponseDto respuesta = transferenciaService.transferir(idempotencyKey, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }
}
