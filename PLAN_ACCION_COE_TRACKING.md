# 🚀 PLAN DE ACCIÓN & TRACKING: CERTIFICACIÓN TÉCNICA COE (FULLSTACK & DEVOPS)

> **Candidato:** Consultor de Desarrollo  
> **Evaluador:** Centro de Excelencia (CoE) de Arquitectura y Desarrollo  
> **Fecha de Evaluación:** Miércoles 14 de Octubre de 2026 ⋅ 9:00 AM – 10:00 AM (COT)  
> **Proyecto Insignia:** **BankCore Digital** (Fullstack Cloud-Native & Event-Driven)  
> **Estado General:** 🟨 En Ejecución Intensiva (5 Días para la Valoración)  
> **Última Actualización:** 2026-10-08  

---

## 📌 Leyenda de Estados
- 🟩 **`[x]` Completado:** Código implementado, funcionando y dominado para explicar en la llamada.
- 🟨 **`[/]` En Curso:** Tema actual en desarrollo, explicación o refactorización.
- ⬜ **`[ ]` Pendiente:** Siguiente paso programado en la ruta de aceleración.

---

## 🏛️ Arquitectura Global del Proyecto a Defender

Este es el mapa mental exacto que proyectarás y explicarás con naturalidad ante los evaluadores:

```mermaid
flowchart TD
    subgraph Frontend["Frontend Layer (Angular 19+)"]
        A[BankCore SPA<br/>Standalone + Signals + Nginx]
    end

    subgraph Backend["Backend Layer (Spring Boot 3 / Java 21)"]
        B[API Gateway / REST Controllers]
        C[Application Services & Domain Rules]
        D[(PostgreSQL Database)]
    end

    subgraph Messaging["Async Event Layer (RabbitMQ)"]
        E[RabbitMQ Broker<br/>Exchange & Queues]
        F[Audit & Notification Consumer]
    end

    subgraph DevOps["DevOps & Cloud Ecosystem (AZ-900 Ready)"]
        G[Docker Multi-Stage Builds]
        H[Docker Compose Local Cluster]
        I[GitHub Actions CI Pipeline]
        J[Azure Target: Container Apps / AKS]
    end

    A -->|HTTP REST Síncrono| B
    B --> C
    C -->|JPA / Hibernate| D
    C -->|Publica Evento Asíncrono| E
    E -->|Consume Evento| F
    G -.-> H
    I -.-> G
```

```mermaid
erDiagram
    CUENTA ||--o{ TARJETA : "posee (1 a N)"
    CUENTA ||--o{ TRANSACCION : "registra movimientos (1 a N)"
    TARJETA ||--o{ TRANSACCION : "genera consumos (1 a N)"

    CUENTA {
        Long id PK
        String numeroCuenta
        BigDecimal saldoDisponible
        String titular
    }

    TARJETA {
        Long id PK
        String numeroEnmascarado
        String franquicia
        BigDecimal cupoTotal
        BigDecimal cupoDisponible
        String estado
    }

    TRANSACCION {
        Long id PK
        BigDecimal monto
        String tipo
        String concepto
        LocalDateTime fecha
        String estado
    }
```

---

## 📅 RUTA INTENSIVA DÍA A DÍA (8 AL 13 DE OCTUBRE)

---

### ☕ DÍA 1: Cimientos del Backend con Java 21 & Spring Boot 3
*Objetivo: Construir la API REST de BankCore aplicando Clean Architecture, DTOs con Records, persistencia JPA, transacciones ACID e Idempotencia Financiera.*

- [x] **1.1 Setup de Infraestructura con Gradle:**
  - Configuración de `settings.gradle` y `build.gradle` con Java 21 LTS y Spring Boot 3.3.4.
  - Entendimiento de los 4 alcances de dependencias: `implementation`, `runtimeOnly`, `compileOnly`, `testImplementation`.
- [ ] **1.2 Clean Architecture & Paquetes de Dominio:**
  - Creación física y separación estricta de responsabilidades por paquetes: `controller/`, `service/`, `repository/`, `domain/`, `dto/`, `config/`.
  - Aplicación de **SRP (Single Responsibility Principle)** a nivel de arquitectura.
