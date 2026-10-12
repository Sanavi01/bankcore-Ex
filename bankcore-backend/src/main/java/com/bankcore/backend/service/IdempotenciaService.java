package com.bankcore.backend.service;

import com.bankcore.backend.infrastructure.persistence.entity.RegistroIdempotenciaJpaEntity;
import com.bankcore.backend.infrastructure.persistence.repository.SpringDataIdempotenciaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Servicio Transversal (Cross-Cutting Concern) de Idempotencia Financiera.
 * Permite que cualquier Caso de Uso o Controlador verifique si una operación
 * ya fue ejecutada previamente con el mismo 'Idempotency-Key'.
 *
 * Utiliza Generics (<T>) y Jackson (ObjectMapper) para ser agnóstico del tipo de DTO.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotenciaService {

    private final SpringDataIdempotenciaRepository idempotenciaRepository;
    private final ObjectMapper objectMapper;

    /**
     * Busca si ya existe una respuesta previa guardada para la clave dada.
     * Si existe, deserializa el JSON guardado al tipo de DTO esperado (T).
     *
     * @param clave Clave UUID enviada por el cliente.
     * @param targetClass Clase del DTO esperado (ej: TransferenciaResponseDto.class).
     * @return Optional con el DTO recuperado o vacío si la clave es nueva.
     */
    @Transactional(readOnly = true)
    public <T> Optional<T> obtenerRespuestaPrevia(String clave, Class<T> targetClass) {
        if (clave == null || clave.isBlank()) {
            return Optional.empty();
        }

        return idempotenciaRepository.findById(clave).map(registro -> {
            log.info("Idempotencia: Clave [{}] ya procesada previamente para la operación [{}]. Reutilizando respuesta.",
                    clave, registro.getOperacion());
            try {
                return objectMapper.readValue(registro.getRespuestaJson(), targetClass);
            } catch (JsonProcessingException e) {
                log.error("Error al deserializar respuesta idempotente para clave [{}]: {}", clave, e.getMessage());
                throw new IllegalStateException("Error al recuperar respuesta idempotente previa", e);
            }
        });
    }

    /**
     * Registra el resultado exitoso de una operación para garantizar idempotencia en futuras llamadas.
     *
     * @param clave Clave UUID enviada por el cliente.
     * @param operacion Nombre descriptivo del proceso (ej: "TRANSFERENCIA").
     * @param codigoEstado Código de estado HTTP devuelto (ej: 201).
     * @param respuesta Objeto DTO que se serializará a JSON.
     */
    @Transactional
    public <T> void registrarRespuesta(String clave, String operacion, int codigoEstado, T respuesta) {
        if (clave == null || clave.isBlank()) {
            return;
        }

        try {
            String json = objectMapper.writeValueAsString(respuesta);

            RegistroIdempotenciaJpaEntity registro = RegistroIdempotenciaJpaEntity.builder()
                    .clave(clave)
                    .operacion(operacion)
                    .codigoEstado(codigoEstado)
                    .respuestaJson(json)
                    .build();

            idempotenciaRepository.save(registro);
            log.info("Idempotencia: Clave [{}] registrada exitosamente para operación [{}].", clave, operacion);

        } catch (JsonProcessingException e) {
            log.error("Error al serializar respuesta para clave [{}]: {}", clave, e.getMessage());
            throw new IllegalStateException("Error al registrar respuesta para idempotencia", e);
        }
    }
}
