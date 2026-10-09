# ☕ PENSUM & TRACKING DE INGENIERÍA: JAVA 21 & SPRING BOOT 3 ENTERPRISE

> **Estudiante:** Software Engineer en Formación Acelerada  
> **Ecosistema:** Java 21 LTS + Spring Boot 3.3+ + PostgreSQL + RabbitMQ + Docker  
> **Enfoque:** Arquitectura Limpia, Concurrencia Financiera, Resiliencia y Grado Bancario  
> **Estado General:** 📋 Hoja de Ruta Maestra de Backend  
> **Última Actualización:** 2026-10-08  

---

## 📌 Leyenda de Estados
- 🟩 **`[x]` Completado:** Concepto dominado, implementado con código limpio y defendido bajo desafío.
- 🟨 **`[/]` En Curso:** Tema actual en estudio o implementación activa.
- ⬜ **`[ ]` Pendiente:** Siguiente paso en la ruta pedagógica de backend.

---

## 🏛️ Filosofía Arquitectónica del Backend Bancario
Un backend empresarial no es un simple conjunto de controladores CRUD. Se rige por 4 mandamientos inquebrantables:
1. **Inmutabilidad y Tipado Estricto:** Transferencia de datos mediante `records` de Java 21, previniendo estados corruptos en memoria.
2. **Inversión de Control & Inyección Segura:** Inyección exclusiva por constructor con inmutabilidad (`final`), prohibiendo el uso de `@Autowired` en campos privados.
3. **Integridad Transaccional ACID:** Ninguna operación financiera queda a medias; rollback automático y bloqueos de concurrencia ante transferencias simultáneas.
4. **Desacoplamiento Orientado a Eventos:** Las operaciones críticas responden de inmediato de forma síncrona; auditorías, alertas y notificaciones viajan asíncronamente vía brokers de mensajería (RabbitMQ).

---

## ☕ MÓDULO 0: Fundamentos Quirúrgicos de Java 21 & JVM Moderna
*Objetivo: Dominar la máquina virtual, la gestión de memoria y las características modernas del lenguaje Java.*

- [ ] **0.1 Anatomía de la JVM & Gestión de Memoria:**
  - Ciclo de vida del código: Código fuente (`.java`) $\rightarrow$ Bytecode (`.class`) $\rightarrow$ JVM / JIT Compiler.
  - Memoria: **Stack** (hilos, referencias y primitivos locales) vs **Heap** (instancias de objetos y ciclo de vida de memoria).
  - Garbage Collection: Cómo funciona y qué rol juegan los recolectores modernos (G1GC y ZGC).
- [ ] **0.2 Java 17 a 21 Moderno en Producción:**
  - Inmutabilidad pura con **`record`**: Por qué reemplaza a clases con Getters/Setters y por qué no usar Lombok para DTOs.
  - **`sealed class` y `sealed interface`**: Control estricto de jerarquías de herencia financiera en tiempo de compilación.
  - **Pattern Matching** para `switch` y `instanceof`: Eliminación de castings inseguros.
  - **Virtual Threads (Project Loom):** Millones de hilos concurrentes livianos frente a los hilos pesados del sistema operativo.
- [ ] **0.3 Sistema de Tipos, Generics & Null-Safety:**
  - Generics avanzados: Invarianza, Covarianza (`? extends T`) y Contravarianza (`? super T`).
  - Tipo `Optional<T>`: Buenas prácticas (retornos de métodos) vs antipatrones (campos de clase o parámetros de método).
- [ ] **0.4 Programación Funcional & Streams API:**
  - Interfaces funcionales nativas (`Predicate`, `Function`, `Consumer`, `Supplier`).
  - Streams avanzados: `map`, `filter`, `flatMap`, `reduce`, y recolectores inmutables (`Collectors.toUnmodifiableList()`).
- [ ] **0.5 Herramientas de Construcción:**
  - **Maven:** Anatomía de `pom.xml`, fases del ciclo de vida (`clean`, `compile`, `test`, `package`, `install`), gestión de dependencias y plugins.
  - **Gradle:** Concepto de tareas (`tasks`), `build.gradle`, wrappers (`gradlew`) y compilación incremental.

---

## 🍃 MÓDULO 1: Arquitectura de Spring Boot 3 & Clean Architecture
*Objetivo: Comprender el contenedor de Inversión de Control, la inyección de dependencias y la separación limpia de capas.*

