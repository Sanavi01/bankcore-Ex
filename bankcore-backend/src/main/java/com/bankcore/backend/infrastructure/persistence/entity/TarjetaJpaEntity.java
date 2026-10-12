package com.bankcore.backend.infrastructure.persistence.entity;

import com.bankcore.backend.domain.EstadoCuenta;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Adaptador Secundario (Persistencia / JPA).
 * Representa la tabla 'tarjetas' en PostgreSQL.
 */
@Entity
@Table(name = "tarjetas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TarjetaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_tarjeta", nullable = false, unique = true, length = 19)
    private String numeroTarjeta;

    @Column(nullable = false, length = 20)
    private String franquicia;

    @Column(name = "fecha_vencimiento", nullable = false, length = 5) // "MM/YY"
    private String fechaVencimiento;

    @Column(nullable = false, length = 4)
    private String cvv;

    @Column(name = "cupo_total", nullable = false, precision = 19, scale = 4)
    private BigDecimal cupoTotal;

    @Column(name = "cupo_disponible", nullable = false, precision = 19, scale = 4)
    private BigDecimal cupoDisponible;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCuenta estado;

    // 🔒 RELACIÓN BLINDADA: Evita el antipatrón N+1 en consultas
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_id", nullable = false)
    private CuentaJpaEntity cuenta;
}
