package com.bankcore.backend.infrastructure.mapper;

import com.bankcore.backend.domain.EstadoCuenta;
import com.bankcore.backend.domain.TipoMovimiento;
import com.bankcore.backend.domain.model.Cuenta;
import com.bankcore.backend.domain.model.Tarjeta;
import com.bankcore.backend.domain.model.Transaccion;
import com.bankcore.backend.dto.CuentaResponseDto;
import com.bankcore.backend.dto.MovimientoResponseDto;
import com.bankcore.backend.dto.TarjetaResponseDto;
import com.bankcore.backend.infrastructure.persistence.entity.CuentaJpaEntity;
import com.bankcore.backend.infrastructure.persistence.entity.TarjetaJpaEntity;
import com.bankcore.backend.infrastructure.persistence.entity.TransaccionJpaEntity;
import com.bankcore.backend.infrastructure.persistence.mapper.CuentaPersistenceMapper;
import com.bankcore.backend.infrastructure.persistence.mapper.TarjetaPersistenceMapper;
import com.bankcore.backend.infrastructure.persistence.mapper.TransaccionPersistenceMapper;
import com.bankcore.backend.infrastructure.rest.mapper.CuentaRestMapper;
import com.bankcore.backend.infrastructure.rest.mapper.MovimientoRestMapper;
import com.bankcore.backend.infrastructure.rest.mapper.TarjetaRestMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas Unitarias de Mappers (Persistencia y REST).
 * Cobertura exhaustiva de null-safety, enmascaramiento PCI-DSS y transformaciones bidireccionales.
 */
@DisplayName("Infraestructura: Pruebas Unitarias de Mappers")
class MappersTest {

    private final CuentaPersistenceMapper cuentaPersistenceMapper = new CuentaPersistenceMapper();
    private final TarjetaPersistenceMapper tarjetaPersistenceMapper = new TarjetaPersistenceMapper();
    private final TransaccionPersistenceMapper transaccionPersistenceMapper = new TransaccionPersistenceMapper();

    private final CuentaRestMapper cuentaRestMapper = new CuentaRestMapper();
    private final TarjetaRestMapper tarjetaRestMapper = new TarjetaRestMapper();
    private final MovimientoRestMapper movimientoRestMapper = new MovimientoRestMapper();

    @Nested
    @DisplayName("Mappers de Persistencia")
    class PersistenceMappers {

        @Test
        @DisplayName("CuentaPersistenceMapper: maneja nulls y mapea correctamente en ambas direcciones")
        void cuentaPersistenceMapperDebeManejarNullsYConversiones() {
            assertThat(cuentaPersistenceMapper.toDomain(null)).isNull();
            assertThat(cuentaPersistenceMapper.toEntity(null)).isNull();

            CuentaJpaEntity entity = CuentaJpaEntity.builder()
                    .id(1L)
                    .numeroCuenta("001-TEST")
                    .titular("Titular")
                    .saldo(new BigDecimal("1000.00"))
                    .estado(EstadoCuenta.ACTIVA)
                    .fechaCreacion(LocalDateTime.now())
                    .build();

            Cuenta domain = cuentaPersistenceMapper.toDomain(entity);
            assertThat(domain).isNotNull();
            assertThat(domain.getId()).isEqualTo(1L);
            assertThat(domain.getNumeroCuenta()).isEqualTo("001-TEST");

            CuentaJpaEntity reconvertida = cuentaPersistenceMapper.toEntity(domain);
            assertThat(reconvertida).isNotNull();
            assertThat(reconvertida.getId()).isEqualTo(1L);
            assertThat(reconvertida.getNumeroCuenta()).isEqualTo("001-TEST");
        }

        @Test
        @DisplayName("TarjetaPersistenceMapper: maneja nulls, entidad cuenta null y mapea en ambas direcciones")
        void tarjetaPersistenceMapperDebeManejarNullsYConversiones() {
            assertThat(tarjetaPersistenceMapper.toDomain(null)).isNull();
            assertThat(tarjetaPersistenceMapper.toEntity(null, null)).isNull();

            // Caso cuenta null en entity
            TarjetaJpaEntity entitySinCuenta = TarjetaJpaEntity.builder()
                    .id(10L)
                    .numeroTarjeta("4532-1111-2222-3333")
                    .franquicia("VISA")
                    .fechaVencimiento("12/28")
                    .cvv("123")
                    .cupoTotal(new BigDecimal("5000.00"))
                    .cupoDisponible(new BigDecimal("4000.00"))
                    .estado(EstadoCuenta.ACTIVA)
                    .cuenta(null)
                    .build();

            Tarjeta domainSinCuenta = tarjetaPersistenceMapper.toDomain(entitySinCuenta);
            assertThat(domainSinCuenta).isNotNull();
            assertThat(domainSinCuenta.getCuentaId()).isNull();

            // Caso cuenta presente en entity
            CuentaJpaEntity cuentaEntity = CuentaJpaEntity.builder().id(99L).build();
            TarjetaJpaEntity entityConCuenta = TarjetaJpaEntity.builder()
                    .id(10L)
                    .numeroTarjeta("4532-1111-2222-3333")
                    .franquicia("VISA")
                    .fechaVencimiento("12/28")
                    .cvv("123")
                    .cupoTotal(new BigDecimal("5000.00"))
                    .cupoDisponible(new BigDecimal("4000.00"))
                    .estado(EstadoCuenta.ACTIVA)
                    .cuenta(cuentaEntity)
                    .build();

            Tarjeta domain = tarjetaPersistenceMapper.toDomain(entityConCuenta);
            assertThat(domain.getCuentaId()).isEqualTo(99L);

            TarjetaJpaEntity reconvertida = tarjetaPersistenceMapper.toEntity(domain, cuentaEntity);
            assertThat(reconvertida).isNotNull();
            assertThat(reconvertida.getCuenta().getId()).isEqualTo(99L);
        }

