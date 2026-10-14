# 📋 ESPECIFICACIÓN TÉCNICA (SDD) — BANKCORE DIGITAL
## `SPEC-BC-01: Control Dinámico de Cupo de Tarjeta de Crédito`

> **Propósito de esta Guía para el CoE:**  
> Documento base para demostración en vivo de **Spec-Driven Development (SDD)** y **Pair Programming con IA**.  
> Permite guiar la implementación paso a paso siguiendo **TDD**, **Arquitectura Hexagonal**, **Clean Code** y **Gobernanza de Calidad (JaCoCo / PITest)**.

---

## 1. 📌 Contexto de Negocio y Justificación
En la banca digital moderna (como Nubank o Revolut), los usuarios demandan autonomía para autogestionar su límite operativo desde la app móvil. Esto previene fraudes en compras web o clonaciones físicas, permitiendo fijar un límite operativo bajo en el día a día y aumentarlo bajo demanda sin exceder la línea de crédito total autorizada por la entidad financiera.

---

## 2. 🚦 Definition of Ready (DoR)
Condiciones previas cumplidas antes de escribir código:
- [x] **Reglas de Negocio Definidas:** Se conocen las restricciones sobre tarjetas bloqueadas y saldo consumido.
- [x] **Contratos de API Especificados:** Semántica HTTP (`PATCH`), DTOs inmutables con validaciones Jakarta y estándar RFC 7807 para errores.
- [x] **Fronteras Hexagonales Claras:** Dominio Puro (`Tarjeta`) $\rightarrow$ Servicio de Aplicación (`TarjetaService`) $\rightarrow$ Adaptador REST (`TarjetaController`).
- [x] **Criterios de Aceptación BDD:** Escenarios Gherkin redactados para guiar las pruebas unitarias e integración.

---

## 3. 🏁 Definition of Done (DoD)
Criterios de aceptación para dar la feature por finalizada:
- [ ] **Modelo de Dominio Puro:** Método `ajustarCupo(...)` en `Tarjeta` con encapsulamiento de invariantes.
- [ ] **TDD / Pruebas Unitarias de Dominio:** 100% de cobertura en ramas y casos borde en `TarjetaTest`.
- [ ] **Capa de Aplicación:** Método `ajustarCupoTarjeta(...)` en `TarjetaService` transaccional con mocks en `TarjetaServiceTest`.
- [ ] **Capa REST (Adaptador Primario):** Endpoint `PATCH /api/v1/tarjetas/{id}/limite` con documentación OpenAPI 3 (`@Operation`).
- [ ] **Pruebas de Integración MockMvc:** Pruebas BDD en `TarjetaIntegrationTest` validando `200 OK`, `400 Bad Request` y `422 Unprocessable Entity`.
- [ ] **Gobernanza y Calidad:** Suite verde en `./gradlew test jacocoTestReport` manteniendo 100% de cobertura de código.

---

## 4. 📐 Contratos de Entrada y Salida (API REST)

### Endpoint:
```http
PATCH /api/v1/tarjetas/{id}/limite
Content-Type: application/json
Accept: application/json
```

### Contrato de Entrada (Request DTO):
```json
{
  "nuevoCupo": 12000.00
}
```
* **Validaciones Jakarta:**
  * `@NotNull(message = "El nuevo cupo es obligatorio")`
  * `@DecimalMin(value = "1.00", message = "El cupo mínimo configurable es 1.00")`

### Contrato de Salida Exitoso (`200 OK`):
```json
{
  "id": 1,
  "numeroTarjeta": "**** **** **** 4589",
  "franquicia": "VISA",
  "fechaVencimiento": "12/28",
  "cupoTotal": 12000.00,
  "cupoDisponible": 8850.00,
  "estado": "ACTIVA",
  "cuentaId": 1
}
```

### Contratos de Error de Negocio (`422 Unprocessable Entity` — RFC 7807):

#### Caso A: Cupo inferior a la deuda actual (saldo consumido)
```json
{
  "type": "https://bankcore.digital/errors/business-rule-violation",
  "title": "Regla de Negocio No Cumplida",
  "status": 422,
  "detail": "El nuevo cupo ($2000.00) no puede ser inferior al saldo ya consumido de la tarjeta ($3150.00).",
  "instance": "/api/v1/tarjetas/1/limite"
}
```

#### Caso B: Tarjeta bloqueada
```json
{
  "type": "https://bankcore.digital/errors/business-rule-violation",
  "title": "Regla de Negocio No Cumplida",
  "status": 422,
  "detail": "No se puede ajustar el cupo de una tarjeta en estado BLOQUEADA.",
  "instance": "/api/v1/tarjetas/1/limite"
}
```

---

## 5. 🧠 Invariantes y Fórmulas de Negocio (Domain Logic)

$$\text{Consumo Actual} = \text{cupoTotal} - \text{cupoDisponible}$$

1. **Estado Obligatorio:** La tarjeta debe estar en estado `EstadoCuenta.ACTIVA`.
2. **Piso Operativo:** $\text{nuevoCupo} \ge \text{Consumo Actual}$. (No se puede reducir el cupo por debajo de lo que el cliente ya debe).
3. **Techo Crediticio Institucional:** $\text{nuevoCupo} \le \text{LIMITE\_MAXIMO\_CREDITO}$ ($\$50,000.00$).
4. **Recálculo Inmediato del Cupo Disponible:**
   $$\text{nuevoCupoDisponible} = \text{nuevoCupo} - \text{Consumo Actual}$$
5. **Actualización de Cupo Total:**
   $$\text{cupoTotal} = \text{nuevoCupo}$$

---

## 6. 🥒 Criterios de Aceptación BDD (Gherkin)

```gherkin
Característica: Gestión dinámica del límite de cupo en tarjetas de crédito

  Escenario: Ajuste exitoso aumentando cupo operativo
    DADO una tarjeta activa con cupo total de 10,000 y consumo acumulado de 3,150 (disponible 6,850)
    CUANDO el cliente solicita ajustar su cupo a 12,000
    ENTONCES el cupo total pasa a ser 12,000
    Y el cupo disponible se actualiza a 8,850
    Y el servicio responde con HTTP 200 OK

  Escenario: Ajuste exitoso reduciendo cupo operativo por encima del consumo
    DADO una tarjeta activa con cupo total de 10,000 y consumo acumulado de 3,150
    CUANDO el cliente solicita ajustar su cupo a 5,000
    ENTONCES el cupo total pasa a ser 5,000
    Y el cupo disponible se actualiza a 1,850
    Y el servicio responde con HTTP 200 OK

  Escenario: Rechazo por cupo inferior a la deuda ya consumida
    DADO una tarjeta activa con consumo acumulado de 3,150
    CUANDO el cliente solicita ajustar su cupo a 2,000
    ENTONCES el sistema rechaza la operación con HTTP 422 Unprocessable Entity
    Y el saldo y cupo de la tarjeta permanecen inalterados

  Escenario: Rechazo por tarjeta bloqueada
    DADO una tarjeta en estado BLOQUEADA
    CUANDO el cliente solicita ajustar su cupo
    ENTONCES el sistema rechaza la operación con HTTP 422 Unprocessable Entity
```

---
