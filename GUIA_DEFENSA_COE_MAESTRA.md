# 🏆 GUÍA MAESTRA DE DEFENSA - VALORACIÓN TÉCNICA COE
## Rol: Senior Backend Software Engineer (Java 21 / Spring Boot 3)
**Proyecto de Referencia:** BankCore Digital (Plataforma Bancaria de Alta Concurrencia e Integridad Transaccional)

---

## 🎯 LA ESTRATEGIA: CÓMO ENFOCAR TU CONVERSACIÓN

> ### 💡 Recordatorio de Oro:
> La invitación oficial del CoE aclara:
> * *"No es un examen ni hay que recitar definiciones."*
> * *"Vamos a recorrer juntos tu forma de trabajar con un proyecto real que puedas explicar a detalle."*
> * *"Los 5 pilares que evaluarán son: **Cómo Diseñas, Cómo Pruebas, Cómo Integras, Cómo Versionas y Cómo usas la IA**."*

Tu enfoque estratégico ganador es presentarte como un **Backend Engineer de alto nivel con mentalidad arquitectónica**, apoyándote en **BankCore** como tu proyecto insigne. Si te preguntan por frontend, dices con total naturalidad:
> *"Para verificar el flujo punta a punta expuse una SPA en Angular 19, pero el corazón de mi diseño, donde invertí el rigor técnico y la ingeniería financiera, es en el Backend con Java 21, Spring Boot y RabbitMQ."*

---

## 🎙️ 1. TU INTRODUCCIÓN NATURAL (1 MINUTO)

> *"Para esta conversación quiero compartirles **BankCore Digital**, un motor transaccional bancario que diseñé para procesar transferencias interbancarias atómicas de alta concurrencia con requerimientos estrictos de integridad contable y auditoría regulatoria.*
>
> *Mi objetivo fue resolver tres retos reales de la industria financiera:*
> 1. * **Desacoplar el negocio bancario de la infraestructura** mediante Clean Architecture Hexagonal.*
> 2. * **Blindar el sistema contra fallos de red y dobles cobros** mediante transacciones ACID y un motor de Idempotencia estricto (RFC 9110).*
> 3. * **Garantizar resiliencia y auditoría asíncrona** desacoplando el cumplimiento normativo (AML) con RabbitMQ y Dead Letter Queues, validado con pruebas de estrés de más de 15,000 transacciones concurrentes con k6.*
>
> *Me encantaría mostrarles cómo estructuré el proyecto a lo largo de los pilares de diseño, testing, integración, versionamiento y cómo uso la IA en mi día a día."*

---

## 🏛️ 2. LOS 5 PILARES DEL COE: GUION DETALLADO Y RESPUESTAS MAESTRAS

---

### PILAR 1: CÓMO DISEÑAS (Diseño de Software & Arquitectura)

#### ¿Qué buscan evaluar?
* ¿Entiende principios de arquitectura limpia o solo crea controladores gigantes que llaman a repositorios?
* ¿Aplica SOLID y patrones de diseño reales en su código?
* ¿Sabe proteger la integridad del dominio del negocio?

#### Tu narrativa técnica para responder:
1. **Clean Architecture Hexagonal Estricta (Ports & Adapters):**
   * Estructuré el código en 3 capas físicas bien diferenciadas:
     * `domain/` (Núcleo Puro): Contiene las entidades (`Cuenta`, `Tarjeta`, `Transaccion`), enums (`EstadoCuenta`, `TipoMovimiento`) y puertos (`CuentaRepositoryPort`, `AuditoriaRepositoryPort`). **Es Java puro, libre de anotaciones de Spring o Hibernate**.
     * `service/` (Capa de Aplicación / Casos de Uso): Orquesta las reglas de negocio bancario llamando únicamente a interfaces de puertos.
     * `infrastructure/` (Adaptadores Técnicos): Contiene JPA entities, repositorios de Spring Data, controladores REST, DTOs y consumers de RabbitMQ.
   * *La justificación de negocio:* Si mañana la organización decide cambiar PostgreSQL por DynamoDB o migrar a microservicios en Quarkus, el núcleo financiero de BankCore no cambia ni una sola línea.

