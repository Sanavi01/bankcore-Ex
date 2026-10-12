package com.bankcore.backend.domain.model;

import com.bankcore.backend.domain.TipoMovimiento;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Modelo de Dominio Puro para Transacciones Contables.
 * Registro inmutable del libro mayor bancario.
 */
public class Transaccion {

    private final Long id;
    private final Long cuentaId;
    private final BigDecimal monto;
    private final TipoMovimiento tipo;
    private final String concepto;
    private final LocalDateTime fecha;
    private final String estado;

    public Transaccion(Long id, Long cuentaId, BigDecimal monto, TipoMovimiento tipo, String concepto, LocalDateTime fecha, String estado) {
        this.id = id;
        this.cuentaId = cuentaId;
        this.monto = monto;
        this.tipo = tipo;
        this.concepto = concepto;
        this.fecha = fecha != null ? fecha : LocalDateTime.now();
        this.estado = estado != null ? estado : "EXITOSA";
    }

    public Long getId() { return id; }
    public Long getCuentaId() { return cuentaId; }
    public BigDecimal getMonto() { return monto; }
    public TipoMovimiento getTipo() { return tipo; }
    public String getConcepto() { return concepto; }
    public LocalDateTime getFecha() { return fecha; }
    public String getEstado() { return estado; }
}
