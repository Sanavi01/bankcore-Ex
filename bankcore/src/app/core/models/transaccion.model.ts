/**
 * Contrato oficial de dominio para las transacciones en BankCore.
 * Ubicación: Core/Models (accesible globalmente por toda la aplicación).
 */
export interface Transaccion {
  readonly id: string;
  readonly monto: number;
  readonly tipo: 'INGRESO' | 'EGRESO';
  readonly concepto: string;
  readonly fecha: Date;
  readonly estado: 'COMPLETADA' | 'PENDIENTE' | 'RECHAZADA';
  readonly referencia?: string; // Opcional
}
