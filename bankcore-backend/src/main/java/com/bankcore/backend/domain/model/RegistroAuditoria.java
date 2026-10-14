package com.bankcore.backend.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad de Dominio Puro para Auditoría Forense y Cumplimiento (SARLAFT/AML).
 * Totalmente desacoplada de la contabilidad transaccional.
 * Cero dependencias de frameworks o librerías externas.
 */
public class RegistroAuditoria {

    private final Long id;
    private final String idTransaccion;
    private final String cuentaOrigen;
    private final String cuentaDestino;
    private final BigDecimal monto;
    private final String clasificacionRiesgo;
    private final LocalDateTime fechaEvento;
    private final LocalDateTime fechaAuditoria;
    private final String canal;

    public RegistroAuditoria(Long id, String idTransaccion, String cuentaOrigen, String cuentaDestino,
                             BigDecimal monto, String clasificacionRiesgo, LocalDateTime fechaEvento,
                             LocalDateTime fechaAuditoria, String canal) {
        this.id = id;
        this.idTransaccion = idTransaccion;
        this.cuentaOrigen = cuentaOrigen;
        this.cuentaDestino = cuentaDestino;
        this.monto = monto != null ? monto : BigDecimal.ZERO;
        this.clasificacionRiesgo = clasificacionRiesgo != null ? clasificacionRiesgo : "BAJO_RIESGO_CONFORME";
        this.fechaEvento = fechaEvento != null ? fechaEvento : LocalDateTime.now();
        this.fechaAuditoria = fechaAuditoria != null ? fechaAuditoria : LocalDateTime.now();
        this.canal = canal != null ? canal : "RABBITMQ_ASYNC";
    }

    public Long getId() { return id; }
    public String getIdTransaccion() { return idTransaccion; }
    public String getCuentaOrigen() { return cuentaOrigen; }
    public String getCuentaDestino() { return cuentaDestino; }
    public BigDecimal getMonto() { return monto; }
    public String getClasificacionRiesgo() { return clasificacionRiesgo; }
    public LocalDateTime getFechaEvento() { return fechaEvento; }
    public LocalDateTime getFechaAuditoria() { return fechaAuditoria; }
    public String getCanal() { return canal; }
}
