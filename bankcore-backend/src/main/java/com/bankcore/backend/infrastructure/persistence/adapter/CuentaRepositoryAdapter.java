package com.bankcore.backend.infrastructure.persistence.adapter;

import com.bankcore.backend.domain.model.Cuenta;
import com.bankcore.backend.domain.port.CuentaRepositoryPort;
import com.bankcore.backend.infrastructure.persistence.entity.CuentaJpaEntity;
import com.bankcore.backend.infrastructure.persistence.mapper.CuentaPersistenceMapper;
import com.bankcore.backend.infrastructure.persistence.repository.SpringDataCuentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

/**
 * Adaptador de Persistencia (Hexagonal Ports & Adapters).
 * Implementa el puerto del dominio conectándolo con Spring Data JPA mediante el Mapper.
 * Cumple DIP: la infraestructura depende del dominio.
 */
@Component
@RequiredArgsConstructor
public class CuentaRepositoryAdapter implements CuentaRepositoryPort {

    private final SpringDataCuentaRepository springDataRepository;
    private final CuentaPersistenceMapper mapper;

    @Override
    public Optional<Cuenta> buscarPorId(Long id) {
        return springDataRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Cuenta> buscarPorNumeroCuenta(String numeroCuenta) {
        return springDataRepository.findByNumeroCuenta(numeroCuenta)
                .map(mapper::toDomain);
    }

    @Override
    public Cuenta guardar(Cuenta cuenta) {
        CuentaJpaEntity jpaEntity = mapper.toEntity(cuenta);
        CuentaJpaEntity guardada = springDataRepository.save(jpaEntity);
        return mapper.toDomain(guardada);
    }
}
