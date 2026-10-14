package com.bankcore.backend.service;

import com.bankcore.backend.domain.EstadoCuenta;
import com.bankcore.backend.domain.model.Tarjeta;
import com.bankcore.backend.domain.port.TarjetaRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas Unitarias de TarjetaService (Fachada GoF).
 * Cobertura de búsquedas, listados por cuenta y bloqueo de seguridad.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Servicio: TarjetaService")
class TarjetaServiceTest {

    @Mock
    private TarjetaRepositoryPort tarjetaRepositoryPort;

    private TarjetaService tarjetaService;

    @BeforeEach
    void setUp() {
        tarjetaService = new TarjetaService(tarjetaRepositoryPort);
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
    @DisplayName("Bloqueo de Tarjetas")
    class BloqueoRules {

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
        @DisplayName("DADO ID inexistente al bloquear, ENTONCES lanza IllegalArgumentException")
        void debeFallarAlBloquearTarjetaInexistente() {
            when(tarjetaRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> tarjetaService.bloquearTarjeta(99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Tarjeta no encontrada con ID: 99");

            verify(tarjetaRepositoryPort, never()).guardar(any());
        }
    }
}
