/**
 * Contrato DTO para Movimientos Contables recibidos del Backend (Spring Boot MovimientoResponseDto).
 * Ubicación: Core/Models
 */
export interface MovimientoBancarioDto {
  readonly id: number;
  readonly monto: number;
  readonly tipo: 'TRANSFERENCIA_ENTRANTE' | 'TRANSFERENCIA_SALIENTE' | 'DEPOSITO' | 'RETIRO';
  readonly concepto: string;
  readonly fecha: string;
  readonly estado: string;
}