- [ ] **1.3 Inyección de Dependencias Quirúrgica & SOLID (DIP):**
  - Análisis técnico: por qué la inyección por atributo `@Autowired` es un antipatrón prohibido (oculta dependencias y rompe tests unitarios).
  - Implementación de inyección por constructor con campos inmutables `private final` (o `@RequiredArgsConstructor` de Lombok).
- [ ] **1.4 Contratos Inmutables con Java 21 Records (DTO Pattern):**
  - Creación de DTOs de entrada y salida: `TransferenciaRequestDto`, `TransferenciaResponseDto`, `CuentaResponseDto`.
  - Por qué los `records` reemplazan clases mutables con getters/setters y protegen la integridad de memoria.
- [ ] **1.5 Validación Defensiva de Entradas (Jakarta Validation):**
  - Anotaciones declarativas en DTOs: `@NotNull`, `@Positive`, `@NotBlank`.
  - Patrón *Fail-Fast*: rechazar peticiones maliciosas (ej. montos negativos) en la puerta de enlace (`HTTP 400 Bad Request`).
- [ ] **1.6 La Tríada de Dominio JPA (Entidades de Base de Datos):**
  - Mapeo de entidades relacionales con Hibernate: `CuentaEntity`, `TarjetaEntity` y `TransaccionEntity`.
  - Tipado estricto con `@Entity`, `@Table`, `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)`.
- [ ] **1.7 Relaciones Relacionales & Prevención del Antipatrón N+1:**
  - Relación `@ManyToOne` entre `TarjetaEntity` $\rightarrow$ `CuentaEntity` y `TransaccionEntity` $\rightarrow$ `CuentaEntity`.
  - Por qué la carga perezosa (`FetchType.LAZY`) es obligatoria en banca para evitar consultas SQL explosivas.
- [ ] **1.8 Capa de Persistencia (Spring Data JPA Repositories):**
  - Interfaces `CuentaRepository`, `TarjetaRepository` y `TransaccionRepository` extendiendo `JpaRepository`.
  - Aplicación del **Repository Pattern**: abstraer el acceso a datos sin acoplarse a SQL nativo.
- [ ] **1.9 Lógica Bancaria y Transaccionalidad ACID en Servicios:**
  - Creación de `CuentaService` y `TransferenciaService`.
  - Aplicación de `@Transactional(rollbackFor = Exception.class)` para garantizar atomicidad estricta en el débito y crédito.
- [ ] **1.10 Patrón de Idempotencia Financiera en Código Real:**
  - Creación de `IdempotenciaService` (o tabla de control).
  - Intercepción del header `Idempotency-Key` (UUID) para detectar transacciones repetidas y evitar doble cobro.
- [ ] **1.11 Controladores RESTful & Semántica HTTP:**
  - `CuentaController`, `TarjetaController` y `TransferenciaController` con `@RestController` y `@RequestMapping`.
  - Endpoints: `GET /api/v1/cuentas/{id}`, `GET /api/v1/cuentas/{id}/movimientos`, `GET /api/v1/cuentas/{id}/tarjetas`, `PUT /api/v1/tarjetas/{id}/bloquear`, `POST /api/v1/transferencias`.
  - Respuestas HTTP correctas (`200 OK`, `201 Created`, `400 Bad Request`, `404 Not Found`).
- [ ] **1.12 Manejo Centralizado de Excepciones (RFC 7807):**
  - Creación de `GlobalExceptionHandler` con `@RestControllerAdvice` y `@ExceptionHandler`.
  - Captura de `"Saldo no disponible"` devolviendo el estándar bancario `ProblemDetail` (RFC 7807) sin fugar trazas internas (`StackTraces`).
- [ ] **1.13 Documentación Viva con Swagger / OpenAPI:**
  - Configuración de `springdoc-openapi` y verificación visual de endpoints en `http://localhost:8080/swagger-ui.html`.
- [ ] **1.14 🎙️ Simulacro CoE Día 1:**
  - Batería de 3 preguntas de entrevista resueltas en 20 segundos (*Inyección por constructor, DTOs vs Entidades, Idempotencia*).

---

### 🐰 DÍA 2: Asincronía Financiera & Event-Driven con RabbitMQ
*Objetivo: Implementar mensajería asíncrona real desacoplando la transacción bancaria de la auditoría y configurando Dead Letter Queue (DLQ) en código.*

