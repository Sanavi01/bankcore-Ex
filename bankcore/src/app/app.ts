import { Component, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Header } from './shared/components/header/header';
import { BankCard } from './shared/components/bank-card/bank-card';
import { MovimientosTable } from './shared/components/movimientos-table/movimientos-table';
import { TarjetasList } from './shared/components/tarjetas-list/tarjetas-list';
import { Transaccion } from './core/models/transaccion.model';
import { TarjetaBancaria } from './core/models/tarjeta.model';
import { CuentasService } from './core/services/cuentas.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header, BankCard, MovimientosTable, TarjetasList],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  // -------------------------------------------------------------
  // INYECCIÓN DE DEPENDENCIAS MODERNA (Tema 2.1 y 2.2):
  // -------------------------------------------------------------
  private readonly cuentasService = inject(CuentasService);

  // Configuración global obtenida a través de la inyección de dependencias
  readonly configBancaria = this.cuentasService.obtenerConfiguracion();

  // Obtenemos los datos del Servicio Singleton (fuente única de verdad)
  readonly historial = signal<Transaccion[]>(this.cuentasService.obtenerHistorial());
  readonly detalleActivo = signal<Transaccion | null>(null);

  mostrarDetalle(tx: Transaccion): void {
    this.detalleActivo.set(tx);
  }

  alternarLista(): void {
    if (this.historial().length > 0) {
      this.historial.set([]);
    } else {
      this.historial.set(this.cuentasService.obtenerHistorial());
    }
  }

  // -------------------------------------------------------------
  // SECCIÓN 2: DATOS DE TARJETAS DE CRÉDITO (EJEMPLO 2 DE PRUEBA)
  // -------------------------------------------------------------
  readonly tarjetasCredito = signal<TarjetaBancaria[]>([
    {
      id: 'CARD-01',
      numeroEnmascarado: '•••• •••• •••• 4589',
      titular: 'CARLOS MENDOZA',
      franquicia: 'VISA',
      cupoTotal: 10000,
      cupoDisponible: 6850,
      estado: 'ACTIVA'
    },
    {
      id: 'CARD-02',
      numeroEnmascarado: '•••• •••• •••• 9921',
      titular: 'CARLOS MENDOZA',
      franquicia: 'MASTERCARD',
      cupoTotal: 15000,
      cupoDisponible: 14200,
      estado: 'ACTIVA'
    },
    {
      id: 'CARD-03',
      numeroEnmascarado: '•••• •••• •••• 3310',
      titular: 'CARLOS MENDOZA',
      franquicia: 'AMEX',
      cupoTotal: 25000,
      cupoDisponible: 0,
      estado: 'BLOQUEADA'
    }
  ]);

  readonly mensajeAccionTarjeta = signal<string | null>(null);

  // Escucha el evento (bloquear) del hijo TarjetasList
  bloquearTarjeta(tarjetaId: string): void {
    // Inmutabilidad estricta: creamos un nuevo arreglo sin mutar en memoria
    this.tarjetasCredito.update(tarjetas =>
      tarjetas.map(t => t.id === tarjetaId ? { ...t, estado: 'BLOQUEADA' as const } : t)
    );
    this.mensajeAccionTarjeta.set(`🔒 La tarjeta ${tarjetaId} ha sido bloqueada preventivamente.`);
  }

  // Escucha el evento (pagar) del hijo TarjetasList
  pagarTarjeta(tarjeta: TarjetaBancaria): void {
    this.mensajeAccionTarjeta.set(`💳 Redirigiendo a la pasarela de pago para la tarjeta ${tarjeta.numeroEnmascarado}.`);
  }
}
