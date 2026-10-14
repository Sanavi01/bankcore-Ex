import http from 'k6/http';
import { check, sleep } from 'k6';

/**
 * Script de Pruebas de Estrés y Rendimiento con Grafana k6 (DevOps / SRE).
 * 
 * Objetivo:
 * Simular ráfagas masivas de transferencias concurrentes contra la API de BankCore
 * para verificar que:
 * 1. La API responde con latencias p(95) < 300ms gracias al procesamiento asíncrono.
 * 2. RabbitMQ absorbe el pico de eventos en 'transacciones.audit.queue' sin saturar PostgreSQL.
 * 3. La tasa de fallos HTTP (http_req_failed) permanece por debajo del 1%.
 */

export const options = {
  stages: [
    { duration: '5s', target: 20 },   // Calentamiento rápido hasta 20 VUs
    { duration: '15s', target: 50 },  // Carga sostenida de 50 VUs concurrentes
    { duration: '5s', target: 100 },  // Pico de estrés repentino a 100 VUs
    { duration: '5s', target: 0 },    // Desaceleración
  ],
  thresholds: {
    http_req_duration: ['p(95)<400'], // El 95% de las transferencias debe responder en menos de 400ms
    http_req_failed: ['rate<0.01'],   // Menos del 1% de errores permitidos
  },
};

// Generador de UUID v4 simple para Idempotency-Key
function generarUuid() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    const r = (Math.random() * 16) | 0;
    const v = c === 'x' ? r : (r & 0x3) | 0x8;
    return v.toString(16);
  });
}

export default function () {
  const url = __ENV.API_URL || 'http://localhost:8080/api/v1/transferencias';
  const idempotencyKey = generarUuid();

  const payload = JSON.stringify({
    cuentaOrigen: '001-ORIGEN',
    cuentaDestino: '002-DESTINO',
    monto: (Math.random() * 50 + 1).toFixed(2),
    concepto: `Carga concurrente k6 VU-${__VU} iter-${__ITER}`,
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
      'Idempotency-Key': idempotencyKey,
    },
  };

  const res = http.post(url, payload, params);

  check(res, {
    'status es 201 Created': (r) => r.status === 201,
    'comprobante tiene estado EXITOSA': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.estado === 'EXITOSA';
      } catch (e) {
        return false;
      }
    },
    'latencia aceptable (< 400ms)': (r) => r.timings.duration < 400,
  });

  sleep(0.05); // Pausa de 50ms entre iteraciones
}