2. **Inmutabilidad Defensiva con Java 21 Records:**
   * Utilicé `record` para todos los DTOs de entrada y salida (`TransferenciaRequestDto`, `CuentaResponseDto`).
   * *Por qué:* Los Records eliminan getters/setters mutables, garantizan inmutabilidad en memoria entre hilos y ofrecen `equals/hashCode` libre de errores humanos.

3. **Inyección de Dependencias Quirúrgica (DIP):**
   * Eliminé por completo la inyección por atributo `@Autowired` (antipatrón que oculta dependencias y dificulta tests unitarios).
   * Uso inyección por constructor mediante campos inmutables `private final` con `@RequiredArgsConstructor` de Lombok.

4. **Idempotencia Financiera (RFC 9110):**
   * Explica cómo evitaste dobles cobros:
   > *"En banca, un timeout de red no significa que la transacción falló. Si el cliente reintenta, el sistema debe ser idempotente. Implementé una entidad `RegistroIdempotenciaJpaEntity` ligada a una cabecera `Idempotency-Key` (UUID). Si la clave ya existe, devuelvo la respuesta previamente memorizada con `HTTP 201` sin volver a debitar fondos."*

---

### PILAR 2: CÓMO PRUEBAS (Estrategia Integral de Testing & Calidad)

#### ¿Qué buscan evaluar?
* ¿Solo prueba "haciendo clic en la pantalla" o tiene una pirámide de pruebas automatizada?
* ¿Sabe mockear dependencias correctamente?
* ¿Mide la calidad real de los tests (cobertura vs efectividad)?

#### Tu narrativa técnica para responder:
1. **La Pirámide de Pruebas Automatizadas de BankCore:**
   * **Pruebas Unitarias Aisladas (Base de la pirámide):**
     * Usé **JUnit 5 + Mockito** (`@Mock`, `@InjectMocks`) en `TransferenciaServiceTest`.
     * Pruebo todos los casos de borde en milisegundos: saldos insuficientes, cuentas inactivas, transferencias con monto negativo, transferencias hacia la misma cuenta.
   * **Pruebas de Capa Web (Controladores REST):**
     * Usé **MockMvc** (`CuentaControllerTest`, `GlobalExceptionHandlerTest`) para probar la puerta de enlace HTTP: validación de Bean Validation (`@Valid`, `@Positive`), serialización JSON y códigos de estado HTTP (`200 OK`, `201 Created`, `400 Bad Request`).
   * **Pruebas de Integración con Base de Datos:**
     * Con base de datos H2 en memoria para validar queries y restricciones de clave única (`numero_cuenta UNIQUE`).

2. **Métricas Reales de Calidad (No solo "pasar verde"):**
   * **JaCoCo:** Alcancé **100% de cobertura de código** en los servicios transaccionales críticos.
   * **Mutation Testing con PITest:** Explica este concepto avanzado (impresiona muchísimo al jurado):
     > *"No me quedo solo con la cobertura de líneas de JaCoCo, porque una prueba sin asserts puede dar 100% de cobertura falsa. Ejecuté **PITest**, que inyecta mutaciones (cambia `<=` por `<`, altera retornos) para verificar que mis asserts realmente fallen y 'maten' al mutante. Logré 0 mutantes sobrevivientes en la lógica de transferencias."*

3. **Pruebas de Estrés y Rendimiento (DevOps / SRE):**
   * Menciona tu test con **Grafana k6**:
     > *"Para asegurar que el diseño soporte ráfagas bancarias concurrentes, diseñé un script en k6 simulando 100 usuarios virtuales concurrentes. El sistema procesó **15,980 transacciones a 532.5 TPS con 0.00% de tasa de fallo y un p95 de 80.62ms**."*

