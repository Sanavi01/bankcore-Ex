import { InjectionToken } from '@angular/core';

export interface BankConfig {
  readonly nombreBanco: string;
  readonly limiteTransferenciaDiaria: number;
  readonly monedaDefecto: string;
  readonly versionApi: string;
}

export const BANK_CONFIG = new InjectionToken<BankConfig>('BANK_CONFIG', {
  providedIn: 'root',
  factory: () => ({
    nombreBanco: 'BankCore Digital',
    limiteTransferenciaDiaria: 5000000,
    monedaDefecto: 'USD',
    versionApi: 'v1.4.0'
  })
});
