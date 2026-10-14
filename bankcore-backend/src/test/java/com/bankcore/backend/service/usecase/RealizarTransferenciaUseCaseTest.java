package com.bankcore.backend.service.usecase;

import com.bankcore.backend.domain.EstadoCuenta;
import com.bankcore.backend.domain.model.Cuenta;
import com.bankcore.backend.domain.model.Transaccion;
import com.bankcore.backend.domain.port.CuentaRepositoryPort;
import com.bankcore.backend.domain.port.TransaccionRepositoryPort;
import com.bankcore.backend.dto.TransferenciaRequestDto;
import com.bankcore.backend.dto.TransferenciaResponseDto;
import com.bankcore.backend.service.IdempotenciaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas Unitarias del Caso de Uso RealizarTransferenciaUseCase.
 * Cobertura completa de ramas: Idempotencia, validaciones de cuentas y balance contable.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Caso de Uso: RealizarTransferenciaUseCase")
class RealizarTransferenciaUseCaseTest {

    @Mock
    private CuentaRepositoryPort cuentaRepositoryPort;

    @Mock
    private TransaccionRepositoryPort transaccionRepositoryPort;

    @Mock
    private IdempotenciaService idempotenciaService;

    private RealizarTransferenciaUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RealizarTransferenciaUseCase(cuentaRepositoryPort, transaccionRepositoryPort, idempotenciaService);
    }

    @Nested
    @DisplayName("Reglas de Idempotencia")
    class IdempotencyRules {

        @Test
        @DisplayName("DADO Idempotency-Key repetido, ENTONCES retorna respuesta previa sin reejecutar lógica contable")
        void debeRetornarRespuestaPreviaCuandoExisteClave() {
            String key = "idemp-key-123";
            TransferenciaRequestDto request = new TransferenciaRequestDto(
                    "001-ORIGEN", "002-DESTINO", new BigDecimal("100.00"), "Pago"
            );
            TransferenciaResponseDto respuestaCacheada = new TransferenciaResponseDto(
                    "tx-uuid", "001-ORIGEN", "002-DESTINO", new BigDecimal("100.00"),
                    "EXITOSA", LocalDateTime.now(), "Transferencia previa"
            );

            when(idempotenciaService.obtenerRespuestaPrevia(key, TransferenciaResponseDto.class))
                    .thenReturn(Optional.of(respuestaCacheada));

            TransferenciaResponseDto resultado = useCase.ejecutar(key, request);

            assertThat(resultado).isEqualTo(respuestaCacheada);
            verifyNoInteractions(cuentaRepositoryPort);
            verifyNoInteractions(transaccionRepositoryPort);
        }
    }

    @Nested
    @DisplayName("Validaciones de Negocio y Búsqueda")
    class ValidationRules {

        @Test
        @DisplayName("DADO cuenta origen igual a cuenta destino, ENTONCES lanza IllegalArgumentException")
        void debeFallarSiCuentasSonIguales() {
            TransferenciaRequestDto request = new TransferenciaRequestDto(
                    "001-ORIGEN", "001-ORIGEN", new BigDecimal("100.00"), "Autotransferencia"
            );

            assertThatThrownBy(() -> useCase.ejecutar(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("no pueden ser iguales");
        }

        @Test
        @DisplayName("DADO cuenta origen inexistente, ENTONCES lanza IllegalArgumentException")
        void debeFallarSiCuentaOrigenNoExiste() {
            TransferenciaRequestDto request = new TransferenciaRequestDto(
                    "999-INEXISTENTE", "002-DESTINO", new BigDecimal("100.00"), "Pago"
            );

            when(cuentaRepositoryPort.buscarPorNumeroCuenta("999-INEXISTENTE")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.ejecutar(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Cuenta de origen no encontrada");
        }

        @Test
        @DisplayName("DADO cuenta destino inexistente, ENTONCES lanza IllegalArgumentException")
        void debeFallarSiCuentaDestinoNoExiste() {
            TransferenciaRequestDto request = new TransferenciaRequestDto(
                    "001-ORIGEN", "999-INEXISTENTE", new BigDecimal("100.00"), "Pago"
            );

            Cuenta origen = new Cuenta(1L, "001-ORIGEN", "Titular 1", new BigDecimal("1000.00"), EstadoCuenta.ACTIVA, LocalDateTime.now());
            when(cuentaRepositoryPort.buscarPorNumeroCuenta("001-ORIGEN")).thenReturn(Optional.of(origen));
            when(cuentaRepositoryPort.buscarPorNumeroCuenta("999-INEXISTENTE")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.ejecutar(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Cuenta de destino no encontrada");
        }
    }

    @Nested
    @DisplayName("Ejecución Exitosa y Asientos Contables")
    class SuccessExecutionRules {

        @Test
        @DisplayName("DADO transferencia válida con Idempotency-Key y concepto personalizado, ENTONCES procesa saldos y registra idempotencia")
        void debeEjecutarTransferenciaConConceptoYClave() {
            String key = "idemp-key-456";
            TransferenciaRequestDto request = new TransferenciaRequestDto(
                    "001-ORIGEN", "002-DESTINO", new BigDecimal("250.00"), "Pago de factura"
            );

            Cuenta origen = new Cuenta(1L, "001-ORIGEN", "Titular 1", new BigDecimal("1000.00"), EstadoCuenta.ACTIVA, LocalDateTime.now());
            Cuenta destino = new Cuenta(2L, "002-DESTINO", "Titular 2", new BigDecimal("300.00"), EstadoCuenta.ACTIVA, LocalDateTime.now());

            when(idempotenciaService.obtenerRespuestaPrevia(key, TransferenciaResponseDto.class)).thenReturn(Optional.empty());
            when(cuentaRepositoryPort.buscarPorNumeroCuenta("001-ORIGEN")).thenReturn(Optional.of(origen));
            when(cuentaRepositoryPort.buscarPorNumeroCuenta("002-DESTINO")).thenReturn(Optional.of(destino));

            TransferenciaResponseDto resultado = useCase.ejecutar(key, request);

            assertThat(resultado.estado()).isEqualTo("EXITOSA");
            assertThat(resultado.monto()).isEqualByComparingTo(new BigDecimal("250.00"));
            assertThat(origen.getSaldo()).isEqualByComparingTo(new BigDecimal("750.00"));
            assertThat(destino.getSaldo()).isEqualByComparingTo(new BigDecimal("550.00"));

            verify(cuentaRepositoryPort).guardar(origen);
            verify(cuentaRepositoryPort).guardar(destino);

            org.mockito.ArgumentCaptor<Transaccion> txCaptor = org.mockito.ArgumentCaptor.forClass(Transaccion.class);
            verify(transaccionRepositoryPort, times(2)).guardar(txCaptor.capture());
            assertThat(txCaptor.getAllValues().get(0).getConcepto()).contains("Pago de factura");
            assertThat(txCaptor.getAllValues().get(1).getConcepto()).contains("Pago de factura");

            verify(idempotenciaService).registrarRespuesta(eq(key), eq("TRANSFERENCIA"), eq(200), any(TransferenciaResponseDto.class));
        }

        @Test
        @DisplayName("DADO transferencia sin clave y con concepto nulo o vacío, ENTONCES asigna concepto por defecto y no registra idempotencia")
        void debeEjecutarTransferenciaSinClaveYConceptoPorDefecto() {
            TransferenciaRequestDto request = new TransferenciaRequestDto(
                    "001-ORIGEN", "002-DESTINO", new BigDecimal("100.00"), "   "
            );

            Cuenta origen = new Cuenta(1L, "001-ORIGEN", "Titular 1", new BigDecimal("500.00"), EstadoCuenta.ACTIVA, LocalDateTime.now());
            Cuenta destino = new Cuenta(2L, "002-DESTINO", "Titular 2", new BigDecimal("100.00"), EstadoCuenta.ACTIVA, LocalDateTime.now());

            when(cuentaRepositoryPort.buscarPorNumeroCuenta("001-ORIGEN")).thenReturn(Optional.of(origen));
            when(cuentaRepositoryPort.buscarPorNumeroCuenta("002-DESTINO")).thenReturn(Optional.of(destino));

            TransferenciaResponseDto resultado = useCase.ejecutar(null, request);

            assertThat(resultado.estado()).isEqualTo("EXITOSA");
            assertThat(origen.getSaldo()).isEqualByComparingTo(new BigDecimal("400.00"));
            assertThat(destino.getSaldo()).isEqualByComparingTo(new BigDecimal("200.00"));

            org.mockito.ArgumentCaptor<Transaccion> txCaptor = org.mockito.ArgumentCaptor.forClass(Transaccion.class);
            verify(transaccionRepositoryPort, times(2)).guardar(txCaptor.capture());
            assertThat(txCaptor.getAllValues().get(0).getConcepto()).contains("Transferencia interbancaria");
            assertThat(txCaptor.getAllValues().get(1).getConcepto()).contains("Transferencia interbancaria");

            verify(idempotenciaService, never()).registrarRespuesta(anyString(), anyString(), anyInt(), any());
        }

        @Test
        @DisplayName("DADO sobrecarga ejecutar(request) con concepto nulo y clave en blanco, ENTONCES procesa exitosamente")
        void debeEjecutarConSobrecargaUnParametroYConceptoNulo() {
            TransferenciaRequestDto request = new TransferenciaRequestDto(
                    "001-ORIGEN", "002-DESTINO", new BigDecimal("50.00"), null
            );

            Cuenta origen = new Cuenta(1L, "001-ORIGEN", "Titular 1", new BigDecimal("500.00"), EstadoCuenta.ACTIVA, LocalDateTime.now());
            Cuenta destino = new Cuenta(2L, "002-DESTINO", "Titular 2", new BigDecimal("100.00"), EstadoCuenta.ACTIVA, LocalDateTime.now());

            when(cuentaRepositoryPort.buscarPorNumeroCuenta("001-ORIGEN")).thenReturn(Optional.of(origen));
            when(cuentaRepositoryPort.buscarPorNumeroCuenta("002-DESTINO")).thenReturn(Optional.of(destino));

            // Probar sobrecarga ejecutar(request)
            TransferenciaResponseDto resultado = useCase.ejecutar(request);

            assertThat(resultado.estado()).isEqualTo("EXITOSA");
            assertThat(origen.getSaldo()).isEqualByComparingTo(new BigDecimal("450.00"));
            assertThat(destino.getSaldo()).isEqualByComparingTo(new BigDecimal("150.00"));

            // Probar también con clave de idempotencia en blanco ("   ")
            useCase.ejecutar("   ", request);
            verify(idempotenciaService, never()).registrarRespuesta(anyString(), anyString(), anyInt(), any());
        }
    }
}
