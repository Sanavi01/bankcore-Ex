package com.bankcore.backend.infrastructure.rest.controller;

import com.bankcore.backend.domain.model.Cuenta;
import com.bankcore.backend.dto.CrearCuentaRequestDto;
import com.bankcore.backend.dto.CuentaResponseDto;
import com.bankcore.backend.infrastructure.rest.mapper.CuentaRestMapper;
import com.bankcore.backend.service.CuentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Adaptador Primario (HTTP / REST).
 * Expone la API pública consumida por la SPA de Angular.
 */
@RestController
@RequestMapping("/api/v1/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaService cuentaService;
    private final CuentaRestMapper restMapper;

    @GetMapping("/{id}")
    public ResponseEntity<CuentaResponseDto> obtenerPorId(@PathVariable Long id) {
        Cuenta cuenta = cuentaService.obtenerPorId(id);
        return ResponseEntity.ok(restMapper.toResponseDto(cuenta));
    }

    @GetMapping("/numero/{numeroCuenta}")
    public ResponseEntity<CuentaResponseDto> obtenerPorNumero(@PathVariable String numeroCuenta) {
        Cuenta cuenta = cuentaService.obtenerPorNumeroCuenta(numeroCuenta);
        return ResponseEntity.ok(restMapper.toResponseDto(cuenta));
    }

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
