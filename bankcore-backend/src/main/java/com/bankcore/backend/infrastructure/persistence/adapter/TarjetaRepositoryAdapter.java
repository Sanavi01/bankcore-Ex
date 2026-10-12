package com.bankcore.backend.infrastructure.persistence.adapter;

import com.bankcore.backend.domain.model.Tarjeta;
import com.bankcore.backend.domain.port.TarjetaRepositoryPort;
import com.bankcore.backend.infrastructure.persistence.entity.CuentaJpaEntity;
import com.bankcore.backend.infrastructure.persistence.entity.TarjetaJpaEntity;
import com.bankcore.backend.infrastructure.persistence.mapper.TarjetaPersistenceMapper;
import com.bankcore.backend.infrastructure.persistence.repository.SpringDataCuentaRepository;
import com.bankcore.backend.infrastructure.persistence.repository.SpringDataTarjetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador Secundario de Persistencia para Tarjetas (Patrón GoF Adapter).
 * Implementa TarjetaRepositoryPort desacoplando el dominio de Spring Data JPA.
 */
@Component
@RequiredArgsConstructor
public class TarjetaRepositoryAdapter implements TarjetaRepositoryPort {

    private final SpringDataTarjetaRepository tarjetaRepository;
    private final SpringDataCuentaRepository cuentaRepository;
    private final TarjetaPersistenceMapper mapper;

    @Override
    public Optional<Tarjeta> buscarPorId(Long id) {
        return tarjetaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Tarjeta> buscarPorCuentaId(Long cuentaId) {
        return tarjetaRepository.findByCuentaId(cuentaId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Tarjeta guardar(Tarjeta tarjeta) {
        CuentaJpaEntity cuenta = cuentaRepository.findById(tarjeta.getCuentaId())
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada con ID: " + tarjeta.getCuentaId()));

        TarjetaJpaEntity entity = mapper.toEntity(tarjeta, cuenta);
        TarjetaJpaEntity saved = tarjetaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