---

### PILAR 3: CÓMO INTEGRAS (Integración de Sistemas & Arquitectura Distribuida)

#### ¿Qué buscan evaluar?
* ¿Sabe cómo comunicar sistemas de forma síncrona y asíncrona?
* ¿Qué hace cuando un servicio externo o una cola falla?
* ¿Aplica patrones de resiliencia?

#### Tu narrativa técnica para responder:
1. **Integración Síncrona RESTful:**
   * Endpoints semánticos con versionamiento `/api/v1/cuentas`, `/api/v1/transferencias`.
   * Manejador global de excepciones con `@RestControllerAdvice` retornando el estándar **RFC 7807 (ProblemDetail)** para que el cliente reciba errores estructurados (`status`, `detail`, `timestamp`).
   * Configuración de CORS con orígenes restringidos y cabeceras expuestas.

2. **Integración Asíncrona con RabbitMQ (Event-Driven Architecture):**
   * *El problema resuelto:* La auditoría legal y contra lavado de activos (AML) no debe frenar la velocidad de la transferencia ni bloquear registros en la base de datos transaccional.
   * *La topología:*
     * Un **Topic Exchange** (`bankcore.exchange`).
     * Routing key: `transaccion.creada`.
     * Cola durable `auditoria.transacciones`.
   * *El patrón Dead Letter Queue (DLQ):*
     > *"Si un evento de auditoría viene mal formateado o la base de datos de auditoría falla, no reintentamos infinitamente porque saturaría la CPU. Con la política de RabbitMQ y `@AmqpRejectAndDontRequeue`, el mensaje envenenado se expulsa de la cola principal y se desvía a `dlq.transacciones` para inspección forense sin interrumpir el flujo del banco."*

---

### PILAR 4: CÓMO VERSIONAS (Gitflow, Commits & Trabajo en Equipo)

#### ¿Qué buscan evaluar?
* ¿Sabe trabajar con flujos de Git estructurados o hace commits caóticos directo a main?
* ¿Escribe mensajes de commit claros que cuenten la historia del desarrollo?
* ¿Entiende cómo se automatiza la validación en equipo?

#### Tu narrativa técnica para responder:
1. **Estrategia de Ramificación (Gitflow Estricto):**
   * `main`: Código productivo y estable.
   * `develop`: Rama base de integración protegida con Pull Requests.
   * Ramas de trabajo nombradas por funcionalidad: `feature/hexagonal-core-banking`, `feature/rabbitmq-event-driven`, `feature/fullstack-angular-integration`.
   * Ningún cambio entra sin un Pull Request revisado y fusionado formalmente.

2. **Conventional Commits:**
   * Utilizo el estándar de la industria para mensajes atómicos y semánticos:
     * `feat(core): implement atomic transfer service with ACID isolation`
     * `fix(rest): handle idempotency collision with RFC 7807 problem detail`
     * `test(service): add unit tests for transfer edge cases with Mockito`
     * `refactor(domain): extract account state validation to domain enum`
   * *Por qué:* Permite generar changelogs automáticos, facilita el bisect de bugs y documenta la intención de cada cambio.

3. **Integración Continua (CI con GitHub Actions):**
   * Archivo `.github/workflows/ci.yml` que en cada PR compila el código, corre los linters y ejecuta la suite de tests unitarios antes de permitir la fusión.

---

### PILAR 5: CÓMO TE APOYAS EN LA IA EN EL DÍA A DÍA (Ingeniero Aumentado)

#### ¿Qué buscan evaluar?
* ¿Usa la IA como un "oráculo que copia y pega a ciegas"? (Antipatrón Júnior).
* O por el contrario: ¿Usa la IA como un multiplicador de productividad, manteniendo el criterio técnico, la verificación y el control de calidad? (Patrón Senior).

