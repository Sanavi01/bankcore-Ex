import { Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterOutlet } from '@angular/router';
import { Header } from './shared/components/header/header';
import { BankCard } from './shared/components/bank-card/bank-card';
import { MovimientosTable } from './shared/components/movimientos-table/movimientos-table';
import { TarjetasList } from './shared/components/tarjetas-list/tarjetas-list';
import { TransferenciaFormComponent } from './features/transferencias/transferencia-form/transferencia-form';
import { CurrencyPipe } from '@angular/common';
import { Transaccion } from './core/models/transaccion.model';
import { TarjetaBancaria } from './core/models/tarjeta.model';
import { TransferenciaResponse } from './core/models/transferencia.model';
import { CuentasService } from './core/services/cuentas.service';
import { TarjetasService } from './core/services/tarjetas.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header, BankCard, MovimientosTable, TarjetasList, TransferenciaFormComponent, CurrencyPipe],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  // 1. Inyección de dependencias segregada por subdominio (DIP / SRP)
  private readonly cuentasService = inject(CuentasService);
  private readonly tarjetasService = inject(TarjetasService);

  readonly configBancaria = this.cuentasService.obtenerConfiguracion();

  // -------------------------------------------------------------
  // CONSUMO HTTP REACTIVO CON toSignal() (Módulos 2 & 5 Angular):
  // -------------------------------------------------------------
  // 1. Subdominio Cuentas: Cuenta Principal consultada reactivamente
  readonly cuenta = toSignal(this.cuentasService.obtenerCuenta(1), {
    initialValue: {
      id: 1,
      numeroCuenta: 'ES91-2100-0418-45-9921448102',
      titular: 'CARLOS MENDOZA',
      saldo: 19850.50,
      estado: 'ACTIVA' as const,
      fechaCreacion: new Date().toISOString()
    }
  });

  // 2. Subdominio Movimientos: Movimientos vivos convertidos en Signal
  private readonly historialApi = toSignal(this.cuentasService.obtenerMovimientos(1), {
    initialValue: []
  });

  // Estado para simular lista vacía sin perder reactividad
  readonly forzarVacia = signal(false);

  // Señal computada derivada (Zero memory leaks)
  readonly historial = computed(() => this.forzarVacia() ? [] : this.historialApi());

  alternarLista(): void {
    this.forzarVacia.update(v => !v);
  }

  // 3. Subdominio Tarjetas: Tarjetas delegadas a TarjetasService
  readonly tarjetasCredito = toSignal(this.tarjetasService.obtenerTarjetasPorCuenta(1), {
    initialValue: []
  });

  readonly detalleActivo = signal<Transaccion | null>(null);
  readonly mensajeAccionTarjeta = signal<string | null>(null);

  mostrarDetalle(tx: Transaccion): void {
    this.detalleActivo.set(tx);
  }

  // Delegación al servicio de tarjetas (SRP)
  bloquearTarjeta(tarjetaId: string): void {
    this.tarjetasService.bloquearTarjeta(tarjetaId).subscribe({
      next: (tarjetaActualizada) => {
        this.mensajeAccionTarjeta.set(`🔒 La tarjeta ${tarjetaActualizada.numeroTarjeta} ha sido bloqueada exitosamente en el servidor.`);
      },
      error: () => {
        this.mensajeAccionTarjeta.set(`❌ Error al intentar bloquear la tarjeta #${tarjetaId}.`);
      }
    });
  }

  pagarTarjeta(tarjeta: TarjetaBancaria): void {
    this.mensajeAccionTarjeta.set(`💳 Redirigiendo a la pasarela de pago para la tarjeta ${tarjeta.numeroEnmascarado}.`);
  }

  // Notificación reactiva tras transferencia exitosa
  readonly ultimaTransferencia = signal<TransferenciaResponse | null>(null);

  onTransferenciaExitosa(tx: TransferenciaResponse): void {
    this.ultimaTransferencia.set(tx);
  }
}
