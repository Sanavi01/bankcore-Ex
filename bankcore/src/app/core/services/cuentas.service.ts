import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { CuentaBancaria } from '../models/cuenta.model';
import { MovimientoBancarioDto } from '../models/movimiento.model';
import { Transaccion } from '../models/transaccion.model';
import { CuentaAdapter } from '../adapters/cuenta.adapter';
import { BANK_CONFIG } from '../tokens/bank-config.token';

/**
 * Servicio de Dominio para Cuentas Bancarias y Movimientos.
 * Responsabilidad Única (SRP): Consulta de estado de cuentas y libro contable.
 */
@Injectable({
  providedIn: 'root'
})
export class CuentasService {
  private readonly http = inject(HttpClient);
  private readonly config = inject(BANK_CONFIG);
  private readonly apiUrl = 'http://localhost:8080/api/v1';

  /**
   * Obtiene la configuración bancaria inyectada por tokens.
   */
  obtenerConfiguracion() {
    return this.config;
  }

  /**
   * Consulta los datos de la cuenta por ID desde la API REST de Spring Boot.
   */
  obtenerCuenta(id: number = 1): Observable<CuentaBancaria> {
    return this.http.get<CuentaBancaria>(`${this.apiUrl}/cuentas/${id}`);
  }

  /**
   * Consulta los movimientos contables de la cuenta y los adapta al modelo visual.
   */
  obtenerMovimientos(id: number = 1): Observable<Transaccion[]> {
    return this.http.get<MovimientoBancarioDto[]>(`${this.apiUrl}/cuentas/${id}/movimientos`).pipe(
      map(movimientos => movimientos.map(CuentaAdapter.adaptarMovimiento))
    );
  }
}
