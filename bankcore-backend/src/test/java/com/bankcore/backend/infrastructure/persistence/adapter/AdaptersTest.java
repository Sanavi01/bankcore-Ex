package com.bankcore.backend.infrastructure.persistence.adapter;

import com.bankcore.backend.domain.EstadoCuenta;
import com.bankcore.backend.domain.TipoMovimiento;
import com.bankcore.backend.domain.model.Cuenta;
import com.bankcore.backend.domain.model.Tarjeta;
import com.bankcore.backend.domain.model.Transaccion;
import com.bankcore.backend.infrastructure.persistence.entity.CuentaJpaEntity;
import com.bankcore.backend.infrastructure.persistence.entity.TarjetaJpaEntity;
import com.bankcore.backend.infrastructure.persistence.entity.TransaccionJpaEntity;
import com.bankcore.backend.infrastructure.persistence.mapper.CuentaPersistenceMapper;
import com.bankcore.backend.infrastructure.persistence.mapper.TarjetaPersistenceMapper;
import com.bankcore.backend.infrastructure.persistence.mapper.TransaccionPersistenceMapper;
import com.bankcore.backend.infrastructure.persistence.repository.SpringDataCuentaRepository;
import com.bankcore.backend.infrastructure.persistence.repository.SpringDataTarjetaRepository;
import com.bankcore.backend.infrastructure.persistence.repository.SpringDataTransaccionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Pruebas Unitarias de Adaptadores de Persistencia (GoF Adapter).
 * Cobertura de excepciones de cuenta no encontrada y búsquedas por ID.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Adaptadores: Pruebas Unitarias de Persistencia")
class AdaptersTest {

    @Nested
    @DisplayName("TarjetaRepositoryAdapter")
    class TarjetaAdapterRules {

        @Mock
        private SpringDataTarjetaRepository tarjetaRepository;

        @Mock
        private SpringDataCuentaRepository cuentaRepository;

        @Mock
        private TarjetaPersistenceMapper mapper;

        @InjectMocks
        private TarjetaRepositoryAdapter adapter;

        @Test
        @DisplayName("DADO cuenta inexistente al guardar tarjeta, ENTONCES lanza IllegalArgumentException")
        void debeLanzarExcepcionSiCuentaNoExisteAlGuardar() {
            Tarjeta tarjeta = new Tarjeta(1L, "4532", "VISA", "12/28", "123", BigDecimal.TEN, BigDecimal.TEN, EstadoCuenta.ACTIVA, 99L);
            when(cuentaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> adapter.guardar(tarjeta))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Cuenta no encontrada con ID: 99");

            verify(tarjetaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("TransaccionRepositoryAdapter")
    class TransaccionAdapterRules {

        @Mock
        private SpringDataTransaccionRepository transaccionRepository;

        @Mock
        private SpringDataCuentaRepository cuentaRepository;

        @Mock
        private TransaccionPersistenceMapper mapper;

        @InjectMocks
        private TransaccionRepositoryAdapter adapter;

        @Test
        @DisplayName("DADO cuenta inexistente al guardar transacción, ENTONCES lanza IllegalArgumentException")
        void debeLanzarExcepcionSiCuentaNoExisteAlGuardar() {
            Transaccion transaccion = new Transaccion(1L, 99L, BigDecimal.TEN, TipoMovimiento.DEPOSITO, "Abono", LocalDateTime.now(), "EXITOSA");
            when(cuentaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> adapter.guardar(transaccion))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Cuenta no encontrada con ID: 99");

            verify(transaccionRepository, never()).save(any());
        }

        @Test
        @DisplayName("DADO ID de transacción existente, CUANDO se consulta por ID, ENTONCES retorna dominio mapeado")
        void debeBuscarTransaccionPorId() {
            TransaccionJpaEntity entity = TransaccionJpaEntity.builder().id(1L).build();
            Transaccion domain = new Transaccion(1L, 10L, BigDecimal.TEN, TipoMovimiento.DEPOSITO, "Abono", LocalDateTime.now(), "EXITOSA");

            when(transaccionRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(mapper.toDomain(entity)).thenReturn(domain);

            Optional<Transaccion> resultado = adapter.buscarPorId(1L);

            assertThat(resultado).isPresent().contains(domain);
        }
    }
}
