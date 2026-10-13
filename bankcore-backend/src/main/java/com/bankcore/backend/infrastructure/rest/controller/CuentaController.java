package com.bankcore.backend.infrastructure.rest.controller;

import com.bankcore.backend.domain.model.Cuenta;
import com.bankcore.backend.domain.model.Tarjeta;
import com.bankcore.backend.domain.model.Transaccion;
import com.bankcore.backend.dto.CrearCuentaRequestDto;
import com.bankcore.backend.dto.CuentaResponseDto;
import com.bankcore.backend.dto.MovimientoResponseDto;
import com.bankcore.backend.dto.TarjetaResponseDto;
import com.bankcore.backend.infrastructure.rest.mapper.CuentaRestMapper;
import com.bankcore.backend.infrastructure.rest.mapper.MovimientoRestMapper;
import com.bankcore.backend.infrastructure.rest.mapper.TarjetaRestMapper;
import com.bankcore.backend.service.CuentaService;
import com.bankcore.backend.service.TarjetaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

/**
 * Adaptador Primario (HTTP / REST).
 * Expone la API pública consumida por la SPA de Angular.
 */
@Tag(name = "Cuentas", description = "Endpoints para la administración y consulta de cuentas bancarias")
@RestController
@RequestMapping("/api/v1/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaService cuentaService;
    private final TarjetaService tarjetaService;
    private final CuentaRestMapper restMapper;
    private final TarjetaRestMapper tarjetaRestMapper;
    private final MovimientoRestMapper movimientoRestMapper;

    @Operation(summary = "Obtener cuenta por ID")
    @GetMapping("/{id}")
    public ResponseEntity<CuentaResponseDto> obtenerPorId(@PathVariable Long id) {
        Cuenta cuenta = cuentaService.obtenerPorId(id);
        return ResponseEntity.ok(restMapper.toResponseDto(cuenta));
    }

    @Operation(summary = "Obtener cuenta por número de cuenta")
    @GetMapping("/numero/{numeroCuenta}")
    public ResponseEntity<CuentaResponseDto> obtenerPorNumero(@PathVariable String numeroCuenta) {
        Cuenta cuenta = cuentaService.obtenerPorNumeroCuenta(numeroCuenta);
        return ResponseEntity.ok(restMapper.toResponseDto(cuenta));
    }

    @Operation(summary = "Consultar movimientos contables de la cuenta")
    @GetMapping("/{id}/movimientos")
    public ResponseEntity<List<MovimientoResponseDto>> obtenerMovimientos(@PathVariable Long id) {
        List<Transaccion> transacciones = cuentaService.obtenerMovimientosPorCuentaId(id);
        List<MovimientoResponseDto> dtos = transacciones.stream()
                .map(movimientoRestMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Consultar tarjetas asociadas a la cuenta")
    @GetMapping("/{id}/tarjetas")
    public ResponseEntity<List<TarjetaResponseDto>> obtenerTarjetas(@PathVariable Long id) {
        List<Tarjeta> tarjetas = tarjetaService.obtenerPorCuentaId(id);
        List<TarjetaResponseDto> dtos = tarjetas.stream()
                .map(tarjetaRestMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Crear nueva cuenta bancaria")
    @PostMapping
    public ResponseEntity<CuentaResponseDto> crear(@Valid @RequestBody CrearCuentaRequestDto request) {
        Cuenta cuenta = cuentaService.crearCuenta(
                request.numeroCuenta(),
                request.titular(),
                request.saldoInicial()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(restMapper.toResponseDto(cuenta));
    }
}