#### Tu narrativa técnica para responder:
> *"Para mí, la inteligencia artificial no es un reemplazo del criterio de ingeniería, sino un **Senior Pair Programmer** que multiplica mi velocidad de entrega en cuatro áreas específicas:*
>
> 1. * **Generación de Casos de Borde en Pruebas:** Le pido a la IA que analice mis modelos de dominio y me proponga escenarios límite (ej. transferencias en límites de decimales, desbordamientos de saldo, claves de idempotencia duplicadas en concurrencia).*
> 2. * **Refactorización y Reducción de Boilerplate:** Uso IA para transformar código imperativo en streams funcionales de Java 21, o para generar mappers y configuraciones repetitivas de infraestructura.*
> 3. * **Simulaciones y Escenarios de Carga:** Me apoyo en la IA para estructurar escenarios de prueba en Grafana k6 o manifiestos de Docker Compose con rapidez.*
> 4. * **El Principio del Piloto Responsable:** Nunca acepto código generado sin someterlo a tres filtros obligatorios:
>    - Lo reviso línea por línea para asegurar que respete la arquitectura hexagonal.
>    - Ejecuto `./gradlew test` con JaCoCo y PITest para verificar que los tests pasen y no haya mutantes.
>    - Lo valido en ejecución real con peticiones HTTP en Postman.*
> *La IA propone y acelera, pero el diseño, la responsabilidad y las decisiones arquitectónicas son 100% mías."*

---

## 📋 3. HOJA DE RUTA DE LA CONVERSACIÓN (TU GUÍA DE PANTALLA COMPARTIDA)

Cuando te digan: *"Santiago, muéstranos el proyecto que trajiste"*, sigue exactamente este orden de 4 pasos (te tomará solo 5 minutos):

```text
PASO 1: Estructura de Paquetes en el IDE (1 min)
  Muestra el árbol de paquetes de bankcore-backend:
  ├── domain (puro: model, port, enums)
  ├── service (casos de uso)
  └── infrastructure (persistence, rest, messaging)
  -> Di: "Aquí apliqué Clean Architecture Hexagonal para aislar el negocio de la tecnología".

PASO 2: El Servicio Transaccional Atómico (1.5 min)
  Abre TransferenciaService.java:
  -> Muestra @Transactional.
  -> Muestra la validación de la Idempotencia antes de transferir.
  -> Muestra la publicación del evento a RabbitMQ.

PASO 3: Las Pruebas y la Calidad (1.5 min)
  Abre el reporte de JaCoCo (build/reports/jacoco/test/html/index.html) o corre ./gradlew test:
  -> Muestra el 100% de cobertura de transferencias y la suite de JUnit 5 + MockMvc.
  -> Menciona los resultados de k6 (15,980 transacciones a 532 TPS).

PASO 4: RabbitMQ y la Resiliencia (1 min)
  Muestra RabbitMQConfig.java o la consola web de RabbitMQ (localhost:15672):
  -> Muestra el Topic Exchange, la cola de auditoría y la Dead Letter Queue (DLQ).
```

---

## 🧘 4. CONSEJOS CLAVE PARA EL DÍA DE LA EVALUACIÓN

1. **La sesión es tuya:** Ellos están allí para escucharte y ayudarte a ubicar tu nivel, no para atacarte.
2. **Habla con aplomo y calma:** Cuando uses términos como *Hexagonal*, *ACID*, *Idempotencia*, *DLQ* o *Fail-Fast*, hazlo con la seguridad de quien ya los programó y vio los tests en verde.
3. **Si te preguntan algo que no sabes:** 
   Usa la respuesta Senior: *"No he implementado ese caso específico en este proyecto, pero basándome en los principios de diseño que uso, lo abordaría investigando X o aislando la responsabilidad en Y"*.
4. **Descansa:** Ya tienes todo el código listo, los commits ordenados, las pruebas al 100% y este guion en la mano. Llega descansado y con la cámara encendida con confianza.
