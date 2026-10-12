package com.bankcore.backend.service;

import com.bankcore.backend.dto.TransferenciaRequestDto;
import com.bankcore.backend.dto.TransferenciaResponseDto;
import com.bankcore.backend.service.usecase.RealizarTransferenciaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Servicio de Aplicación / Fachada para el Subdominio de Transferencias (GoF Façade Pattern).
 * Actúa como punto de entrada cohesivo para la orquestación de transferencias interbancarias.
 * 
 * Responsabilidad Única (SRP):
 * - Representar la fachada del módulo de Transferencias.
 * - Delegar la ejecución transaccional e idempotente a RealizarTransferenciaUseCase.
 */
@Service
@RequiredArgsConstructor
public class TransferenciaService {

    private final RealizarTransferenciaUseCase realizarTransferenciaUseCase;

    /**
     * Procesa una transferencia bancaria con soporte de idempotencia.
     */
    public TransferenciaResponseDto transferir(String idempotencyKey, TransferenciaRequestDto request) {
        return realizarTransferenciaUseCase.ejecutar(idempotencyKey, request);
    }

    /**
     * Sobrecarga para transferencias sin clave de idempotencia explícita.
     */
    public TransferenciaResponseDto transferir(TransferenciaRequestDto request) {
        return realizarTransferenciaUseCase.ejecutar(null, request);
    }
}