- [ ] **2.1 Paradigma Event-Driven en Finanzas:**
  - Análisis arquitectónico: por qué una transferencia bancaria jamás debe bloquearse esperando el envío de correos o análisis de fraude.
- [ ] **2.2 Configuración del Broker Spring AMQP:**
  - Definición programática en `RabbitMQConfig.java`: Exchange de tipo Topic (`bank.events`), Queue (`transacciones.audit.queue`) y serializador JSON con Jackson.
- [ ] **2.3 El Productor de Eventos (Event Publisher):**
  - Creación del evento inmutable `TransaccionRealizadaEvent` (Record).
  - Publicación confiable con `RabbitTemplate.convertAndSend()` tras confirmar la transacción en base de datos.
- [ ] **2.4 El Consumidor Resiliente (Event Listener):**
  - Listener con `@RabbitListener(queues = "transacciones.audit.queue")` que procesa la auditoría de seguridad en segundo plano.
- [ ] **2.5 Resiliencia con Dead Letter Queue (DLQ en Código Real):**
  - Configuración programática del Exchange Dead-Letter (`bank.dlx`) y cola DLQ (`transacciones.audit.dlq`).
  - Demostración de resiliencia: si el consumidor de auditoría falla tras reintentos, el mensaje se redirige a la DLQ sin perder dinero ni bloquear el sistema.
- [ ] **2.6 Ingeniería de Rendimiento & Estrés con Grafana k6 (DevOps / SRE):**
  - Creación del script `load-test.js` con **Grafana k6** para disparar miles de peticiones concurrentes contra `POST /api/v1/transferencias`.
  - Validación visual en tiempo real: ver el pico de mensajes en el dashboard de RabbitMQ (:15672) y cómo el sistema absorbe la carga sin pérdida de datos ni bloqueos en PostgreSQL.
- [ ] **2.7 🎙️ Simulacro CoE Día 2:**
  - Batería de 2 preguntas de entrevista resueltas en 20 segundos (*Justificación EDA y arquitectura de una Dead Letter Queue*).

---

### 🔗 DÍA 3: Integración Fullstack (Angular ↔ Spring Boot) & Estrategia de Testing
*Objetivo: Conectar el frontend existente con el backend real aplicando los Módulos 2, 3, 4 y 5 de Angular con pruebas automatizadas.*

- [ ] **3.1 Consumo HTTP Reactivo en Angular (Módulo 2 Angular):**
  - Configuración de CORS en Spring Boot (`@CrossOrigin` en `WebConfig`).
  - Uso de `provideHttpClient()` y consumo de endpoints REST en `CuentasService`.
- [ ] **3.2 Puente Reactivo RxJS a Signals (Módulo 2 Angular):**
  - Transformación reactiva de observables a señales con `toSignal()` para vincular datos vivos a los componentes.
- [ ] **3.3 Formularios Reactivos Bancarios (Módulo 3 Angular):**
  - Formulario de transferencias con `ReactiveFormsModule` y `FormGroup` fuertemente tipado.
  - Validaciones síncronas (`Validators.required`, `Validators.min(1)`) y reactividad en tiempo real.
- [ ] **3.4 Interceptor HTTP de Seguridad e Idempotencia (Módulo 4 Angular):**
  - Creación de un interceptor funcional con `HttpInterceptorFn` para adjuntar el header bancario `Idempotency-Key` (UUID) en cada transacción.
- [ ] **3.5 Optimización de Rendimiento con OnPush (Módulo 5 Angular):**
  - Aplicación de `ChangeDetectionStrategy.OnPush` en componentes de movimientos y tarjetas.
- [ ] **3.6 Pruebas Unitarias en Frontend (Módulo 5 Angular):**
  - Pruebas unitarias de componentes con `TestBed` y Jasmine validando emisión de eventos (`output`) y renderizado.
- [ ] **3.7 Pruebas Unitarias en Backend con JUnit 5 & Mockito (Módulo 6 Java):**
  - Prueba unitaria de lógica de negocio en `TransferenciaService` aislando los repositorios con `@Mock` y `@InjectMocks`.
- [ ] **3.8 Pruebas de Capa Web Backend con MockMvc (Módulo 6 Java):**
  - Prueba de integración de API con **MockMvc** validando que el endpoint devuelva `HTTP 200` y el JSON esperado.
