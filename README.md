# 🏦 BankCore Digital — Core Bancario de Alta Concurrencia

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg?logo=openjdk)](https://www.oracle.com/java/)
[![Spring Boot 3.3.4](https://img.shields.io/badge/Spring_Boot-3.3.4-brightgreen.svg?logo=springboot)](https://spring.io/projects/spring-boot)
[![Angular 19](https://img.shields.io/badge/Angular-19_Standalone-dd0031.svg?logo=angular)](https://angular.dev/)
[![RabbitMQ 3.13](https://img.shields.io/badge/RabbitMQ-3.13_EDA-ff6600.svg?logo=rabbitmq)](https://www.rabbitmq.com/)
[![Docker Compose](https://img.shields.io/badge/Docker-Multi--Stage_Hardened-2496ed.svg?logo=docker)](https://www.docker.com/)
[![JaCoCo](https://img.shields.io/badge/JaCoCo-100%25_Coverage-success.svg)](bankcore-backend/build/reports/jacoco/test/html/index.html)
[![PITest](https://img.shields.io/badge/PITest-100%25_Mutation_Killed-blueviolet.svg)](bankcore-backend/build/reports/pitest/index.html)
[![SonarCloud](https://img.shields.io/badge/SonarCloud-Quality_Gate_Passed-4b95d6.svg)](https://sonarcloud.io/)

Sistema bancario digital transaccional de misión crítica diseñado bajo **Arquitectura Hexagonal (Ports & Adapters)**, **Arquitectura Dirigida por Eventos (EDA)** y principios de **Clean Code / DDD**. Desarrollado con **Java 21**, **Spring Boot 3**, **Angular 19 (Signals)** y **RabbitMQ**.

---

## 📑 Tabla de Contenidos

- [1. Resumen de Arquitectura y Patrones](#1-resumen-de-arquitectura-y-patrones)
- [2. Despliegue Rápido con Docker (Zero-Config)](#2-despliegue-rápido-con-docker-zero-config)
- [3. URLs y Enlaces a Consolas](#3-urls-y-enlaces-a-consolas)
- [4. Pruebas y Colección Postman (16 Endpoints)](#4-pruebas-y-colección-postman-16-endpoints)
- [5. Gobernanza de Calidad: JaCoCo, PITest y ArchUnit](#5-gobernanza-de-calidad-jacoco-pitest-y-archunit)
- [6. Ciberseguridad Bancaria y Cumplimiento SARLAFT](#6-ciberseguridad-bancaria-y-cumplimiento-sarlaft)
- [7. Desarrollo Local (Paso a Paso)](#7-desarrollo-local-paso-a-paso)

---

## 1. Resumen de Arquitectura y Patrones

El sistema desacopla estrictamente las reglas de negocio de los detalles de infraestructura:

```mermaid
graph TD
    Client["SPA Angular 19 (Signals & Nginx)"] -->|HTTP / REST JSON| AdapterIn["Primary Adapter: REST Controllers"]
    
    subgraph CoreBancario["Hexágono de Dominio (Hexagonal Core)"]
        AdapterIn -->|DTOs| UseCase["Casos de Uso & Application Services"]
        UseCase -->|Modelos Puros| Domain["Domain Entities: Cuenta, Tarjeta, Transaccion"]
        UseCase -->|Invoca| PortOutRepo["Driven Port: CuentaRepositoryPort"]
        UseCase -->|Invoca| PortOutEvent["Driven Port: TransaccionEventPublisherPort"]
    end
    
    subgraph InfrastructureLayer["Capa de Infraestructura (Driven Adapters)"]
        PortOutRepo -->|Spring Data JPA| H2DB[("H2 Database (ACID / Pessimistic Lock)")]
        PortOutEvent -->|Spring AMQP| RabbitBroker["RabbitMQ Topic Exchange (bankcore.exchange)"]
    end

    subgraph AsyncAuditoria["Procesamiento Asíncrono Forense"]
        RabbitBroker -->|transaccion.realizada| AuditQueue["transacciones.auditoria.queue"]
        RabbitBroker -->|Dead Letter Exchange| DLQ["transacciones.auditoria.dlq"]
        AuditQueue -->|@RabbitListener| Consumer["AuditoriaTransaccionConsumer"]
        Consumer -->|Evalúa Regla AML| SarlaftService["AuditoriaService (SARLAFT)"]
        SarlaftService -->|AuditoriaRepositoryPort| AuditStore[("Tabla de Auditoría Inmutable")]
    end
```

### 🧩 Patrones de Diseño Implementados

| Patrón | Capa / Componente | Propósito Técnico |
| :--- | :--- | :--- |
| **Ports & Adapters (Hexagonal)** | Core Bancario vs Infraestructura | Aislamiento total del dominio contra frameworks externos (probabilidad cero de acoplamiento a Spring). |
| **Idempotency Key (RFC 7231)** | `TransferenciaController` & `IdempotenciaService` | Tabla de hashes UUID con control concurrente; previene doble cobro ante microcortes o reintentos del cliente. |
| **Pessimistic Locking (`PESSIMISTIC_WRITE`)** | `CuentaRepositoryAdapter` | Exclusión mutua a nivel de fila en la BD para evitar *race conditions* y sobregiros en transferencias concurrentes. |
| **Dead Letter Queue (DLQ) & Exponential Backoff** | `RabbitMQConfig` & `AuditoriaDlqConsumer` | Resiliencia EDA: reintentos controlados con fallo tolerante; los eventos con veneno van a la DLQ sin tumbar el broker. |
| **Mapper Pattern (GoF)** | `*PersistenceMapper` & `*RestMapper` | Conversión bidireccional pura entre Entidades JPA, Modelos de Dominio y DTOs inmutables (Java 21 `record`). |
| **Factory Method (GoF)** | `Tarjeta.emitir(...)` | Creación controlada de tarjetas con generación determinista de PAN según franquicia (VISA, Mastercard, AMEX). |
| **HTTP Interceptor (`HttpInterceptorFn`)** | Frontend `idempotenciaInterceptor` | Inyección automática de `Idempotency-Key` único (UUID v4) en cada mutación de balance financiera. |
| **Signals & toSignal (Angular 19)** | `TransferenciasService` & Components | Reactividad declarativa de grano fino sin *memory leaks* y con zoneless *change detection*. |

---

## 2. Despliegue Rápido con Docker (Zero-Config)

Cualquier persona puede ejecutar el ecosistema bancario completo (**RabbitMQ + Backend Spring Boot + Frontend Nginx**) sin instalar Java, Node ni Gradle:

### Requisito Previo
* [Docker Desktop](https://www.docker.com/products/docker-desktop/) iniciado.

### Ejecución en 1 Línea:
```bash
docker compose up --build -d
```

### Para Detener el Sistema:
```bash
docker compose down
# O con reseteo completo de datos y colas:
docker compose down -v
```

---

## 3. URLs y Enlaces a Consolas

| Servicio | URL | Descripción | Credenciales |
| :--- | :--- | :--- | :--- |
| 🌐 **Frontend SPA** | [http://localhost:4200](http://localhost:4200) | Portal bancario Angular 19 servido por Nginx endurecido | N/A |
| 📑 **Swagger / OpenAPI 3** | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) | Interfaz interactiva de documentación y ejecución de endpoints | N/A |
| 📋 **OpenAPI JSON Spec** | [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) | Especificación JSON OAS 3.0 para consumo de clientes | N/A |
| 🐇 **RabbitMQ Dashboard** | [http://localhost:15672](http://localhost:15672) | Monitor de Exchanges, Colas de Auditoría y DLQ | `guest` / `guest` |
| 🩺 **Actuator Health** | [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health) | Sonda de salud (H2 Database, Disco, RabbitMQ Broker) | N/A |
| 📊 **Actuator Metrics** | [http://localhost:8080/actuator/metrics](http://localhost:8080/actuator/metrics) | Telemetría JVM, memoria, hilos y latencias HTTP | N/A |

---

## 4. Pruebas y Colección Postman (16 Endpoints)

Se incluye la colección oficial de pruebas en la raíz del repositorio:
📁 **[`bankcore.postman_collection.json`](file:///c:/Users/sanav/Documents/Planes%20de%20Estudio/Angular/bankcore.postman_collection.json)**

Al importarla en Postman encontrarás **16 peticiones organizadas en 5 subdominios**:

```text
bankcore.postman_collection.json
├── 1. Subdominio Cuentas Bancarias
│   ├── 1.1 Cuentas - Crear Nueva Cuenta Bancaria (POST /api/v1/cuentas)
│   ├── 1.2 Cuentas - Obtener Cuenta Origen por ID (GET /api/v1/cuentas/1)
│   ├── 1.3 Cuentas - Obtener Cuenta por Número (GET /api/v1/cuentas/numero/001-ORIGEN)
│   ├── 1.4 Cuentas - Depositar Fondos Directos Cash-In (POST /api/v1/cuentas/1/depositar)
│   ├── 1.5 Cuentas - Consultar Movimientos Contables (GET /api/v1/cuentas/1/movimientos)
│   └── 1.6 Cuentas - Consultar Tarjetas de la Cuenta (GET /api/v1/cuentas/1/tarjetas)
├── 2. Subdominio Tarjetas de Crédito
│   ├── 2.1 Tarjetas - Emitir y Vincular Nueva Tarjeta (POST /api/v1/tarjetas)
│   ├── 2.2 Tarjetas - Consultar Tarjeta por ID [PCI-DSS] (GET /api/v1/tarjetas/1)
│   ├── 2.3 Tarjetas - Bloquear Tarjeta Preventivamente (PUT /api/v1/tarjetas/1/bloquear)
│   └── 2.4 Tarjetas - Desbloquear / Reactivar Tarjeta (PUT /api/v1/tarjetas/1/desbloquear)
├── 3. Transferencias Bancarias (ACID & EDA)
│   ├── 3.1 Transferencias - Ejecutar Transferencia Atómica ACID (POST /api/v1/transferencias)
│   └── 3.2 Transferencias - Reintento Idempotente Blindaje (POST /api/v1/transferencias)
├── 4. Auditoría Forense y Cumplimiento AML (RabbitMQ)
│   ├── 4.1 Auditoría AML - Consultar Registro por ID Transacción (GET /api/v1/auditoria/transacciones/{id})
│   ├── 4.2 Auditoría AML - Filtrar por Bajo Riesgo Conforme (GET /api/v1/auditoria/riesgo/BAJO_RIESGO_CONFORME)
│   └── 4.3 Auditoría AML - Filtrar por Alerta Alta SARLAFT (GET /api/v1/auditoria/riesgo/ALERTA_AML_REVISION)
└── 5. Observabilidad, Salud & Governance
    └── 5.1 Actuator - Healthcheck General (GET /actuator/health)
```

---

## 5. Gobernanza de Calidad: JaCoCo, PITest y ArchUnit

El proyecto cuenta con una pirámide de pruebas robusta (Unitarias, Integración MockMvc, Arquitectura y Mutación).

### 📊 Reporte de Cobertura JaCoCo (100% Líneas)

Para ejecutar la suite completa de pruebas y generar el reporte HTML:

```bash
cd bankcore-backend
./gradlew test jacocoTestReport
```

* **Ruta del Reporte:** `bankcore-backend/build/reports/jacoco/test/html/index.html`
* **Métricas Obtenidas:**
  * **Line Coverage:** `100.0%` (555 / 555 líneas)
  * **Instruction Coverage:** `100.0%` (2,260 / 2,260 instrucciones)
  * **Branch Coverage:** `99.3%` (141 / 142 ramas)
  * **Clases con instrucciones no cubiertas:** `0`

---

### 🧬 Pruebas de Mutación PITest (100% Mutantes Eliminados)

PITest inyecta mutaciones sintéticas en el bytecode (cambio de operadores booleanos, saltos lógicos, límites) para garantizar que las pruebas no sean cosméticas:

```bash
cd bankcore-backend
./gradlew pitest
```

* **Ruta del Reporte:** `bankcore-backend/build/reports/pitest/index.html`
* **Resultado:** **0% de mutantes sobrevivientes** en el Core transaccional (`RealizarTransferenciaUseCase`), comprobando aserciones hiper-estrictas.

---

### 🏛️ Verificación de Reglas Arquitectónicas con ArchUnit

Se ejecutan como parte del test suite estándar:
* El Dominio no tiene dependencias de Spring, JPA, Hibernate ni infraestructura.
* Los controladores REST solo se comunican con Casos de Uso / Servicios de Aplicación.
* Los Adaptadores de Persistencia implementan rigurosamente los Puertos definidos en el Hexágono.

---

### ⚡ Pruebas de Carga y Resiliencia con k6

Prueba de estrés de alto volumen simulando ráfagas transaccionales concurrentes:
* **Transacciones Ejecutadas:** `15,980 transacciones exitosas`
* **Throughput Alcanzado:** `532.5 TPS`
* **Latencia Percentil 95 (p95):** `80.62 ms`
* **Tasa de Error (HTTP 5xx):** `0.00%`

---

## 6. Ciberseguridad Bancaria y Cumplimiento SARLAFT

1. **Enmascaramiento PCI-DSS:** Los números de tarjeta de crédito (PAN) se ofuscan en el Adaptador REST (`**** **** **** 1234`), evitando fugas de información sensible en los logs y la interfaz gráfica.
2. **Clasificación Automática de Riesgo AML / SARLAFT:**
   * Transacciones $\ge \$5,000,000$ son etiquetadas automáticamente como `ALERTA_AML_REVISION` para auditoría inmediata.
   * Transacciones menores son clasificadas como `BAJO_RIESGO_CONFORME`.
3. **Hardening de Contenedores (OWASP / CIS Benchmarks):**
   * El Dockerfile del backend ejecuta Spring Boot bajo un usuario no privilegiado (`appuser:appgroup`, UID 10001).
   * El contenedor web Nginx implementa cabeceras bancarias estrictas:
     * `X-Frame-Options: DENY` (anti-Clickjacking)
     * `X-Content-Type-Options: nosniff` (anti-MIME Sniffing)
     * `X-XSS-Protection: 1; mode=block`
     * `Content-Security-Policy (CSP)` estricto restringiendo ejecución de scripts no autorizados.

---

## 7. Desarrollo Local (Paso a Paso)

Si deseas levantar y depurar los servicios manualmente fuera de Docker:

### 1. Iniciar Broker RabbitMQ
```bash
docker run -d --name bankcore-rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management-alpine
```

### 2. Iniciar Backend (Spring Boot 3)
```bash
cd bankcore-backend
./gradlew bootRun
```
*API disponible en:* `http://localhost:8080`

### 3. Iniciar Frontend (Angular 19)
```bash
cd bankcore
npm install
npm start
```
*Web App disponible en:* `http://localhost:4200`
