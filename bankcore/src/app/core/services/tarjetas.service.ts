import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { TarjetaBancaria, TarjetaDto } from '../models/tarjeta.model';
import { TarjetaAdapter } from '../adapters/tarjeta.adapter';

/**
 * Servicio de Dominio para la Gestión de Tarjetas de Crédito y Débito.
 * Responsabilidad Única (SRP): Consultas y operaciones de tarjetas bancarias.
 */
@Injectable({
  providedIn: 'root'
})
export class TarjetasService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/v1';

  /**
   * Consulta las tarjetas asociadas a una cuenta bancaria.
   */
  obtenerTarjetasPorCuenta(cuentaId: number = 1, titular: string = 'CARLOS MENDOZA'): Observable<TarjetaBancaria[]> {
    return this.http.get<TarjetaDto[]>(`${this.apiUrl}/cuentas/${cuentaId}/tarjetas`).pipe(
      map(tarjetas => tarjetas.map(t => TarjetaAdapter.adaptar(t, titular)))
    );
  }

  /**
   * Bloquea una tarjeta mediante el endpoint seguro PUT /api/v1/tarjetas/{id}/bloquear.
   */
  bloquearTarjeta(tarjetaId: string | number): Observable<TarjetaDto> {
    return this.http.put<TarjetaDto>(`${this.apiUrl}/tarjetas/${tarjetaId}/bloquear`, {});
  }
}
