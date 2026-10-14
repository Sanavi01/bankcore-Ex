package com.bankcore.backend.infrastructure.persistence.adapter;

import com.bankcore.backend.domain.EstadoCuenta;
import com.bankcore.backend.domain.TipoMovimiento;
import com.bankcore.backend.domain.model.Cuenta;
import com.bankcore.backend.domain.model.RegistroAuditoria;
import com.bankcore.backend.domain.model.Tarjeta;
import com.bankcore.backend.domain.model.Transaccion;
import com.bankcore.backend.infrastructure.persistence.entity.CuentaJpaEntity;
import com.bankcore.backend.infrastructure.persistence.entity.RegistroAuditoriaJpaEntity;
import com.bankcore.backend.infrastructure.persistence.entity.TarjetaJpaEntity;
import com.bankcore.backend.infrastructure.persistence.entity.TransaccionJpaEntity;
import com.bankcore.backend.infrastructure.persistence.mapper.CuentaPersistenceMapper;
import com.bankcore.backend.infrastructure.persistence.mapper.RegistroAuditoriaPersistenceMapper;
import com.bankcore.backend.infrastructure.persistence.mapper.TarjetaPersistenceMapper;
import com.bankcore.backend.infrastructure.persistence.mapper.TransaccionPersistenceMapper;
import com.bankcore.backend.infrastructure.persistence.repository.SpringDataCuentaRepository;
import com.bankcore.backend.infrastructure.persistence.repository.SpringDataRegistroAuditoriaRepository;
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
import java.util.List;
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

    @Nested
    @DisplayName("RegistroAuditoriaRepositoryAdapter")
    class RegistroAuditoriaAdapterRules {

        @Mock
        private SpringDataRegistroAuditoriaRepository repository;

        @Mock
        private RegistroAuditoriaPersistenceMapper mapper;

        @InjectMocks
        private RegistroAuditoriaRepositoryAdapter adapter;

        @Test
        @DisplayName("DADO registro de auditoría, CUANDO se guarda, ENTONCES mapea y persiste correctamente")
        void debeGuardarRegistroAuditoria() {
            RegistroAuditoria domain = new RegistroAuditoria(null, "tx-100", "001", "002", BigDecimal.TEN, "BAJO_RIESGO_CONFORME", LocalDateTime.now(), LocalDateTime.now(), "RABBITMQ_ASYNC");
            RegistroAuditoriaJpaEntity entity = RegistroAuditoriaJpaEntity.builder().id(10L).idTransaccion("tx-100").build();
            RegistroAuditoria domainGuardado = new RegistroAuditoria(10L, "tx-100", "001", "002", BigDecimal.TEN, "BAJO_RIESGO_CONFORME", LocalDateTime.now(), LocalDateTime.now(), "RABBITMQ_ASYNC");

            when(mapper.toEntity(domain)).thenReturn(entity);
            when(repository.save(entity)).thenReturn(entity);
            when(mapper.toDomain(entity)).thenReturn(domainGuardado);

            RegistroAuditoria resultado = adapter.guardar(domain);

            assertThat(resultado).isEqualTo(domainGuardado);
            verify(repository).save(entity);
        }

        @Test
        @DisplayName("DADO idTransaccion existente, CUANDO se busca, ENTONCES retorna Optional con dominio")
        void debeBuscarPorIdTransaccion() {
            RegistroAuditoriaJpaEntity entity = RegistroAuditoriaJpaEntity.builder().id(10L).idTransaccion("tx-100").build();
            RegistroAuditoria domain = new RegistroAuditoria(10L, "tx-100", "001", "002", BigDecimal.TEN, "BAJO_RIESGO_CONFORME", LocalDateTime.now(), LocalDateTime.now(), "RABBITMQ_ASYNC");

            when(repository.findByIdTransaccion("tx-100")).thenReturn(Optional.of(entity));
            when(mapper.toDomain(entity)).thenReturn(domain);

            Optional<RegistroAuditoria> resultado = adapter.buscarPorIdTransaccion("tx-100");

            assertThat(resultado).isPresent().contains(domain);
        }

        @Test
        @DisplayName("DADO clasificacionRiesgo, CUANDO se busca, ENTONCES retorna lista de dominios mapeados")
        void debeBuscarPorClasificacionRiesgo() {
            RegistroAuditoriaJpaEntity entity = RegistroAuditoriaJpaEntity.builder().id(10L).clasificacionRiesgo("ALERTA_AML_REVISION").build();
            RegistroAuditoria domain = new RegistroAuditoria(10L, "tx-100", "001", "002", BigDecimal.TEN, "ALERTA_AML_REVISION", LocalDateTime.now(), LocalDateTime.now(), "RABBITMQ_ASYNC");

            when(repository.findByClasificacionRiesgo("ALERTA_AML_REVISION")).thenReturn(List.of(entity));
            when(mapper.toDomain(entity)).thenReturn(domain);

            List<RegistroAuditoria> resultado = adapter.buscarPorClasificacionRiesgo("ALERTA_AML_REVISION");

            assertThat(resultado).hasSize(1).contains(domain);
        }
    }
}
