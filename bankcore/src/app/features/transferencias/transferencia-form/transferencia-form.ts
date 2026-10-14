import { Component, ChangeDetectionStrategy, inject, signal, input, output, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup, FormControl, AbstractControl, ValidationErrors } from '@angular/forms';
import { TransferenciasService } from '../../../core/services/transferencias.service';
import { TransferenciaRequest, TransferenciaResponse } from '../../../core/models/transferencia.model';

/**
 * Contrato estrictamente tipado para los controles del formulario de transferencias.
 * Elimina cualquier uso de 'any' garantizando Type-Safety (Angular 14+).
 */
export interface TransferenciaFormControls {
  cuentaOrigen: FormControl<string>;
  cuentaDestino: FormControl<string>;
  monto: FormControl<number | null>;
  concepto: FormControl<string>;
}

/**
 * Validador cruzado personalizado (Cross-Field Validator).
 * Regla de negocio bancaria: La cuenta destino no puede ser igual a la cuenta origen.
 */
export function validarCuentasDiferentes(control: AbstractControl): ValidationErrors | null {
  const origen = control.get('cuentaOrigen')?.value;
  const destino = control.get('cuentaDestino')?.value;
  if (origen && destino && origen.trim().toUpperCase() === destino.trim().toUpperCase()) {
    return { cuentasIdenticas: true };
  }
  return null;
}

/**
 * Componente de Formulario Reactivo Bancario (Subtema 3.3).
 * Responsabilidad Única (SRP): Captura, validación síncrona/cruzada y envío de transferencias atómicas.
 */
@Component({
  selector: 'app-transferencia-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './transferencia-form.html',
  styleUrl: './transferencia-form.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TransferenciaFormComponent {
  private readonly fb = inject(FormBuilder);
  private readonly transferenciasService = inject(TransferenciasService);

  // Inputs reactivos declarativos con Signals
  readonly cuentaOrigenDefault = input<string>('001-ORIGEN');
  readonly saldoDisponible = input<number>(0);

  // Output reactivo para notificar al contenedor padre sobre la transferencia exitosa
  readonly transferenciaExitosa = output<TransferenciaResponse>();

  // Estados locales gestionados mediante Signals
  readonly enviando = signal(false);
  readonly mensajeExito = signal<string | null>(null);
  readonly mensajeError = signal<string | null>(null);

  // Definición del FormGroup fuertemente tipado con validaciones síncronas nativas
  readonly form: FormGroup<TransferenciaFormControls> = this.fb.group<TransferenciaFormControls>({
    cuentaOrigen: new FormControl(this.cuentaOrigenDefault(), {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(8), Validators.maxLength(20)]
    }),
    cuentaDestino: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(8), Validators.maxLength(20)]
    }),
    monto: new FormControl<number | null>(null, {
      validators: [Validators.required, Validators.min(1)]
    }),
    concepto: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(100)]
    })
  }, {
    validators: [validarCuentasDiferentes]
  });

  constructor() {
    // Sincroniza reactivamente el cambio en la cuenta origen recibida por Input
    effect(() => {
      const origen = this.cuentaOrigenDefault();
      if (origen && this.form.controls.cuentaOrigen.value !== origen) {
        this.form.controls.cuentaOrigen.setValue(origen);
      }
    });
  }

  // Getters para simplificar la lectura en la plantilla HTML
  get cuentaOrigenCtrl() { return this.form.controls.cuentaOrigen; }
  get cuentaDestinoCtrl() { return this.form.controls.cuentaDestino; }
  get montoCtrl() { return this.form.controls.monto; }
  get conceptoCtrl() { return this.form.controls.concepto; }

  // Helpers de UX Bancaria para control de estados (touched / dirty)
  tieneError(control: FormControl, errorName: string): boolean {
    return control.hasError(errorName) && (control.touched || control.dirty);
  }

  get tieneErrorCuentasIdenticas(): boolean {
    return this.form.hasError('cuentasIdenticas') && 
           (this.cuentaDestinoCtrl.touched || this.cuentaDestinoCtrl.dirty);
  }

  /**
   * Ejecuta el despacho seguro de la transferencia bancaria.
   */
  onSubmit(): void {
    if (this.form.invalid || this.enviando()) {
      this.form.markAllAsTouched();
      return;
    }

    const monto = this.montoCtrl.value;
    if (monto && this.saldoDisponible() > 0 && monto > this.saldoDisponible()) {
      this.mensajeError.set(`Fondos insuficientes. Tu saldo disponible es de $${this.saldoDisponible().toFixed(2)} USD.`);
      return;
    }

    this.enviando.set(true);
    this.mensajeError.set(null);
    this.mensajeExito.set(null);

    const payload: TransferenciaRequest = {
      cuentaOrigen: this.cuentaOrigenCtrl.value.trim(),
      cuentaDestino: this.cuentaDestinoCtrl.value.trim(),
      monto: Number(monto),
      concepto: this.conceptoCtrl.value.trim()
    };

    this.transferenciasService.realizarTransferencia(payload).subscribe({
      next: (response) => {
        this.enviando.set(false);
        this.mensajeExito.set(`✅ Transferencia exitosa por $${response.monto.toFixed(2)} USD hacia ${response.cuentaDestino}.`);
        this.transferenciaExitosa.emit(response);
        // Reset conservando la cuenta origen actual
        this.form.reset({
          cuentaOrigen: this.cuentaOrigenDefault(),
          cuentaDestino: '',
          monto: null,
          concepto: ''
        });
      },
      error: (err) => {
        this.enviando.set(false);
        const detalleError = err?.error?.detail || err?.error?.mensaje || 'Error al procesar la transferencia bancaria en el servidor.';
        this.mensajeError.set(`❌ ${detalleError}`);
      }
    });
  }
}
