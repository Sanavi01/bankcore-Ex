package com.bankcore.backend.domain.model;

import com.bankcore.backend.domain.EstadoCuenta;
import java.math.BigDecimal;

/**
 * Modelo de Dominio Puro para Tarjetas Bancarias.
 * Encapsula reglas de consumo, cupo y validación de estado.
 */
public class Tarjeta {

    private final Long id;
    private final String numeroTarjeta;
    private final String franquicia;
    private final String fechaVencimiento;
    private final String cvv;
    private final BigDecimal cupoTotal;
    private BigDecimal cupoDisponible;
    private EstadoCuenta estado;
    private final Long cuentaId;

    public Tarjeta(Long id, String numeroTarjeta, String franquicia, String fechaVencimiento, 
                   String cvv, BigDecimal cupoTotal, BigDecimal cupoDisponible, EstadoCuenta estado, Long cuentaId) {
        this.id = id;
        this.numeroTarjeta = numeroTarjeta;
        this.franquicia = franquicia;
        this.fechaVencimiento = fechaVencimiento;
        this.cvv = cvv;
        this.cupoTotal = cupoTotal;
        this.cupoDisponible = cupoDisponible != null ? cupoDisponible : cupoTotal;
        this.estado = estado != null ? estado : EstadoCuenta.ACTIVA;
        this.cuentaId = cuentaId;
    }

    // Regla de Negocio: Verificación de fondos de la tarjeta
    public boolean puedeConsumir(BigDecimal monto) {
        return this.estado == EstadoCuenta.ACTIVA 
            && monto != null 
            && monto.compareTo(BigDecimal.ZERO) > 0 
            && this.cupoDisponible.compareTo(monto) >= 0;
    }

    // Comportamiento de negocio: Consumo de cupo
    public void consumir(BigDecimal monto) {
        if (!puedeConsumir(monto)) {
            throw new IllegalStateException("Cupo disponible insuficiente o tarjeta inactiva");
        }
        this.cupoDisponible = this.cupoDisponible.subtract(monto);
    }

    // Comportamiento de negocio: Bloqueo preventivo de seguridad
    public void bloquear() {
        if (this.estado == EstadoCuenta.BLOQUEADA) {
            throw new IllegalStateException("La tarjeta ya se encuentra bloqueada");
        }
        this.estado = EstadoCuenta.BLOQUEADA;
    }

    // Comportamiento de negocio: Desbloqueo / Reactivación de tarjeta
    public void desbloquear() {
        if (this.estado == EstadoCuenta.ACTIVA) {
            throw new IllegalStateException("La tarjeta ya se encuentra activa");
        }
        this.estado = EstadoCuenta.ACTIVA;
    }

    // Factory method de dominio para emisión de nuevas tarjetas
    public static Tarjeta emitir(String numeroTarjeta, String franquicia, String fechaVencimiento, 
                                 String cvv, BigDecimal cupoTotal, Long cuentaId) {
        return new Tarjeta(null, numeroTarjeta, franquicia, fechaVencimiento, cvv, cupoTotal, cupoTotal, EstadoCuenta.ACTIVA, cuentaId);
    }

    public Long getId() { return id; }
    public String getNumeroTarjeta() { return numeroTarjeta; }
    public String getFranquicia() { return franquicia; }
    public String getFechaVencimiento() { return fechaVencimiento; }
    public String getCvv() { return cvv; }
    public BigDecimal getCupoTotal() { return cupoTotal; }
    public BigDecimal getCupoDisponible() { return cupoDisponible; }
    public EstadoCuenta getEstado() { return estado; }
    public Long getCuentaId() { return cuentaId; }
}
