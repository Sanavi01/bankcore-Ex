/**
 * Contrato de Dominio para Cuentas Bancarias (Reflejo de CuentaResponseDto de Spring Boot).
 * Ubicación: Core/Models
 */
export interface CuentaBancaria {
  readonly id: number;
  readonly numeroCuenta: string;
  readonly titular: string;
  readonly saldo: number;
  readonly estado: 'ACTIVA' | 'INACTIVA' | 'BLOQUEADA';
  readonly fechaCreacion: string;
}
