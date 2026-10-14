import { TestBed, ComponentFixture } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import { TransferenciaFormComponent, validarCuentasDiferentes } from './transferencia-form';
import { TransferenciasService } from '../../../core/services/transferencias.service';
import { TransferenciaResponse } from '../../../core/models/transferencia.model';
import { FormControl, FormGroup } from '@angular/forms';

describe('TransferenciaFormComponent (Subtema 3.3 Formularios Reactivos)', () => {
  let component: TransferenciaFormComponent;
  let fixture: ComponentFixture<TransferenciaFormComponent>;

  const mockTransferenciaResponse: TransferenciaResponse = {
    idTransaccion: 'TX-998877',
    cuentaOrigen: '001-ORIGEN',
    cuentaDestino: '002-DESTINO',
    monto: 150.00,
    estado: 'EXITOSA',
    fechaHora: '2026-10-14T04:30:00Z',
    mensaje: 'Transferencia interbancaria procesada con éxito'
  };

  const mockTransferenciasService = {
    realizarTransferencia: vi.fn().mockReturnValue(of(mockTransferenciaResponse)),
    validarLimiteTransferencia: vi.fn().mockReturnValue(true)
  };

  beforeEach(async () => {
    mockTransferenciasService.realizarTransferencia.mockClear();

    await TestBed.configureTestingModule({
      imports: [TransferenciaFormComponent],
      providers: [
        { provide: TransferenciasService, useValue: mockTransferenciasService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TransferenciaFormComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('cuentaOrigenDefault', '001-ORIGEN');
    fixture.componentRef.setInput('saldoDisponible', 5000.00);
    fixture.detectChanges();
  });

  it('1. Debe instanciarse correctamente con valores iniciales', () => {
    expect(component).toBeTruthy();
    expect(component.form).toBeDefined();
    expect(component.cuentaOrigenCtrl.value).toBe('001-ORIGEN');
    expect(component.cuentaDestinoCtrl.value).toBe('');
    expect(component.montoCtrl.value).toBeNull();
  });

  it('2. El formulario debe ser INVÁLIDO inicialmente por falta de campos obligatorios', () => {
    expect(component.form.valid).toBe(false);
    expect(component.cuentaDestinoCtrl.valid).toBe(false);
    expect(component.cuentaDestinoCtrl.hasError('required')).toBe(true);
    expect(component.montoCtrl.hasError('required')).toBe(true);
    expect(component.conceptoCtrl.hasError('required')).toBe(true);
  });

  it('3. Debe invalidar montos menores a $1.00 USD (Validators.min(1))', () => {
    component.montoCtrl.setValue(0);
    expect(component.montoCtrl.hasError('min')).toBe(true);

    component.montoCtrl.setValue(-25.50);
    expect(component.montoCtrl.hasError('min')).toBe(true);

    component.montoCtrl.setValue(1.00);
    expect(component.montoCtrl.hasError('min')).toBe(false);
  });

  it('4. Validador Cruzado: Debe rechazar transferencias a la misma cuenta (cuentasIdenticas)', () => {
    component.cuentaOrigenCtrl.setValue('001-ORIGEN');
    component.cuentaDestinoCtrl.setValue('001-ORIGEN');
    component.montoCtrl.setValue(100);
    component.conceptoCtrl.setValue('Auto pago');

    expect(component.form.hasError('cuentasIdenticas')).toBe(true);
    expect(component.form.valid).toBe(false);

    // Cambiando la cuenta destino a una distinta
    component.cuentaDestinoCtrl.setValue('002-DESTINO');
    expect(component.form.hasError('cuentasIdenticas')).toBe(false);
    expect(component.form.valid).toBe(true);
  });

  it('5. Validador Puro: función validarCuentasDiferentes evaluada de forma aislada', () => {
    const grupo = new FormGroup({
      cuentaOrigen: new FormControl('ES99-1111-2222'),
      cuentaDestino: new FormControl('ES99-1111-2222')
    });

    const error = validarCuentasDiferentes(grupo);
    expect(error).toEqual({ cuentasIdenticas: true });

    grupo.controls.cuentaDestino.setValue('ES99-8888-9999');
    expect(validarCuentasDiferentes(grupo)).toBeNull();
  });

  it('6. Debe rechazar transferencias si el monto excede el saldo disponible', () => {
    component.cuentaDestinoCtrl.setValue('002-DESTINO');
    component.montoCtrl.setValue(999999.00); // Supera los 5,000 USD disponibles
    component.conceptoCtrl.setValue('Transferencia millonaria');

    component.onSubmit();

    expect(component.mensajeError()).toContain('Fondos insuficientes');
    expect(mockTransferenciasService.realizarTransferencia).not.toHaveBeenCalled();
  });

  it('7. Debe despachar la transferencia al servicio y emitir evento en caso de éxito', () => {
    const emitSpy = vi.spyOn(component.transferenciaExitosa, 'emit');

    component.cuentaDestinoCtrl.setValue('002-DESTINO');
    component.montoCtrl.setValue(150.00);
    component.conceptoCtrl.setValue('Pago de servicios');

    expect(component.form.valid).toBe(true);
    component.onSubmit();

    expect(mockTransferenciasService.realizarTransferencia).toHaveBeenCalledWith({
      cuentaOrigen: '001-ORIGEN',
      cuentaDestino: '002-DESTINO',
      monto: 150.00,
      concepto: 'Pago de servicios'
    });

    expect(component.mensajeExito()).toContain('Transferencia exitosa por $150.00 USD');
    expect(emitSpy).toHaveBeenCalledWith(mockTransferenciaResponse);
    // El formulario debe haberse reseteado manteniendo la cuenta origen
    expect(component.cuentaDestinoCtrl.value).toBe('');
    expect(component.montoCtrl.value).toBeNull();
  });

  it('8. Debe capturar y mostrar el error si el backend falla (HTTP 4xx / 5xx)', () => {
    mockTransferenciasService.realizarTransferencia.mockReturnValueOnce(
      throwError(() => ({ error: { detail: 'Cuenta de destino bloqueada por prevención de fraude.' } }))
    );

    component.cuentaDestinoCtrl.setValue('002-DESTINO');
    component.montoCtrl.setValue(50.00);
    component.conceptoCtrl.setValue('Prueba de error');

    component.onSubmit();

    expect(component.mensajeError()).toContain('Cuenta de destino bloqueada por prevención de fraude');
    expect(component.enviando()).toBe(false);
  });
});
