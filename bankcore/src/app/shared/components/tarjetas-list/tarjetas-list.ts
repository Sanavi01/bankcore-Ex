import { Component, input, output } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { TarjetaBancaria } from '../../../core/models/tarjeta.model';

@Component({
  selector: 'app-tarjetas-list',
  standalone: true,
  imports: [CurrencyPipe],
  templateUrl: './tarjetas-list.html',
  styleUrl: './tarjetas-list.scss',
})
export class TarjetasList {
  // ENTRADA (Data Down): El componente padre le pasa la lista de tarjetas
  readonly tarjetas = input.required<TarjetaBancaria[]>();

  // SALIDAS (Events Up): Notifica al padre cuando el usuario quiere bloquear o pagar
  readonly bloquear = output<string>();
  readonly pagar = output<TarjetaBancaria>();
}
