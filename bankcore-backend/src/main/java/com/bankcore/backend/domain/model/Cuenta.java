package com.bankcore.backend.domain.model;

import com.bankcore.backend.domain.EstadoCuenta;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad de Dominio Puro (Clean Architecture).
 * Encapsula el estado y las reglas de negocio del banco.
 * Cero dependencias de frameworks o librerías externas.
 */
public class Cuenta {

    private final Long id;
    private final String numeroCuenta;
    private final String titular;
    private BigDecimal saldo;
    private EstadoCuenta estado;
    private final LocalDateTime fechaCreacion;

    public Cuenta(Long id, String numeroCuenta, String titular, BigDecimal saldo, EstadoCuenta estado, LocalDateTime fechaCreacion) {
        this.id = id;
        this.numeroCuenta = numeroCuenta;
        this.titular = titular;
        this.saldo = saldo != null ? saldo : BigDecimal.ZERO;
        this.estado = estado != null ? estado : EstadoCuenta.ACTIVA;
        this.fechaCreacion = fechaCreacion != null ? fechaCreacion : LocalDateTime.now();
    }

    // Regla de Negocio: Verificación previa a transferir
    public boolean puedeTransferir(BigDecimal monto) {
        if (this.estado != EstadoCuenta.ACTIVA) {
            return false;
        }
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        return this.saldo.compareTo(monto) >= 0;
    }

    // Comportamiento de negocio: Débito protegido contra sobregiros
    public void debitar(BigDecimal monto) {
        if (!puedeTransferir(monto)) {
            throw new IllegalStateException("Fondos insuficientes o la cuenta no está activa para transferir");
        }
        this.saldo = this.saldo.subtract(monto);
    }

    // Comportamiento de negocio: Acreditación (entrada de dinero)
    public void acreditar(BigDecimal monto) {
        if (this.estado == EstadoCuenta.CANCELADA) {
            throw new IllegalStateException("No se puede abonar dinero a una cuenta cancelada");
        }
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a acreditar debe ser mayor a cero");
        }
        this.saldo = this.saldo.add(monto);
    }

    // Getters de lectura
    public Long getId() { return id; }
    public String getNumeroCuenta() { return numeroCuenta; }
    public String getTitular() { return titular; }
    public BigDecimal getSaldo() { return saldo; }
    public EstadoCuenta getEstado() { return estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
}
