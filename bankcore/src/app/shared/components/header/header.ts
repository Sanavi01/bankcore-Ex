import { Component } from '@angular/core';

@Component({
  selector: 'app-header',
  imports: [],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class Header {
  //VARIABLES
  nombreBanco = 'Bankcore Internacional'
  alertasPendientes = 2;
  canalDigitalAbierto = true;

  //METODO
  descargarExtracto() {
    alert('Generando extracto ...')
  }
}
