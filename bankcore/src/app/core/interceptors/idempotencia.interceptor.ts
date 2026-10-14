import { HttpInterceptorFn } from '@angular/common/http';

/**
 * Interceptor Funcional de Idempotencia Bancaria (Angular 15+ / 19 Standalone).
 * Responsabilidad Única (Cross-Cutting Concern):
 * Garantizar que cada transacción mutacional saliente hacia el endpoint de transferencias
 * transporte un identificador único (UUID v4) en la cabecera 'Idempotency-Key'.
 * 
 * Inmutabilidad: Clona la petición (req.clone) para garantizar la pureza de la memoria de Angular.
 */
export const idempotenciaInterceptor: HttpInterceptorFn = (req, next) => {
  if (req.method === 'POST' && req.url.includes('/transferencias') && !req.headers.has('Idempotency-Key')) {
    const uuid = typeof crypto !== 'undefined' && crypto.randomUUID 
      ? crypto.randomUUID() 
      : `uuid-${Date.now()}-${Math.random().toString(36).substring(2, 9)}`;

    const reqClonada = req.clone({
      setHeaders: {
        'Idempotency-Key': uuid
      }
    });

    return next(reqClonada);
  }

  return next(req);
};
