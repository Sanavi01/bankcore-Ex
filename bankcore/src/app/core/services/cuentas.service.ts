import { Injectable, inject } from '@angular/core';
import { Transaccion } from '../models/transaccion.model';
import { BANK_CONFIG } from '../tokens/bank-config.token';

@Injectable({
  providedIn: 'root' // Singleton a nivel raíz de toda la app
})
export class CuentasService {
  // 1. Inyección de dependencia moderna dentro de un servicio:
  private readonly config = inject(BANK_CONFIG);

  // 2. Fuente de datos de negocio (Centralizada, no dispersa en los componentes):
  private readonly historialMovimientos: Transaccion[] = [
    {
      id: 'TX-1001',
      monto: 3500.00,
      tipo: 'INGRESO',
      concepto: 'Depósito de Nómina Quincenal',
      fecha: new Date('2026-10-01T08:30:00'),
      estado: 'COMPLETADA',
      referencia: 'NOM-9821-TECH'
    },
    {
      id: 'TX-1002',
      monto: 120.50,
      tipo: 'EGRESO',
      concepto: 'Pago de Servicio Eléctrico',
      fecha: new Date('2026-10-02T14:15:00'),
      estado: 'COMPLETADA',
      referencia: 'REC-5541'
    },
    {
      id: 'TX-1003',
      monto: 850.00,
      tipo: 'EGRESO',
      concepto: 'Transferencia Interbancaria SPEI',
      fecha: new Date('2026-10-04T19:40:00'),
      estado: 'PENDIENTE',
      referencia: 'SPEI-8841-BCO'
    },
    {
      id: 'TX-1004',
      monto: 2500.00,
      tipo: 'EGRESO',
      concepto: 'Intento de Retiro en Cajero Ajeno',
      fecha: new Date('2026-10-05T02:10:00'),
      estado: 'RECHAZADA',
      referencia: 'ATM-ERR-99'
    }
  ];

  /**
   * Obtiene la configuración bancaria inyectada.
   */
  obtenerConfiguracion() {
    return this.config;
  }

  /**
   * Obtiene el listado de movimientos de la cuenta.
   */
  obtenerHistorial(): Transaccion[] {
    return this.historialMovimientos;
  }

  /**
   * Regla de negocio: Valida si una transferencia excede el límite configurado en el Token.
   */
  validarLimiteTransferencia(monto: number): boolean {
    return monto <= this.config.limiteTransferenciaDiaria;
  }
}
