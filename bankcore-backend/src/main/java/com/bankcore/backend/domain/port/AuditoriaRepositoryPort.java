package com.bankcore.backend.domain.port;

import com.bankcore.backend.domain.model.RegistroAuditoria;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de Salida del Dominio para Auditoría Forense y Cumplimiento (DIP - Dependency Inversion Principle).
 * Contrato puro Java totalmente agnóstico a bases de datos o frameworks de persistencia.
 */
public interface AuditoriaRepositoryPort {

    RegistroAuditoria guardar(RegistroAuditoria registroAuditoria);

    Optional<RegistroAuditoria> buscarPorIdTransaccion(String idTransaccion);

    List<RegistroAuditoria> buscarPorClasificacionRiesgo(String clasificacionRiesgo);
}