- [ ] **1.1 Contenedor IoC & Ciclo de Vida de los Beans:**
  - Qué es el ApplicationContext y cómo administra la fábrica de objetos.
  - Estereotipos de Spring: `@Component`, `@Service`, `@Repository`, `@Controller`, `@RestController`.
  - Configuración programática: `@Configuration` y métodos con `@Bean`.
  - Ciclo de vida: Inicialización (`@PostConstruct`) y destrucción (`@PreDestroy`).
- [ ] **1.2 Inyección de Dependencias Quirúrgica:**
  - Por qué la inyección por atributo `@Autowired private MiServicio s;` es un antipatrón en producción (impide pruebas unitarias y oculta dependencias circulares).
  - La regla de oro: Inyección por constructor con variables `private final` (o con `@RequiredArgsConstructor` de Lombok).
- [ ] **1.3 Gestión de Configuración & Ambientes (Profiles):**
  - Archivos `application.yml` estructurados.
  - Inyección tipada con `@ConfigurationProperties` mediante Java Records.
  - Separación de ambientes con perfiles: `application-dev.yml`, `application-prod.yml`.
- [ ] **1.4 Arquitectura Limpia (Hexagonal / Clean Architecture):**
  - Separación en capas:
    - `infrastructure`: Controladores web, adaptadores de base de datos, colas.
    - `application`: Casos de uso y orquestación de servicios.
    - `domain`: Reglas de negocio puras, entidades ricas y puertos (interfaces agnósticas de Spring).

---

## 🌐 MÓDULO 2: APIs RESTful de Alto Nivel & Validación Defensiva
*Objetivo: Construir APIs financieras seguras, contratos OpenAPI y manejo de errores estandarizado.*

- [ ] **2.1 Controladores RESTful & Mapeo HTTP:**
  - Semántica HTTP correcta: `GET`, `POST`, `PUT`, `PATCH`, `DELETE` con códigos de estado adecuados (`200 OK`, `201 Created`, `204 No Content`, `400 Bad Request`, `404 Not Found`, `409 Conflict`).
  - Uso correcto de `@PathVariable`, `@RequestParam`, `@RequestBody`, `@RequestHeader`.
- [ ] **2.2 DTOs (Data Transfer Objects) & Mapeo con MapStruct:**
  - Aislamiento absoluto: Por qué NUNCA se debe retornar una `@Entity` de base de datos en un `@RestController`.
  - Uso de **Records** para Request y Response DTOs.
  - Mapeadores automáticos y eficientes en tiempo de compilación con MapStruct.
- [ ] **2.3 Validación Defensiva de Entradas (Jakarta Validation):**
  - Anotaciones estándar: `@NotNull`, `@NotBlank`, `@Positive`, `@Email`, `@Size`, `@Digits`.
  - Creación de validadores personalizados (`@ConstraintValidator`) para reglas bancarias (ej. validar algoritmo de número de cuenta).
- [ ] **2.4 Manejo Global de Excepciones & Estándar RFC 7807 (ProblemDetail):**
  - `@RestControllerAdvice` y `@ExceptionHandler` centralizados.
  - Estructuración de respuestas de error con el estándar moderno de Spring Boot 3: `ProblemDetail` (RFC 7807), evitando la fuga de trazas internas del servidor (`StackTrace`).
- [ ] **2.5 Contratos Vivos con OpenAPI 3 / Swagger:**
  - Integración de `springdoc-openapi-starter-webmvc-ui`.
  - Documentación de modelos y códigos de respuesta mediante anotaciones `@Operation`, `@ApiResponse`.

---

## 💾 MÓDULO 3: Persistencia Avanzada, Concurrencia & JPA (Spring Data JPA)
*Objetivo: Dominar la persistencia relacional con PostgreSQL, transacciones atómicas y control de concurrencia financiero.*

- [ ] **3.1 Fundamentos de Hibernate & Estados de Entidad:**
  - Ciclo de vida JPA: *Transient*, *Managed/Persistent*, *Detached*, *Removed*.
  - Entendiendo el First-Level Cache (Persistence Context) y el comando `flush()`.
- [ ] **3.2 Modelado de Relaciones y el Peligro del Antipatrón N+1:**
  - Relaciones `@OneToMany`, `@ManyToOne`, `@JoinColumn`.
  - FetchType: Por qué `FetchType.EAGER` está prohibido en producción y siempre usamos `FetchType.LAZY`.
  - Detección y erradicación del problema N+1 mediante `JOIN FETCH` y `@EntityGraph`.
