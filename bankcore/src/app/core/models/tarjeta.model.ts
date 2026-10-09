/**
 * Contrato oficial de dominio para las Tarjetas Bancarias en BankCore.
 * Ubicación: Core/Models
 */
export interface TarjetaBancaria {
  readonly id: string;
  readonly numeroEnmascarado: string;
  readonly titular: string;
  readonly franquicia: 'VISA' | 'MASTERCARD' | 'AMEX';
  readonly cupoTotal: number;
  readonly cupoDisponible: number;
  readonly estado: 'ACTIVA' | 'BLOQUEADA';
}
