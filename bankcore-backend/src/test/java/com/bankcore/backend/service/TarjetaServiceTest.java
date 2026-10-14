package com.bankcore.backend.service;

import com.bankcore.backend.domain.EstadoCuenta;
import com.bankcore.backend.domain.model.Cuenta;
import com.bankcore.backend.domain.model.Tarjeta;
import com.bankcore.backend.domain.port.CuentaRepositoryPort;
import com.bankcore.backend.domain.port.TarjetaRepositoryPort;
import com.bankcore.backend.dto.CrearTarjetaRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas Unitarias de TarjetaService (Fachada GoF).
 * Cobertura de emisión, búsquedas, listados por cuenta, bloqueo y desbloqueo.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Servicio: TarjetaService")
class TarjetaServiceTest {

    @Mock
    private TarjetaRepositoryPort tarjetaRepositoryPort;

    @Mock
    private CuentaRepositoryPort cuentaRepositoryPort;

    private TarjetaService tarjetaService;

    @BeforeEach
    void setUp() {
        tarjetaService = new TarjetaService(tarjetaRepositoryPort, cuentaRepositoryPort);
    }

    @Nested
    @DisplayName("Emisión de Tarjetas")
    class EmisionRules {

        @Test
        @DisplayName("DADO cuenta activa, CUANDO se emite tarjeta VISA, ENTONCES genera datos válidos y persiste")
        void debeEmitirTarjetaExitosamente() {
            Cuenta cuenta = new Cuenta(1L, "001-ORIGEN", "SANTIAGO", new BigDecimal("1000.00"), EstadoCuenta.ACTIVA, LocalDateTime.now());
            when(cuentaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(cuenta));
            when(tarjetaRepositoryPort.guardar(any(Tarjeta.class))).thenAnswer(invocation -> invocation.getArgument(0));

            CrearTarjetaRequestDto request = new CrearTarjetaRequestDto(1L, "VISA", new BigDecimal("5000.00"));
            Tarjeta emitida = tarjetaService.emitirTarjeta(request);

            assertThat(emitida).isNotNull();
            assertThat(emitida.getFranquicia()).isEqualTo("VISA");
            assertThat(emitida.getNumeroTarjeta()).startsWith("4532");
            assertThat(emitida.getCupoTotal()).isEqualByComparingTo(new BigDecimal("5000.00"));
            assertThat(emitida.getEstado()).isEqualTo(EstadoCuenta.ACTIVA);
            assertThat(emitida.getCuentaId()).isEqualTo(1L);
            verify(tarjetaRepositoryPort).guardar(any(Tarjeta.class));
        }

        @Test
        @DisplayName("DADO cuenta inactiva o bloqueada, CUANDO se emite tarjeta, ENTONCES lanza IllegalStateException")
        void debeRechazarEmisionEnCuentaBloqueada() {
            Cuenta cuentaBloqueada = new Cuenta(1L, "001-ORIGEN", "SANTIAGO", new BigDecimal("1000.00"), EstadoCuenta.BLOQUEADA, LocalDateTime.now());
            when(cuentaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(cuentaBloqueada));

            CrearTarjetaRequestDto request = new CrearTarjetaRequestDto(1L, "MASTERCARD", new BigDecimal("5000.00"));

            assertThatThrownBy(() -> tarjetaService.emitirTarjeta(request))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("No se pueden emitir tarjetas asociadas a una cuenta inactiva");

            verify(tarjetaRepositoryPort, never()).guardar(any());
        }

