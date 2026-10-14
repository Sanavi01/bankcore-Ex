package com.bankcore.backend.infrastructure.rest.controller;

import com.bankcore.backend.domain.model.RegistroAuditoria;
import com.bankcore.backend.dto.RegistroAuditoriaResponseDto;
import com.bankcore.backend.infrastructure.rest.mapper.AuditoriaRestMapper;
import com.bankcore.backend.service.AuditoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Adaptador Primario REST para Auditoría Forense y AML (Driving Adapter).
 * Expone la trazabilidad de eventos procesados asíncronamente mediante RabbitMQ.
 */
@Tag(name = "Auditoría AML", description = "Consultas de trazabilidad y cumplimiento legal procesadas asíncronamente vía RabbitMQ")
@Slf4j
@RestController
@RequestMapping("/api/v1/auditoria")
@RequiredArgsConstructor
public class AuditoriaController {

    private final AuditoriaService auditoriaService;
    private final AuditoriaRestMapper restMapper;

    @Operation(summary = "Consultar auditoría por ID de transacción", 
               description = "Devuelve el comprobante inmutable generado asíncronamente por el consumidor de RabbitMQ")
    @GetMapping("/transacciones/{idTransaccion}")
    public ResponseEntity<RegistroAuditoriaResponseDto> consultarPorTransaccion(@PathVariable String idTransaccion) {
        RegistroAuditoria registro = auditoriaService.consultarPorIdTransaccion(idTransaccion);
        return ResponseEntity.ok(restMapper.toResponseDto(registro));
    }

    @Operation(summary = "Consultar transacciones por clasificación de riesgo AML",
               description = "Permite a los analistas de cumplimiento filtrar operaciones según su perfil de riesgo (BAJO_RIESGO, MEDIO_RIESGO, ALTO_RIESGO)")
    @GetMapping("/riesgo/{clasificacionRiesgo}")
    public ResponseEntity<List<RegistroAuditoriaResponseDto>> consultarPorRiesgo(@PathVariable String clasificacionRiesgo) {
        List<RegistroAuditoria> registros = auditoriaService.consultarPorRiesgo(clasificacionRiesgo);
        List<RegistroAuditoriaResponseDto> dtos = registros.stream()
                .map(restMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }
}