- [ ] **3.3 Transaccionalidad Bancaria ACID (`@Transactional`):**
  - Propiedades ACID (Atomicidad, Consistencia, Aislamiento, Durabilidad).
  - Propagación de transacciones: `REQUIRED`, `REQUIRES_NEW`, `MANDATORY`.
  - Rollback defensivo: Por qué Spring por defecto solo hace rollback ante excepciones no verificadas (`RuntimeException`) y cómo solucionarlo con `rollbackFor = Exception.class`.
- [ ] **3.4 Control de Concurrencia en Transferencias Simultáneas:**
  - **Bloqueo Optimista (`@Version`):** Manejo de versiones numéricas para evitar sobreescritura de datos (`OptimisticLockException`).
  - **Bloqueo Pesimista (`PESSIMISTIC_WRITE`):** Bloqueo a nivel de fila en PostgreSQL (`SELECT ... FOR UPDATE`) para cuentas con alta concurrencia de débitos.
- [ ] **3.5 Migración y Versionamiento de Base de Datos:**
  - Por qué `ddl-auto: update` está prohibido en producción.
  - Versionamiento reproducible de esquemas con **Flyway** (scripts SQL versionados: `V1__init.sql`, `V2__add_cards.sql`).

---

## 🐰 MÓDULO 4: Asincronía, Mensajería & Arquitectura Orientada a Eventos (RabbitMQ)
*Objetivo: Desacoplar microservicios bancarios y procesar eventos masivos sin bloquear hilos HTTP.*

- [ ] **4.1 El Paradigma Event-Driven en Finanzas:**
  - La diferencia entre RPC/HTTP (síncrono y bloqueante) y Mensajería Asíncrona (publicar y continuar).
- [ ] **4.2 Anatomía de RabbitMQ & Spring AMQP:**
  - Conceptos de AMQP: Broker, VHost, Exchanges (`Direct`, `Topic`, `Fanout`, `Headers`), Queues y Bindings.
  - Serialización segura de mensajes en JSON con `Jackson2JsonMessageConverter`.
- [ ] **4.3 Productor de Eventos (Event Publisher):**
  - Envío confiable mediante `RabbitTemplate`.
  - Publicación del evento `TransaccionRealizadaEvent`.
- [ ] **4.4 Consumidor Resiliente (Event Listener):**
  - Implementación con `@RabbitListener`.
  - Modos de confirmación (ACK automático vs ACK manual) para garantizar que ningún mensaje financiero se pierda en un fallo de servidor.
- [ ] **4.5 Patrones Avanzados de Resiliencia:**
  - **Dead Letter Queue (DLQ):** Manejo de mensajes venenosos o fallidos tras reintentos fallidos.
  - **Transactional Outbox Pattern (Conceptual):** Cómo garantizar consistencia entre el commit en base de datos y la publicación en RabbitMQ sin transacciones distribuidas 2PC.

---

## 🔒 MÓDULO 5: Seguridad Bancaria en Backend (Spring Security & JWT)
*Objetivo: Proteger recursos financieros con autenticación sin estado, autorización por roles y protección contra ataques comunes.*

- [ ] **5.1 Arquitectura del Security Filter Chain:**
  - Cómo fluye una petición HTTP a través de la cadena de filtros de servlets de Spring Security.
  - Configuración moderna basada en componentes (`SecurityFilterChain`) sin heredar de clases obsoletas (`WebSecurityConfigurerAdapter`).
- [ ] **5.2 Autenticación Stateless con Tokens JWT:**
  - Estructura de un token JWT: Header, Payload (Claims), Signature criptográfica.
  - Filtro personalizado `JwtAuthenticationFilter` que valida la firma en cada petición y puebla el `SecurityContextHolder`.
- [ ] **5.3 Autorización Declarativa:**
  - Control de acceso por roles y autoridades con `@PreAuthorize("hasRole('ADMIN')")` y `@PreAuthorize("hasAuthority('TRANSACCION_WRITE')")`.
- [ ] **5.4 Endurecimiento de Seguridad en API:**
  - Protección contra CSRF en APIs sin estado (`csrf.disable()`).
  - Configuración estricta de CORS (`CorsConfigurationSource`) permitiendo únicamente orígenes autorizados del frontend.

---