        @Test
        @DisplayName("DADO cuenta inexistente, CUANDO se emite tarjeta, ENTONCES lanza IllegalArgumentException")
        void debeRechazarEmisionEnCuentaInexistente() {
            when(cuentaRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

            CrearTarjetaRequestDto request = new CrearTarjetaRequestDto(99L, "AMEX", new BigDecimal("5000.00"));

            assertThatThrownBy(() -> tarjetaService.emitirTarjeta(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("No existe la cuenta con ID: 99");

            verify(tarjetaRepositoryPort, never()).guardar(any());
        }

        @Test
        @DisplayName("DADO franquicias MASTERCARD, AMEX y DEFAULT, ENTONCES genera prefijos correspondientes")
        void debeEmitirConDiferentesFranquicias() {
            Cuenta cuenta = new Cuenta(1L, "001-ORIGEN", "SANTIAGO", new BigDecimal("1000.00"), EstadoCuenta.ACTIVA, LocalDateTime.now());
            when(cuentaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(cuenta));
            when(tarjetaRepositoryPort.guardar(any(Tarjeta.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // MASTERCARD
            Tarjeta mc = tarjetaService.emitirTarjeta(new CrearTarjetaRequestDto(1L, "MASTERCARD", new BigDecimal("2000.00")));
            assertThat(mc.getNumeroTarjeta()).startsWith("5412");

            // AMEX
            Tarjeta amex = tarjetaService.emitirTarjeta(new CrearTarjetaRequestDto(1L, "AMEX", new BigDecimal("3000.00")));
            assertThat(amex.getNumeroTarjeta()).startsWith("3712");

            // DEFAULT
            Tarjeta defaultCard = tarjetaService.emitirTarjeta(new CrearTarjetaRequestDto(1L, "DINERS", new BigDecimal("4000.00")));
            assertThat(defaultCard.getNumeroTarjeta()).startsWith("4000");
        }
    }

    @Nested
    @DisplayName("Consultas de Tarjetas")
    class ConsultaRules {

        @Test
        @DisplayName("DADO un ID existente, CUANDO se consulta por ID, ENTONCES retorna la tarjeta")
        void debeRetornarTarjetaCuandoExistePorId() {
            Tarjeta tarjeta = new Tarjeta(1L, "4532-XXXX", "VISA", "12/28", "123", BigDecimal.TEN, BigDecimal.TEN, EstadoCuenta.ACTIVA, 5L);
            when(tarjetaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(tarjeta));

            Tarjeta resultado = tarjetaService.obtenerPorId(1L);

            assertThat(resultado).isEqualTo(tarjeta);
        }

        @Test
        @DisplayName("DADO un ID inexistente, CUANDO se consulta por ID, ENTONCES lanza IllegalArgumentException")
        void debeLanzarExcepcionCuandoNoExistePorId() {
            when(tarjetaRepositoryPort.buscarPorId(88L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> tarjetaService.obtenerPorId(88L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Tarjeta no encontrada con ID: 88");
        }

        @Test
        @DisplayName("DADO un cuentaId, CUANDO se consulta por cuenta, ENTONCES retorna la lista asociada")
        void debeRetornarListaDeTarjetasPorCuentaId() {
            List<Tarjeta> tarjetas = List.of(
                    new Tarjeta(1L, "4532-XXXX", "VISA", "12/28", "123", BigDecimal.TEN, BigDecimal.TEN, EstadoCuenta.ACTIVA, 10L)
            );
            when(tarjetaRepositoryPort.buscarPorCuentaId(10L)).thenReturn(tarjetas);

            List<Tarjeta> resultado = tarjetaService.obtenerPorCuentaId(10L);

            assertThat(resultado).isEqualTo(tarjetas);
        }
    }

    @Nested
    @DisplayName("Bloqueo y Desbloqueo de Tarjetas")
    class CicloVidaRules {

        @Test
        @DisplayName("DADO tarjeta activa existente, CUANDO se solicita bloqueo, ENTONCES bloquea y persiste")
        void debeBloquearTarjetaActiva() {
            Tarjeta tarjetaActiva = new Tarjeta(1L, "4532-XXXX", "VISA", "12/28", "123", BigDecimal.TEN, BigDecimal.TEN, EstadoCuenta.ACTIVA, 5L);
            when(tarjetaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(tarjetaActiva));
            when(tarjetaRepositoryPort.guardar(any(Tarjeta.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Tarjeta bloqueada = tarjetaService.bloquearTarjeta(1L);

            assertThat(bloqueada.getEstado()).isEqualTo(EstadoCuenta.BLOQUEADA);
            verify(tarjetaRepositoryPort).guardar(tarjetaActiva);
        }

        @Test
        @DisplayName("DADO tarjeta bloqueada, CUANDO se solicita desbloqueo, ENTONCES reactiva y persiste")
        void debeDesbloquearTarjetaBloqueada() {
            Tarjeta tarjetaBloqueada = new Tarjeta(1L, "4532-XXXX", "VISA", "12/28", "123", BigDecimal.TEN, BigDecimal.TEN, EstadoCuenta.BLOQUEADA, 5L);
            when(tarjetaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(tarjetaBloqueada));
            when(tarjetaRepositoryPort.guardar(any(Tarjeta.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Tarjeta reactivada = tarjetaService.desbloquearTarjeta(1L);

            assertThat(reactivada.getEstado()).isEqualTo(EstadoCuenta.ACTIVA);
            verify(tarjetaRepositoryPort).guardar(tarjetaBloqueada);
        }

        @Test
        @DisplayName("DADO ID inexistente al bloquear, ENTONCES lanza IllegalArgumentException")
        void debeFallarAlBloquearTarjetaInexistente() {
            when(tarjetaRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> tarjetaService.bloquearTarjeta(99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Tarjeta no encontrada con ID: 99");

            verify(tarjetaRepositoryPort, never()).guardar(any());
        }

        @Test
        @DisplayName("DADO ID inexistente al desbloquear, ENTONCES lanza IllegalArgumentException")
        void debeFallarAlDesbloquearTarjetaInexistente() {
            when(tarjetaRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> tarjetaService.desbloquearTarjeta(99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Tarjeta no encontrada con ID: 99");

            verify(tarjetaRepositoryPort, never()).guardar(any());
        }
    }
}
