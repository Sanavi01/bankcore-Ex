package com.bankcore.backend.infrastructure.rest.mapper;

import com.bankcore.backend.domain.model.RegistroAuditoria;
import com.bankcore.backend.dto.RegistroAuditoriaResponseDto;
import org.springframework.stereotype.Component;

/**
 * Mapper de Capa de Presentación REST para Auditoría (GoF Mapper).
 * Traduce modelos de dominio puros a contratos inmutables DTO.
 */
@Component
public class AuditoriaRestMapper {

    public RegistroAuditoriaResponseDto toResponseDto(RegistroAuditoria domain) {
        if (domain == null) {
            return null;
        }
        return new RegistroAuditoriaResponseDto(
                domain.getId(),
                domain.getIdTransaccion(),
                domain.getCuentaOrigen(),
                domain.getCuentaDestino(),
                domain.getMonto(),
                domain.getCanal(),
                domain.getClasificacionRiesgo(),
                domain.getFechaEvento(),
                domain.getFechaAuditoria()
        );
    }
}