        @Test
        @DisplayName("TransaccionPersistenceMapper: maneja nulls, entidad cuenta null y mapea en ambas direcciones")
        void transaccionPersistenceMapperDebeManejarNullsYConversiones() {
            assertThat(transaccionPersistenceMapper.toDomain(null)).isNull();
            assertThat(transaccionPersistenceMapper.toEntity(null, null)).isNull();

            // Caso cuenta null
            TransaccionJpaEntity entitySinCuenta = TransaccionJpaEntity.builder()
                    .id(50L)
                    .monto(new BigDecimal("200.00"))
                    .tipo(TipoMovimiento.DEPOSITO)
                    .concepto("Abono")
                    .fecha(LocalDateTime.now())
                    .estado("EXITOSA")
                    .cuenta(null)
                    .build();

            Transaccion domainSinCuenta = transaccionPersistenceMapper.toDomain(entitySinCuenta);
            assertThat(domainSinCuenta.getCuentaId()).isNull();

            // Caso cuenta presente
            CuentaJpaEntity cuentaEntity = CuentaJpaEntity.builder().id(88L).build();
            TransaccionJpaEntity entityConCuenta = TransaccionJpaEntity.builder()
                    .id(50L)
                    .monto(new BigDecimal("200.00"))
                    .tipo(TipoMovimiento.DEPOSITO)
                    .concepto("Abono")
                    .fecha(LocalDateTime.now())
                    .estado("EXITOSA")
                    .cuenta(cuentaEntity)
                    .build();

            Transaccion domain = transaccionPersistenceMapper.toDomain(entityConCuenta);
            assertThat(domain.getCuentaId()).isEqualTo(88L);

            TransaccionJpaEntity reconvertida = transaccionPersistenceMapper.toEntity(domain, cuentaEntity);
            assertThat(reconvertida).isNotNull();
            assertThat(reconvertida.getCuenta().getId()).isEqualTo(88L);
        }
    }

    @Nested
    @DisplayName("Mappers REST")
    class RestMappers {

        @Test
        @DisplayName("CuentaRestMapper: maneja nulls y mapea DTO")
        void cuentaRestMapperDebeManejarNulls() {
            assertThat(cuentaRestMapper.toResponseDto(null)).isNull();

            Cuenta cuenta = new Cuenta(1L, "001-TEST", "Titular", BigDecimal.TEN, EstadoCuenta.ACTIVA, LocalDateTime.now());
            CuentaResponseDto dto = cuentaRestMapper.toResponseDto(cuenta);

            assertThat(dto).isNotNull();
            assertThat(dto.numeroCuenta()).isEqualTo("001-TEST");
        }

        @Test
        @DisplayName("TarjetaRestMapper: maneja nulls, número corto (<4), número nulo y enmascaramiento PCI-DSS")
        void tarjetaRestMapperDebeManejarEnmascaramientoPCI() {
            assertThat(tarjetaRestMapper.toResponseDto(null)).isNull();

            // Número estándar (>= 4 dígitos)
            Tarjeta tarjetaEstandar = new Tarjeta(
                    1L, "4532-1111-2222-1234", "VISA", "12/28", "123",
                    BigDecimal.TEN, BigDecimal.TEN, EstadoCuenta.ACTIVA, 5L
            );
            TarjetaResponseDto dtoEstandar = tarjetaRestMapper.toResponseDto(tarjetaEstandar);
            assertThat(dtoEstandar.numeroTarjeta()).isEqualTo("**** **** **** 1234");

            // Número corto (< 4 dígitos)
            Tarjeta tarjetaCorta = new Tarjeta(
                    2L, "123", "VISA", "12/28", "123",
                    BigDecimal.TEN, BigDecimal.TEN, EstadoCuenta.ACTIVA, 5L
            );
            TarjetaResponseDto dtoCorta = tarjetaRestMapper.toResponseDto(tarjetaCorta);
            assertThat(dtoCorta.numeroTarjeta()).isEqualTo("123");

            // Número nulo
            Tarjeta tarjetaNumeroNulo = new Tarjeta(
                    3L, null, "VISA", "12/28", "123",
                    BigDecimal.TEN, BigDecimal.TEN, EstadoCuenta.ACTIVA, 5L
            );
            TarjetaResponseDto dtoNumeroNulo = tarjetaRestMapper.toResponseDto(tarjetaNumeroNulo);
            assertThat(dtoNumeroNulo.numeroTarjeta()).isNull();
        }

        @Test
        @DisplayName("MovimientoRestMapper: maneja nulls, tipo nulo y mapea DTO")
        void movimientoRestMapperDebeManejarNullsYTipoNulo() {
            assertThat(movimientoRestMapper.toResponseDto(null)).isNull();

            // Transacción con tipo
            Transaccion conTipo = new Transaccion(1L, 10L, BigDecimal.TEN, TipoMovimiento.RETIRO, "Cajero", LocalDateTime.now(), "EXITOSA");
            MovimientoResponseDto dtoConTipo = movimientoRestMapper.toResponseDto(conTipo);
            assertThat(dtoConTipo.tipo()).isEqualTo("RETIRO");

            // Transacción sin tipo
            Transaccion sinTipo = new Transaccion(2L, 10L, BigDecimal.TEN, null, "Cajero", LocalDateTime.now(), "EXITOSA");
            MovimientoResponseDto dtoSinTipo = movimientoRestMapper.toResponseDto(sinTipo);
            assertThat(dtoSinTipo.tipo()).isNull();
        }
    }
}
