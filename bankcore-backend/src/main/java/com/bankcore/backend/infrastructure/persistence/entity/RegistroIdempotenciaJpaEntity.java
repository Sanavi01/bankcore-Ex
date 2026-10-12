package com.bankcore.backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad de Persistencia para Idempotencia Financiera.
 * Almacena el resultado de operaciones procesadas para prevenir dobles cobros o ejecuciones duplicadas.
 */
@Entity
@Table(name = "registros_idempotencia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroIdempotenciaJpaEntity {

    /**
     * Clave única enviada por el cliente en el Header 'Idempotency-Key' (UUID v4).
     * Es la clave primaria de la tabla: si se intenta insertar la misma dos veces, la BD lo rechaza.
     */
    @Id
    @Column(name = "clave", length = 64, nullable = false)
    private String clave;

    /**
     * Identificador de la operación de negocio (ej: "TRANSFERENCIA", "BLOQUEO_TARJETA").
     */
    @Column(name = "operacion", length = 50, nullable = false)
    private String operacion;

    /**
     * Código de estado HTTP retornado (ej: 200, 201).
     */
    @Column(name = "codigo_estado", nullable = false)
    private int codigoEstado;

    /**
     * Payload completo de la respuesta serializado en JSON (almacenado como TEXT en PostgreSQL).
     */
    @Column(name = "respuesta_json", columnDefinition = "TEXT", nullable = false)
    private String respuestaJson;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
    }
}
