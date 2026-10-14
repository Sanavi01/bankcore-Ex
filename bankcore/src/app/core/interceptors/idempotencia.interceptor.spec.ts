import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { idempotenciaInterceptor } from './idempotencia.interceptor';

describe('idempotenciaInterceptor (Subtema 3.4 Interceptores Funcionales)', () => {
  let httpClient: HttpClient;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([idempotenciaInterceptor])),
        provideHttpClientTesting()
      ]
    });

    httpClient = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('1. Debe adjuntar automáticamente el header Idempotency-Key en peticiones POST a transferencias', () => {
    httpClient.post('http://localhost:8080/api/v1/transferencias', { monto: 100 }).subscribe();

    const req = httpMock.expectOne('http://localhost:8080/api/v1/transferencias');
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.has('Idempotency-Key')).toBe(true);
    expect(req.request.headers.get('Idempotency-Key')).toBeTruthy();
    req.flush({ status: 'OK' });
  });

  it('2. NO debe sobreescribir un Idempotency-Key preexistente', () => {
    const keyPersonalizada = 'manual-uuid-12345';
    httpClient.post('http://localhost:8080/api/v1/transferencias', { monto: 100 }, {
      headers: { 'Idempotency-Key': keyPersonalizada }
    }).subscribe();

    const req = httpMock.expectOne('http://localhost:8080/api/v1/transferencias');
    expect(req.request.headers.get('Idempotency-Key')).toBe(keyPersonalizada);
    req.flush({ status: 'OK' });
  });

  it('3. NO debe adjuntar Idempotency-Key en peticiones GET (cumplimiento RFC 9110)', () => {
    httpClient.get('http://localhost:8080/api/v1/cuentas/1').subscribe();

    const req = httpMock.expectOne('http://localhost:8080/api/v1/cuentas/1');
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.has('Idempotency-Key')).toBe(false);
    req.flush({ id: 1 });
  });
});
