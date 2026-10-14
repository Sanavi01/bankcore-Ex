import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TransferenciaRequest, TransferenciaResponse } from '../models/transferencia.model';
import { BANK_CONFIG } from '../tokens/bank-config.token';

/**
 * Servicio de Dominio para Operaciones y Transferencias Bancarias.
 * Responsabilidad Única (SRP): Ejecución de transferencias e validación de límites financieros.
 */
@Injectable({
  providedIn: 'root'
})
export class TransferenciasService {
  private readonly http = inject(HttpClient);
  private readonly config = inject(BANK_CONFIG);
  private readonly apiUrl = 'http://localhost:8080/api/v1';

  /**
   * Ejecuta una transferencia bancaria atómica hacia Spring Boot.
   */
  realizarTransferencia(request: TransferenciaRequest, idempotencyKey?: string): Observable<TransferenciaResponse> {
    const headers = idempotencyKey ? { 'Idempotency-Key': idempotencyKey } : undefined;
    return this.http.post<TransferenciaResponse>(`${this.apiUrl}/transferencias`, request, { headers });
  }

  /**
   * Regla de negocio en cliente: Valida si el monto excede el límite diario configurado.
   */
  validarLimiteTransferencia(monto: number): boolean {
    return monto > 0 && monto <= this.config.limiteTransferenciaDiaria;
  }
}
