package com.bankcore.backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Adaptador Secundario (Persistencia / JPA).
 * Representa la tabla 'registros_auditoria' para trazabilidad forense y cumplimiento SARLAFT/AML.
 */
@Entity
@Table(name = "registros_auditoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroAuditoriaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_transaccion", nullable = false, length = 64)
    private String idTransaccion;

    @Column(name = "cuenta_origen", nullable = false, length = 20)
    private String cuentaOrigen;

    @Column(name = "cuenta_destino", nullable = false, length = 20)
    private String cuentaDestino;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal monto;

    @Column(name = "clasificacion_riesgo", nullable = false, length = 50)
    private String clasificacionRiesgo;

    @Column(name = "fecha_evento", nullable = false)
    private LocalDateTime fechaEvento;

    @Column(name = "fecha_auditoria", nullable = false)
    private LocalDateTime fechaAuditoria;

    @Column(nullable = false, length = 30)
    private String canal;

    @PrePersist
    public void prePersist() {
        if (this.fechaAuditoria == null) {
            this.fechaAuditoria = LocalDateTime.now();
        }
    }
}
