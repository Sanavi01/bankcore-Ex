import { Component, input, output } from '@angular/core';
import { DatePipe, CurrencyPipe } from '@angular/common';
import { Transaccion } from '../../../core/models/transaccion.model';

@Component({
  selector: 'app-movimientos-table',
  standalone: true,
  imports: [DatePipe, CurrencyPipe],
  templateUrl: './movimientos-table.html',
  styleUrl: './movimientos-table.scss',
})
export class MovimientosTable {
  // 1. SIGNAL INPUT: Recibe la lista desde el padre (Componente Dumb puro)
  readonly transacciones = input.required<Transaccion[]>();

  // 2. SIGNAL OUTPUT: Notifica al padre cuando el usuario hace clic en una fila
  readonly seleccionar = output<Transaccion>();

  // 3. Método para emitir el evento hacia afuera
  onSeleccionar(tx: Transaccion): void {
    this.seleccionar.emit(tx);
  }
}
