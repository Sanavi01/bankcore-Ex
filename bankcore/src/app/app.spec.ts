import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { App } from './app';
import { CuentasService } from './core/services/cuentas.service';
import { TarjetasService } from './core/services/tarjetas.service';

describe('App', () => {
  const mockCuentasService = {
    obtenerConfiguracion: () => ({
      nombreBanco: 'BankCore Digital',
      codigoEntidad: 'BC-001',
      versionApi: 'v1.0.0-PROD',
      limiteTransferenciaDiaria: 10000,
      monedaPorDefecto: 'USD',
      soporteMultidivisa: true,
      ambiente: 'TESTING'
    }),
    obtenerCuenta: () => of({
      id: 1,
      numeroCuenta: 'ES91-2100-0418-45-9921448102',
      titular: 'CARLOS MENDOZA',
      saldo: 19850.50,
      estado: 'ACTIVA' as const,
      fechaCreacion: '2026-10-01T00:00:00Z'
    }),
    obtenerMovimientos: () => of([])
  };

  const mockTarjetasService = {
    obtenerTarjetasPorCuenta: () => of([]),
    bloquearTarjeta: () => of({
      id: 1,
      numeroTarjeta: '**** **** **** 1234',
      franquicia: 'VISA' as const,
      cupoTotal: 10000,
      cupoDisponible: 5000,
      estado: 'BLOQUEADA' as const
    })
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        { provide: CuentasService, useValue: mockCuentasService },
        { provide: TarjetasService, useValue: mockTarjetasService }
      ]
    }).compileComponents();
  });

  it('debe instanciar el componente principal App con servicios segregados', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('debe renderizar el título de la cuenta principal', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Cuenta de Ahorros Principal');
    expect(compiled.textContent).toContain('ES91-2100-0418-45-9921448102');
  });
});