- [ ] **3.9 🎙️ Simulacro CoE Día 3:**
  - Batería de preguntas de entrevista resueltas en 20 segundos (*Pirámide de pruebas, OnPush y Mockito vs MockMvc*).

---

### 🐳 DÍA 4: Contenerización Docker & Seguridad Bancaria Frontend (Módulo 6 Angular + DevOps)
*Objetivo: Empaquetar todo el sistema con Docker, aplicar seguridad web defensiva y demostrar afinidad natural con Azure (AZ-900).*

- [ ] **4.1 Multi-Stage Dockerfile para Angular (Build + Runtime):**
  - Stage 1: Build de producción optimizado con Node.js (`npm run build`).
  - Stage 2: Imagen ultra-ligera con Nginx sirviendo los estáticos compilados y gestionando rutas SPA (`try_files $uri $uri/ /index.html`).
- [ ] **4.2 Endurecimiento de Seguridad Web en Nginx (Módulo 6 Angular):**
  - Archivo `nginx.conf` con cabeceras bancarias estrictas: `Content-Security-Policy` (CSP), `X-Frame-Options: DENY`, `X-Content-Type-Options: nosniff`.
  - Explicación de mitigación contra ataques XSS, Clickjacking y MIME sniffing.
- [ ] **4.3 Multi-Stage Dockerfile para Spring Boot (Módulo 7 Java):**
  - Stage 1: Compilación del artefacto con Gradle (`./gradlew bootJar`).
  - Stage 2: Runtime ligero con Eclipse Temurin JRE 21 Alpine sin usuario root (`USER appuser`) por estándares de ciberseguridad.
- [ ] **4.4 Orquestación Multicontenedor con `docker-compose.yml`:**
  - Definición de 4 servicios: `postgres`, `rabbitmq`, `bankcore-backend`, `bankcore-frontend`.
  - Configuración de redes internas de Docker (`bank-network`), volúmenes persistentes y variables de entorno seguras.
- [ ] **4.5 El Puente DevOps / Azure (AZ-900):**
  - Cómo mapear este proyecto a la nube de Azure:
    - Angular $\rightarrow$ *Azure Static Web Apps* o *Container Apps*.
    - Spring Boot $\rightarrow$ *Azure Container Apps* o *App Service*.
    - RabbitMQ $\rightarrow$ *Azure Service Bus* (o RabbitMQ en clúster).
    - Base de Datos $\rightarrow$ *Azure Database for PostgreSQL Flexible Server*.
- [ ] **4.6 🎙️ Simulacro CoE Día 4:**
  - Batería de preguntas de entrevista resueltas en 20 segundos (*Multi-stage builds, CSP y arquitectura en Azure*).

---

### ⚙️ DÍA 5: CI/CD con GitHub Actions & Documentación Ejecutiva
*Objetivo: Automatizar la integración continua y preparar el repositorio para proyectarlo con orgullo en la sesión grabada.*

- [ ] **5.1 Versionamiento Profesional (Git Flow & Conventional Commits):**
  - Commits limpios y estructurados: `feat:`, `fix:`, `refactor:`, `test:`, `ci:`.
- [ ] **5.2 Pipeline de CI en GitHub Actions:**
  - Creación de `.github/workflows/ci.yml`.
  - Pasos automáticos: Lint de Angular $\rightarrow$ Tests de Frontend $\rightarrow$ Build de Angular $\rightarrow$ Tests de Spring Boot $\rightarrow$ Build del JAR con Gradle.
- [ ] **5.3 README.md de Nivel Arquitectónico:**
  - Diagrama de arquitectura visible.
  - Decisiones de diseño justificadas (ADR - Architectural Decision Records).
  - Guía de inicio rápido con un solo comando: `docker compose up --build`.
  - Instrucciones para ejecutar la prueba de estrés de **Grafana k6**.
- [ ] **5.4 🎙️ Simulacro CoE Día 5:**
  - Batería de preguntas de entrevista resueltas en 20 segundos (*Calidad continua y prevención de regresiones en CI*).

---

### 🎙️ DÍA 6: El Gran Ensayo General (Simulacro CoE de 60 Minutos)
*Objetivo: Dominar la narrativa, el tono y las respuestas exactas de la valoración para asegurar un dictamen positivo.*

- [ ] **6.1 El Pitch de Apertura (2 minutos):**
  - Cómo presentarte: quién eres, qué construiste en BankCore y por qué tiene arquitectura empresarial.
