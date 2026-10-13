package com.bankcore.backend.infrastructure.rest.controller;

import com.bankcore.backend.domain.model.Tarjeta;
import com.bankcore.backend.dto.TarjetaResponseDto;
import com.bankcore.backend.infrastructure.rest.mapper.TarjetaRestMapper;
import com.bankcore.backend.service.TarjetaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Adaptador Primario REST para el Subdominio de Tarjetas (Driving Adapter).
 * Expone endpoints con semántica HTTP para consulta y bloqueo de tarjetas.
 */
@Tag(name = "Tarjetas", description = "Operaciones de consulta y bloqueo preventivo de tarjetas")
@Slf4j
@RestController
@RequestMapping("/api/v1/tarjetas")
@RequiredArgsConstructor
public class TarjetaController {

    private final TarjetaService tarjetaService;
    private final TarjetaRestMapper restMapper;

    @Operation(summary = "Obtener tarjeta por ID")
    @GetMapping("/{id}")
    public ResponseEntity<TarjetaResponseDto> obtenerPorId(@PathVariable Long id) {
        Tarjeta tarjeta = tarjetaService.obtenerPorId(id);
        return ResponseEntity.ok(restMapper.toResponseDto(tarjeta));
    }

    @Operation(summary = "Obtener tarjetas de una cuenta")
    @GetMapping("/cuenta/{cuentaId}")
    public ResponseEntity<List<TarjetaResponseDto>> obtenerPorCuentaId(@PathVariable Long cuentaId) {
        List<Tarjeta> tarjetas = tarjetaService.obtenerPorCuentaId(cuentaId);
        List<TarjetaResponseDto> dtos = tarjetas.stream()
                .map(restMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    /**
     * Endpoint idempotente para bloquear una tarjeta por seguridad (RFC 9110).
     * Se utiliza PUT porque muta el estado hacia un valor determinista (BLOQUEADA).
     */
    @Operation(summary = "Bloquear tarjeta por seguridad", description = "Cambia el estado de la tarjeta a BLOQUEADA de forma idempotente")
    @PutMapping("/{id}/bloquear")
    public ResponseEntity<TarjetaResponseDto> bloquear(@PathVariable Long id) {
        log.info("REST: Solicitud recibida para bloquear tarjeta con ID [{}]", id);
        Tarjeta tarjetaBloqueada = tarjetaService.bloquearTarjeta(id);
        return ResponseEntity.ok(restMapper.toResponseDto(tarjetaBloqueada));
    }
}
