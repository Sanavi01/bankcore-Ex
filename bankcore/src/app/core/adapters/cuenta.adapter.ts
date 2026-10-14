import { Transaccion } from '../models/transaccion.model';
import { MovimientoBancarioDto } from '../models/movimiento.model';

/**
 * Adaptador de Infraestructura Frontend para Cuentas y Movimientos (Patrón Adapter GoF).
 * Traduce entre las respuestas DTO de Spring Boot y el modelo Transaccion consumido por la UI.
 */
export class CuentaAdapter {

  /**
   * Adapta un DTO de movimiento contable al modelo Transaccion consumido por MovimientosTable.
   */
  static adaptarMovimiento(dto: MovimientoBancarioDto): Transaccion {
    const esIngreso = dto.tipo === 'TRANSFERENCIA_ENTRANTE' || dto.tipo === 'DEPOSITO';
    return {
      id: String(dto.id),
      monto: Number(dto.monto),
      tipo: esIngreso ? 'INGRESO' : 'EGRESO',
      concepto: dto.concepto,
      fecha: new Date(dto.fecha),
      estado: dto.estado === 'EXITOSA' ? 'COMPLETADA' : 'RECHAZADA',
      referencia: `OP-${dto.id}`
    };
  }
}