- [ ] **6.2 Defensa de los 5 Pilares del CoE:**
  - **Cómo diseñas:** Clean Architecture, SOLID, desacoplamiento Dumb/Smart y broker de mensajería.
  - **Cómo pruebas:** Pruebas unitarias de componentes y servicios; pruebas de integración con MockMvc; pruebas de estrés con k6.
  - **Cómo integras:** APIs REST síncronas para consultas, RabbitMQ para eventos asíncronos y Docker Compose para el entorno local.
  - **Cómo versionas:** Git con Conventional Commits y ramas protegidas.
  - **Cómo usas la IA:** La IA como acelerador y copiloto de refactor, pero con total control humano sobre las decisiones técnicas.
- [ ] **6.3 Demostración en Vivo Fluida (Las 6 Ventanas del Arsenal):**
  1. Frontend Angular (:4200) operando con Signals.
  2. Swagger UI (:8080/swagger-ui.html) documentando contratos REST y RFC 7807.
  3. Panel de RabbitMQ (:15672) mostrando exchanges, colas y la DLQ.
  4. Terminal ejecutando la prueba de carga masiva con **Grafana k6**.
  5. Docker Desktop mostrando los 4 contenedores operando en verde.
  6. GitHub Actions mostrando el pipeline de CI completado exitosamente.
- [ ] **6.4 Estrategia de Transición hacia DevOps:**
  - Cómo expresar tu interés genuino por DevOps/Cloud presentando tu certificación **AZ-900**, tus pruebas de k6 y la base de Docker/CI que ya dominas.

---

## 📊 Matriz de Competencias a Demostrar ante el CoE

| Pilar del CoE | Lo que ellos evalúan | Lo que demostraremos con BankCore |
| :--- | :--- | :--- |
| **Diseño** | ¿Copia código o sabe estructurar soluciones? | Clean Architecture, Inmutabilidad, SRP, Signals, Records, RabbitMQ, Idempotencia. |
| **Pruebas** | ¿Sabe probar o solo prueba manual en navegador? | Tests unitarios con Jasmine/Jest (Frontend), JUnit 5/Mockito (Backend) y MockMvc. |
| **Rendimiento / SRE** | ¿El sistema colapsa ante alta demanda? | **Pruebas de estrés con Grafana k6** disparando miles de peticiones concurrentes y monitoreo en RabbitMQ. |
| **Integración** | ¿Entiende cómo se comunican los sistemas? | REST JSON síncrono + RabbitMQ asíncrono con Dead Letter Queue (DLQ) para resiliencia. |
| **Versionamiento** | ¿Sabe trabajar en equipo y con estándares? | Git estructurado, Conventional Commits y CI con GitHub Actions. |
| **Uso de IA** | ¿Es un copipegador pasivo o un ingeniero aumentado? | Capacidad de justificar cada línea, cada patrón y cada tecnología elegida. |
| **Aspiración DevOps** | ¿Tiene potencial para proyectos Cloud? | Multi-stage Dockerfiles, Docker Compose, CI, k6 y certificación AZ-900. |

---

## 📝 Bitácora de Sesiones de Batalla

| Fecha | Día / Fase | Hitos Técnicos Logrados | Estado |
| :--- | :--- | :--- | :--- |
| *08-Oct-2026* | Planificación | Creación del Roadmap Estratégico CoE y alineación de prioridades. | 🟩 Completado |
| *09-Oct-2026* | Día 1 & 2 | Backend Spring Boot 3 + REST + RabbitMQ Asíncrono. | ⬜ Pendiente |
| *10-Oct-2026* | Día 3 | Integración Angular ↔ Spring Boot + Testing Frontend/Backend. | ⬜ Pendiente |
| *11-Oct-2026* | Día 4 | AZ-900 + Dockerización Fullstack + Docker Compose. | ⬜ Pendiente |
| *12-Oct-2026* | Día 5 | GitHub Actions CI + README Ejecutivo CoE. | ⬜ Pendiente |
| *13-Oct-2026* | Día 6 | Simulacro Técnico 1-a-1 de 60 minutos (Ensayo General). | ⬜ Pendiente |
| *14-Oct-2026* | **Día D** | **Valoración Oficial Centro de Excelencia (9:00 AM COT).** | 🏆 Meta Final |
