/**
 * Contratos DTO para Operaciones de Transferencias Bancarias (Spring Boot Request / Response).
 * Ubicación: Core/Models
 */
export interface TransferenciaRequest {
  readonly cuentaOrigen: string;
  readonly cuentaDestino: string;
  readonly monto: number;
  readonly concepto: string;
}

export interface TransferenciaResponse {
  readonly idTransaccion: string;
  readonly cuentaOrigen: string;
  readonly cuentaDestino: string;
  readonly monto: number;
  readonly estado: string;
  readonly fechaHora: string;
  readonly mensaje: string;
}
