package com.bankcore.backend.infrastructure.persistence.adapter;

import com.bankcore.backend.domain.model.Transaccion;
import com.bankcore.backend.domain.port.TransaccionRepositoryPort;
import com.bankcore.backend.infrastructure.persistence.entity.CuentaJpaEntity;
import com.bankcore.backend.infrastructure.persistence.entity.TransaccionJpaEntity;
import com.bankcore.backend.infrastructure.persistence.mapper.TransaccionPersistenceMapper;
import com.bankcore.backend.infrastructure.persistence.repository.SpringDataCuentaRepository;
import com.bankcore.backend.infrastructure.persistence.repository.SpringDataTransaccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador Secundario de Persistencia para Transacciones (Patrón GoF Adapter).
 * Implementa TransaccionRepositoryPort para guardar y consultar asientos contables en PostgreSQL.
 */
@Component
@RequiredArgsConstructor
public class TransaccionRepositoryAdapter implements TransaccionRepositoryPort {

    private final SpringDataTransaccionRepository transaccionRepository;
    private final SpringDataCuentaRepository cuentaRepository;
    private final TransaccionPersistenceMapper mapper;

    @Override
    public Transaccion guardar(Transaccion transaccion) {
        CuentaJpaEntity cuenta = cuentaRepository.findById(transaccion.getCuentaId())
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada con ID: " + transaccion.getCuentaId()));

        TransaccionJpaEntity entity = mapper.toEntity(transaccion, cuenta);
        TransaccionJpaEntity guardada = transaccionRepository.save(entity);
        return mapper.toDomain(guardada);
    }

    @Override
    public List<Transaccion> buscarPorCuentaId(Long cuentaId) {
        return transaccionRepository.findByCuentaIdOrderByFechaDesc(cuentaId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Transaccion> buscarPorId(Long id) {
        return transaccionRepository.findById(id).map(mapper::toDomain);
    }
}
