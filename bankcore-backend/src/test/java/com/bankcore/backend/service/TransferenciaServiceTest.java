package com.bankcore.backend.service;

import com.bankcore.backend.dto.TransferenciaRequestDto;
import com.bankcore.backend.dto.TransferenciaResponseDto;
import com.bankcore.backend.service.usecase.RealizarTransferenciaUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Pruebas Unitarias de TransferenciaService (Fachada GoF).
 * Cobertura de delegación con y sin clave de idempotencia.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Servicio: TransferenciaService (Fachada)")
class TransferenciaServiceTest {

    @Mock
    private RealizarTransferenciaUseCase realizarTransferenciaUseCase;

    private TransferenciaService transferenciaService;

    @BeforeEach
    void setUp() {
        transferenciaService = new TransferenciaService(realizarTransferenciaUseCase);
    }

    @Test
    @DisplayName("DADO request con idempotencyKey, CUANDO se delega a transferir, ENTONCES invoca el caso de uso con la clave")
    void debeDelegarTransferenciaConClave() {
        TransferenciaRequestDto request = new TransferenciaRequestDto("001", "002", BigDecimal.TEN, "Concepto");
        TransferenciaResponseDto response = new TransferenciaResponseDto("tx-1", "001", "002", BigDecimal.TEN, "EXITOSA", LocalDateTime.now(), "Ok");

        when(realizarTransferenciaUseCase.ejecutar("key-1", request)).thenReturn(response);

        TransferenciaResponseDto resultado = transferenciaService.transferir("key-1", request);

        assertThat(resultado).isEqualTo(response);
        verify(realizarTransferenciaUseCase).ejecutar("key-1", request);
    }

    @Test
    @DisplayName("DADO request sin clave (sobrecarga de 1 parámetro), CUANDO se delega a transferir, ENTONCES invoca el caso de uso con clave null")
    void debeDelegarTransferenciaSinClave() {
        TransferenciaRequestDto request = new TransferenciaRequestDto("001", "002", BigDecimal.TEN, "Concepto");
        TransferenciaResponseDto response = new TransferenciaResponseDto("tx-2", "001", "002", BigDecimal.TEN, "EXITOSA", LocalDateTime.now(), "Ok");

        when(realizarTransferenciaUseCase.ejecutar(null, request)).thenReturn(response);

        TransferenciaResponseDto resultado = transferenciaService.transferir(request);

        assertThat(resultado).isEqualTo(response);
        verify(realizarTransferenciaUseCase).ejecutar(null, request);
    }
}
