package com.bankcore.backend.infrastructure.persistence.adapter;

import com.bankcore.backend.domain.model.RegistroAuditoria;
import com.bankcore.backend.domain.port.AuditoriaRepositoryPort;
import com.bankcore.backend.infrastructure.persistence.entity.RegistroAuditoriaJpaEntity;
import com.bankcore.backend.infrastructure.persistence.mapper.RegistroAuditoriaPersistenceMapper;
import com.bankcore.backend.infrastructure.persistence.repository.SpringDataRegistroAuditoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador Secundario de Persistencia para Auditoría Forense (Patrón GoF Adapter).
 * Implementa AuditoriaRepositoryPort aislando los detalles de JPA de la capa de dominio.
 */
@Component
@RequiredArgsConstructor
public class RegistroAuditoriaRepositoryAdapter implements AuditoriaRepositoryPort {

    private final SpringDataRegistroAuditoriaRepository repository;
    private final RegistroAuditoriaPersistenceMapper mapper;

    @Override
    public RegistroAuditoria guardar(RegistroAuditoria registroAuditoria) {
        RegistroAuditoriaJpaEntity entity = mapper.toEntity(registroAuditoria);
        RegistroAuditoriaJpaEntity guardada = repository.save(entity);
        return mapper.toDomain(guardada);
    }

    @Override
    public Optional<RegistroAuditoria> buscarPorIdTransaccion(String idTransaccion) {
        return repository.findByIdTransaccion(idTransaccion).map(mapper::toDomain);
    }

    @Override
    public List<RegistroAuditoria> buscarPorClasificacionRiesgo(String clasificacionRiesgo) {
        return repository.findByClasificacionRiesgo(clasificacionRiesgo)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
