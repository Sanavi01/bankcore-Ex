package com.bankcore.backend.infrastructure.persistence.entity;

import com.bankcore.backend.domain.EstadoCuenta;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Adaptador Secundario (Infraestructura / Persistencia).
 * Representa la tabla física 'cuentas' en PostgreSQL.
 * Responsabilidad Única (SRP): Solo mapear columnas de base de datos.
 */
@Entity
@Table(name = "cuentas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_cuenta", nullable = false, unique = true, length = 20)
    private String numeroCuenta;

    @Column(nullable = false, length = 100)
    private String titular;

    // Precisión monetaria exacta: 19 dígitos totales, 4 decimales
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal saldo;

    // Guardado como texto ("ACTIVA") para evitar corrupción de datos
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCuenta estado;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
        if (this.estado == null) {
            this.estado = EstadoCuenta.ACTIVA;
        }
    }
}