## 🧪 MÓDULO 6: Pirámide de Pruebas & Calidad de Código (Testing)
*Objetivo: Escribir suites de pruebas automatizadas que den confianza total para desplegar a producción sin miedo.*

- [ ] **6.1 Pruebas Unitarias de Lógica Pura (JUnit 5 + AssertJ):**
  - Estructura AAA: *Arrange, Act, Assert*.
  - Aserciones legibles y expresivas con AssertJ (`assertThat(...)`).
- [ ] **6.2 Aislamiento de Servicios con Mockito:**
  - Simulación de dependencias con `@Mock`, `@InjectMocks`.
  - Estilo BDD: `BDDMockito.given(...)` y `BDDMockito.then(...)`.
  - Verificación de interacciones con `verify(repositorio, times(1)).save(any())`.
- [ ] **6.3 Pruebas de Capa Web (MockMvc):**
  - Pruebas rápidas con `@WebMvcTest(CuentaController.class)`.
  - Validación de códigos HTTP, cabeceras y estructura de JSON retornado (`jsonPath("$.saldo").value(...)`).
- [ ] **6.4 Pruebas de Integración Reales con Testcontainers:**
  - Por qué usar bases de datos en memoria H2 en pruebas no es suficiente (H2 no se comporta igual a PostgreSQL).
  - Uso de **Testcontainers** para levantar instancias reales y efímeras de PostgreSQL y RabbitMQ en Docker durante las pruebas de integración (`@SpringBootTest`).

---

## 🚀 MÓDULO 7: Observabilidad, DevOps & Cloud-Native
*Objetivo: Monitorizar el backend en tiempo real, empaquetarlo en Docker y prepararlo para la nube.*

- [ ] **7.1 Observabilidad Operativa con Spring Boot Actuator:**
  - Endpoints de salud: `/actuator/health` (liveness y readiness probes para Kubernetes/Azure).
  - Indicadores personalizados: `HealthIndicator` para verificar la conexión con servicios bancarios externos.
- [ ] **7.2 Métricas y Trazabilidad:**
  - Métricas con **Micrometer** exportables a Prometheus.
  - Trazas distribuidas con OpenTelemetry / Micrometer Tracing para seguir una transacción desde el frontend hasta la base de datos.
- [ ] **7.3 Contenerización Óptima con Docker:**
  - Creación de `Dockerfile` multi-stage: compilación de dependencias por capas y runtime en imagen ultra-ligera (Eclipse Temurin JRE Alpine).
  - Ejecución sin usuario root (`USER appuser`) por estándares de ciberseguridad financiera.
- [ ] **7.4 Arquitectura para la Nube (Azure / AWS):**
  - Despliegue en **Azure Container Apps** o **AKS (Kubernetes)**.
  - Gestión de secretos y cadenas de conexión mediante variables de entorno seguras o Azure Key Vault.

---

## 📊 Matriz de Competencias Seniority en Backend Java / Spring Boot

| Nivel | Lo que domina técnicamente | Comportamiento en Producción |
| :--- | :--- | :--- |
| **Junior** | Sintaxis básica de Java, crea controladores y repositorios básicos con Spring Data JPA. | Usa `@Autowired` en atributos, no usa transacciones explícitas, expone entidades directamente en controladores. |
| **Semi-Senior** | Domina DTOs con `records`, validaciones Jakarta, manejo global de excepciones con RFC 7807, transacciones ACID con `@Transactional`, y escribe pruebas unitarias e integración con Mockito y MockMvc. | Sabe por qué ocurre el problema N+1, maneja migraciones con Flyway y separa microservicios con colas asíncronas (RabbitMQ). |
| **Senior** | Arquitectura Hexagonal/Clean, concurrencia avanzada con bloqueos optimistas/pesimistas, observabilidad con Actuator/Prometheus, Testcontainers, y diseño para la nube con Docker y CI/CD. | Toma decisiones fundamentadas de resiliencia (Outbox Pattern, Circuit Breakers), cuida el rendimiento de la JVM y lidera la seguridad del sistema. |

---

## 📝 Bitácora de Sesiones de Aprendizaje

| Sesión | Fecha | Módulo Abordado | Hitos Logrados | Estado |
| :--- | :--- | :--- | :--- | :--- |
| #01 | 2026-10-08 | Planificación Estratégica | Creación del Pensum Integral de Java 21 y Spring Boot 3 Enterprise. | 🟩 Conquistado |
