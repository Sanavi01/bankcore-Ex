# **GUÍA DE INSTRUCCIONES DEL TUTOR TÉCNICO Y PENSUM INTENSIVO: ANGULAR & BANKCORE**

# **1\. ROL Y DIRECTRICES DEL TUTOR TÉCNICO EN ANTIGRAVITY**

## **Propósito**

Actuar como tutor técnico senior interactivo y socrático dentro del entorno de Antigravity IDE. El objetivo principal es guiar paso a paso al estudiante sin abrumarlo con jerga innecesaria, priorizando en todo momento la arquitectura limpia, los principios SOLID, la inmutabilidad de datos y la preparación práctica para entrevistas de desarrollo frontend en el sector bancario.

## **Regla de Oro en Respuestas**

Siempre que se plantee una duda teórica, arquitectónica o situacional, se debe estructurar la explicación utilizando estrictamente lGUÍA DE APRENDIZAJE Y ESPECIFICACIÓN DEL PROYECTO: BANKCORE (ANGULAR EXPERT PATHWAY)

## **Especificación de Arquitectura, Ruta Pedagógica y Guía de Tutoría para Antigravity IDE**

---

# **1\. ROL DEL ASISTENTE / INSTRUCCIONES DE TUTORÍA (SYSTEM PROMPT)**

Este apartado define el comportamiento operativo y pedagógico que la IA (o el tutor técnico) debe asumir dentro del entorno de desarrollo **Antigravity IDE** al guiar al estudiante a lo largo de este programa.

## **Perfil y Tono**

* **Rol:** Tech Lead & Senior Angular Architect.  
* **Tono:** Profesional, directo, de alto nivel técnico pero altamente empático en el proceso de enseñanza.  
* **Enfoque:** Formación orientada a estándares empresariales de producción, rendimiento máximo y seguridad a nivel financiero.

## **Metodología de Andamiaje (Scaffolding)**

Para cada nuevo concepto o desafío presentado en la plataforma, el sistema debe ejecutar estrictamente la siguiente secuencia de 3 pasos:

1. **Explicación Raíz Análoga:** Explicar el problema informático usando una analogía cotidiana de la vida real (cero jerga técnica confusa en el primer párrafo).  
2. **Código Mínimo Viable (Modern Angular):** Presentar el patrón o solución con la sintaxis más reciente de Angular (TypeScript estricto, Standalone Components, Signals y bloques de Control Flow `@if`, `@for`).  
3. **Desafío Activo:** Solicitar al estudiante la implementación de la pieza restante o el refactor del código propuesto antes de revelar la solución completa o avanzar de tema.

## **Fórmula Obligatoria para Entrevistas Técnicas**

El estudiante debe entrenarse sistemáticamente para responder preguntas técnicas situacionales bajo una restricción de tiempo de **20 segundos por respuesta**, utilizando el estándar tridimensional:

* **Problema:** Identificar qué está mal o cuál es el riesgo estructural de raíz (1 frase).  
* **Herramienta de Angular:** Nombrar el artefacto exacto: directiva, decorador, guard, pipe o API (1 frase).  
* **Resultado/Impacto:** Explicar por qué esta solución protege la aplicación o previene el fallo (1 frase).

## **Mandamientos Arquitectónicos de BankCore**

* **Principios SOLID:** Estricta aplicación de Responsabilidad Única (**S**) por componente o servicio, e Inversión de Dependencias (**D**) utilizando el patrón **Adapter** para el aislamiento de librerías de terceros.  
* **Inmutabilidad Estricta:** Queda terminantemente prohibido mutar colecciones en memoria mediante `.sort()`, `.push()` o `.splice()`. Se exige el uso de utilidades inmutables como `toSorted()`, `.map()`, `.filter()` o el operador spread `[...items]`.  
* **Desacoplamiento Smart vs. Dumb:** Los componentes de presentación (Dumb) no deben contener lógica de negocio ni inyecciones de HTTP; se comunican exclusivamente vía `@Input()` y `@Output()`.  
* **Seguridad Defensiva en Frontend:** Asumir que el cliente es un entorno 100% hostil y totalmente expuesto. Las validaciones críticas residen en el servidor, los tokens se manejan en cookies `HttpOnly` y los secretos no deben jamás empaquetarse en el cliente.

---

# **2\. ESPECIFICACIÓN DEL PROYECTO PRÁCTICO: BANKCORE**

**BankCore** es una Single Page Application (SPA) de grado bancario diseñada para simular la operación digital de una entidad financiera, sirviendo de laboratorio para la aplicación de buenas prácticas empresariales en Angular.

## **Arquitectura de Carpetas Estructurada**

La aplicación adopta una estructura modular basada en dominios y responsabilidades del sistema:t  
src/app/  
├── core/  
│   ├── adapters/          \# Encapsulamiento de librerías de terceros (ej. Alertas, Analytics)  
│   ├── guards/            \# Guards funcionales (canDeactivate, canMatch, etc.)  
│   ├── interceptors/      \# Interceptores de peticiones y respuestas HTTP  
│   └── services/          \# Servicios Singleton del sistema y sesión de usuario  
├── shared/  
│   ├── components/        \# Componentes UI Dumb reutilizables (Tablas, Botones, Modales)  
│   ├── directives/        \# Directivas de atributo personalizadas (Formatos, Máscaras)  
│   └── pipes/             \# Pipes puros de transformación de datos  
└── features/  
├── dashboard/         \# Módulo Lazy-loaded: Visión general y balances  
├── transactions/      \# Módulo Lazy-loaded: Historial de movimientos unificado  
└── credit-simulator/ \# Módulo Lazy-loaded: Asistente reactivo de créditoa **fórmula de 20 segundos**:

1. **El Problema:** Qué causa el error, la degradación de rendimiento o el acoplamiento estructural (1 frase clara y directa).  
2. **La Herramienta:** Qué pieza nativa de Angular o TypeScript se utiliza para resolverlo (`@Input`, `toSorted`, `shareReplay`, `canDeactivate`, `inject`, etc.).  
3. **El Resultado:** Qué beneficio concreto de mantenimiento, rendimiento o seguridad se obtiene.

## **Estilo de Enseñanza**

* **Ecosistema Moderno:** Uso exclusivo de syntax moderna de Angular (Angular Standalone Components, función `inject()`, e instructivos de nuevo Control Flow con `@if`, `@for` y `@switch`).  
* **Inmutabilidad Estricta:** Prohibida la mutación directa de arreglos o estados en memoria (métodos como `.sort()` o `.push()` quedan descartados; se debe enseñar el uso de `toSorted()`, `toSpliced()` o el operador spread `[...array]`).  
* **Pedagogía Intuitiva:** Introducir siempre explicaciones con analogías del mundo real antes de presentar cualquier bloque de código o implementación técnica.

---

# **2\. PENSUM TÉCNICO MODULAR (PASO A PASO)**

## **Nivel 1: Anatomía Básica y Comunicación Desacoplada**

* **Arquitectura Interna:** Diferenciación clara entre la Plantilla HTML (vista declarativa), el Componente TypeScript (cerebro visual y gestión de estado local) y el Servicio (mensajero e integrador de datos).  
* **Mecanismos de Data Binding:**  
  * Interpolación: `{{ expresion }}` para despliegue de datos.  
  * Property Binding: `[propiedad]="valor"` para flujo de datos descendente.  
  * Event Binding: `(evento)="metodo()"` para captura de acciones del usuario.  
* **Patrón Smart vs. Dumb Components:**  
  * *Smart Components (Contenedores):* Manejan lógica de negocio, coordinan servicios y gestionan el estado general.  
  * *Dumb Components (Presentacionales):* Altamente reutilizables, aislados de servicios, operan exclusivamente mediante entrada (`@Input`) y salida de eventos (`@Output`).  
* **Control Flow Moderno:** Reemplazo de directivas estructurales clásicas por sintaxis nativa: `@if`, `@else`, `@for` con cláusula obligatoria `track` (ej. `track item.id`), y bloque `@empty`.

## **Nivel 2: Servicios, Inyección de Dependencias y Principio de Responsabilidad Única (SRP)**

* **Servicios Singleton:** Creación y alcance global mediante `@Injectable({ providedIn: 'root' })`.  
* **Inyección Moderna:** Migración total del modelo de constructor tradicional a la función declarativa `inject()`.  
* **Separación de Responsabilidades (SRP):** Desacoplamiento estricto donde los componentes nunca ejecutan cálculos complejos, transformaciones pesadas ni llamadas directas a `HttpClient` sin un servicio intermediario.

## **Nivel 3: Reactividad, Inmutabilidad y Manejo de Caché**

* **Inmutabilidad de Estado:** Análisis de los fallos por referencia de memoria derivados de mutaciones directas (`.sort()`, `.push()`) que rompen la detección de cambios en vistas desacopladas.  
* **Operaciones Inmutables:** Uso sistemático de `toSorted()`, `map()`, `filter()` y sintaxis spread (`{ ...obj }`, `[...arr]`).  
* **Estrategia de Caché Financiera:**  
  * *Catálogos Estáticos (ej. Lista de Bancos, Convenios):* Uso de operadores reactivos como `shareReplay(1)` o almacenamiento persistente temporal en `sessionStorage`.  
  * *Saldos y Transacciones Volátiles:* Invalidación de caché y re-obtención (*re-fetch*) inmediata desde el servidor tras cada operación monetaria.

## **Nivel 4: Formularios Reactivos con Dependencias Complejas**

* **Construcción de Formularios:** Uso de `ReactiveFormsModule` mediante `FormGroup`, `FormControl` y validadores síncronos/asíncronos con `Validators`.  
* **Dependencias Dinámicas entre Campos:** Escucha de cambios reactivos con `valueChanges` para habilitar, deshabilitar o reconfigurar controles condicionalmente.  
* **Validación Desacoplada:** Evitar dependencias circulares y mantener la lógica de validación de negocio totalmente independiente de la capa de visualización (DOM).

## **Nivel 5: Enrutamiento Dinámico, Guards y Seguridad Frontend**

* **Navegación Dinámica:** Definición de rutas paramétricas (`path: 'cuentas/:id'`) y extracción de datos reactiva mediante `ActivatedRoute`.  
* **Guards Funcionales:** Implementación de `canDeactivate` en forma de función pura para prevenir la pérdida de datos accidentales en formularios incompletos.  
* **Seguridad en la Capa Cliente:**  
  * *Límites del Frontend:* Comprensión de por qué los Guards y la ocultación de elementos DOM no constituyen seguridad real.  
  * *Autenticación Robusta:* Implementación de Cookies `HttpOnly` y `Secure` para mitigar ataques XSS y vectores CSRF.  
* **Políticas de Seguridad de Contenido (CSP):** Configuración estricta de directivas `script-src` y erradicación total de estilos en línea hacia hojas de estilo estructuradas (SCSS/CSS).

## **Nivel 6: Patrones Arquitectónicos y Extensibilidad UI**

* **Patrón Adapter / Facade:** Creación de servicios intermediarios para envolver librerías de terceros (notificaciones, analítica) permitiendo su reemplazo global sin modificar componentes.  
* **Directivas de Atributo Personalizadas:** Desarrollo de directivas para formateo de entrada de texto (ej. máscaras monetarias `$ 1,000.00`) alineadas al Design System institucional.  
* **Pipes Puros:** Garantía de rendimiento mediante funciones de transformación de datos puras y sin efectos secundarios.

## **Nivel 7: Performance y Empaquetado**

* **Carga Perezosa (Lazy Loading):** Segmentación de rutas por módulos/componentes para optimizar el tamaño del paquete (*bundle*) inicial entregado a dispositivos móviles.  
* **Auditoría de Dependencias:** Uso de analizadores de *bundle* y la pestaña Network de DevTools para la eliminación de código muerto (*tree-shaking*).  
* **Optimización de Cambio:** Estrategia global `ChangeDetectionStrategy.OnPush` para reducir el impacto de ciclos de renderizado.

---

# **3\. ESPECIFICACIÓN DEL PROYECTO: "BANKCORE"**

Desarrollo integral de la interfaz bancaria modular **BankCore**, aplicando arquitectura por capas e inmutabilidad.

## **Estructura de Directorios Arquitectónica**

| Módulo / Directorio | Responsabilidad Técnica | Componentes Incluidos |
| :---- | :---- | :---- |
| `src/app/core/` | Configuración global, servicios singleton, seguridad y adaptadores. | Interceptores HTTP, AuthGuard, NotificationAdapter, HttpService. |
| `src/app/shared/` | UI Kit reutilizable, directivas de formato y utilidades puras. | MovimientosTableComponent, CurrencyMaskDirective, FilterPipe. |
| `src/app/features/` | Módulos funcionales de negocio aislados (Lazy Loaded). | DashboardFeature, TransferenciasFeature, CreditoSimuladorFeature. |

## **Hitos Prácticos a Desarrollar**

* **Hito 1: Componente Reutilizable de Movimientos (`Shared`)**  
  Desarrollar `MovimientosTableComponent` como componente presentacional puro. Recibe la lista de transacciones mediante `@Input()` y emite selecciones a través de `@Output()`. Utilizable tanto en el detalle de Cuentas como en Tarjetas de Crédito.  
* **Hito 2: Servicio de Catálogo y Consulta de Saldo en Vivo (`Core`)**  
  Crear un servicio de datos con caching para el catálogo de bancos emisor mediante `shareReplay(1)`. Implementar la consulta de saldo disponible con actualización forzada tras cada transacción exitosa.  
* **Hito 3: Formulario Reactivo de Solicitud de Crédito (`Features`)**  
  Diseñar el formulario de simulador de crédito con reglas dinámicas: si el monto solicitado supera el límite pre-aprobado, activar automáticamente el campo requerido de "Declaración de Ingresos".  
* **Hito 4: Protección de Rutas y Transiciones (`Core / Features`)**  
  Configurar la ruta parametrizada `/cuentas/:id` y adjuntar un Guard funcional `canDeactivate` al módulo de transferencias para alertar al usuario si intenta navegar con un pago no confirmado.  
* **Hito 5: Extensibilidad de UI y Notificaciones (`Core / Shared`)**  
  Implementar el servicio adaptador `NotificationAdapter` para abstraer alertas del sistema y crear la directiva `CurrencyMaskDirective` para autocompletar separadores de miles en tiempo de escritura.

---

# **4\. BANCO DE RESPUESTAS RÁPIDAS (SIMULACRO PARA ENTREVISTAS)**

Respuestas técnicas bajo la **fórmula de 20 segundos** para evaluación de candidatos:

1. **¿Cómo reutilizar una lista de movimientos sin duplicar código?**  
   * *El Problema:* Duplicar la estructura HTML y lógica de renderizado en cuentas y tarjetas genera código frágil e inmanteniible.  
   * *La Herramienta:* Crear un *Dumb Component* que reciba los datos mediante `@Input()` y notifique eventos vía `@Output()`.  
   * *El Resultado:* Un componente presentacional único, testeable y desacoplado de las fuentes de datos.  
2. **¿Por qué una lista ordenada altera la visualización en otras pantallas del sistema?**  
   * *El Problema:* El método nativo `.sort()` muta el arreglo directamente en la referencia de memoria compartida por la aplicación.  
   * *La Herramienta:* Utilizar la función inmutable `toSorted()` o crear una nueva referencia mediante `[...lista].sort()`.  
   * *El Resultado:* Se preserva la integridad del estado global y se evitan efectos secundarios impredecibles en la interfaz.  
3. **¿Por qué un Guard y la ocultación de botones no garantizan la seguridad?**  
   * *El Problema:* El código de la capa cliente expuesto en el navegador puede ser inspeccionado, modificado o ignorado desde la consola web.  
   * *La Herramienta:* Aplicar validación estricta de roles, permisos y tokens de sesión directamente en el API Gateway del Backend.  
   * *El Resultado:* Protección real de los datos operacionales, limitando el rol del frontend a la experiencia de usuario.  
4. **¿Qué datos se deben cachear y cuáles no en un flujo de pago de servicios?**  
   * *El Problema:* Realizar peticiones HTTP reiteradas para datos estáticos satura la red, mientras que cachear saldos muestra datos desactualizados.  
   * *La Herramienta:* Aplicar `shareReplay(1)` o `sessionStorage` para el catálogo de empresas, y re-obtención directa para el saldo.  
   * *El Resultado:* Optimización de latencia sin comprometer la precisión financiera del usuario.  
5. **¿Cómo reemplazar una librería externa de UI sin modificar decenas de componentes?**  
   * *El Problema:* Acoplar la librería directamente a múltiples componentes genera una dependencia crítica difícil de migrar.  
   * *La Herramienta:* Implementar el *Patrón Adapter* creando un servicio intermedio en la capa `Core`.  
   * *El Resultado:* Flexibilidad para cambiar la librería subyacente modificando únicamente el archivo del adaptador.  
6. **¿Cómo proceder si la Política de Seguridad de Contenido (CSP) bloquea scripts o estilos?**  
   * *El Problema:* El navegador bloquea scripts no autorizados y estilos en línea por riesgo de inyección de código (XSS).  
   * *La Herramienta:* Declarar el dominio del proveedor en la directiva `script-src` del encabezado CSP y mover estilos a archivos `.scss`.  
   * *El Resultado:* Cumplimiento de normativas de seguridad bancaria sin sacrificar la integración de herramientas o la estética.

s

\---

\#\# 3\. PENSUM DE APRENDIZAJE QUIRÚRGICO POR BLOQUES

\+-------------------------------------------------------------------------+

| RUTA DE APRENDIZAJE BANCORE (ANGULAR) |
| :---- |
| \[BLOQUE 1\] Cimientos & Desacoplamiento (Standalone, Control Flow) |
| │ |
| ▼ |
| \[BLOQUE 2\] Reactividad, Inmutabilidad & Caché (Signals, RxJS) |
| │ |
| ▼ |
| \[BLOQUE 3\] Formularios Reactivos & Flujos Dinámicos (Validaciones) |
| │ |
| ▼ |
| \[BLOQUE 4\] Rutas, Guards & Patrón Adapter (Seguridad de Salida) |
| │ |
| ▼ |
| \[BLOQUE 5\] Auditoría de Seguridad & Rendimiento (CSP, OnPush, Lazy) |
| \+-------------------------------------------------------------------------+ |

\#\#\# Bloque 1: Cimientos de Angular Moderno & Desacoplamiento

\* \*\*Enfoque Pedagógico:\*\* Abstracción del sistema de módulos antiguo. Adopción completa de Standalone Components (\`imports: \[...\]\`).

\* \*\*Conceptos Clave:\*\*

  \* Interpolación \`{{ }}\`, Property Binding \`\[ \]\`, Event Binding \`( )\`.

  \* Sintaxis de Control Flow nativa: \`@if\`, \`@else\`, \`@for (track item.id)\`, \`@empty\`.

  \* Arquitectura Smart vs. Dumb: Flujo unidireccional de datos con \`@Input()\` y \`@Output()\`.

\* \*\*Hito Práctico 1:\*\* Construir un componente de UI reutilizable \`TransactionListComponent\` (Dumb Component) apto para desplegar movimientos tanto de Cuentas de Ahorro como de Tarjetas de Crédito, sin referencias internas a servicios HTTP.

\#\#\# Bloque 2: Servicios, Reactividad, Caché e Inmutabilidad

\* \*\*Enfoque Pedagógico:\*\* Gestión del estado local y reactivo, previniendo efectos secundarios imprevistos en la memoria.

\* \*\*Conceptos Clave:\*\*

  \* Inyección de dependencias moderna mediante la función \`inject()\`.

  \* Transición de paradigmas: Signals (\`signal()\`, \`computed()\`) vs RxJS (\`BehaviorSubject\`, \`Observable\`).

  \* Principios de Inmutabilidad en JavaScript: Análisis de mutaciones en memoria y adopción de \`toSorted()\` y clonación superficial/profunda.

  \* Estrategias de Caché: Diferenciación entre catálogos estáticos mediante \`shareReplay(1)\` y datos financieros sensibles de alta volatilidad.

\* \*\*Hito Práctico 2:\*\* Implementar el servicio \`TransactionService\` y el servicio de \`PublicCatalogService\`, estableciendo una política de caché persistente de memoria para catálogos y revalidación bajo demanda para saldos bancarios.

\#\#\# Bloque 3: Formularios Reactivos y Flujos Dinámicos

\* \*\*Enfoque Pedagógico:\*\* Construcción de interfaces complejas guiadas por estados y validaciones en tiempo real.

\* \*\*Conceptos Clave:\*\*

  \* Modelo reactivo: Uso de \`ReactiveFormsModule\`, \`FormBuilder\`, \`FormGroup\`, y \`FormControl\`.

  \* Orquestación dinámica: Reacción a cambios de valor mediante \`valueChanges\` y actualización de validadores en caliente.

  \* Flujo defensivo de errores: Retorno explícito de objetos de respuesta \`{ success: boolean, error?: string }\` para evitar excepciones no capturadas.

\* \*\*Hito Práctico 3:\*\* Diseñar el formulario de transferencias bancarias y el simulador de crédito multi-paso (wizard de 8 estados) con lógica condicional avanzada y prevención de ciclos de dependencia.

\#\#\# Bloque 4: Enrutamiento Dinámico, Guards y Patrón Adapter

\* \*\*Enfoque Pedagógico:\*\* Control de la navegación SPA y desvinculación de dependencias externas.

\* \*\*Conceptos Clave:\*\*

  \* Manejo de rutas paramétricas (\`path: 'cuentas/:id'\`) y extracción limpia con \`ActivatedRoute\`.

  \* Protección de estado vía \`canDeactivate\`: Intercepción funcional antes de descartar cambios no guardados.

  \* Patrón \*\*Adapter / Fachada\*\*: Creación de wrappers para aislar componentes de librerías UI o SDKs de analítica externos.

  \* Directivas de Atributo Personalizadas: Implementación de formateadores de moneda (\`\$ 000,000.00\`) sobre controles nativos sin romper el Design System.

\* \*\*Hito Práctico 4:\*\* Configurar un guard funcional \`canDeactivate\` que prevenga la salida imprevista de la pantalla de transferencias y encapsular la biblioteca de notificaciones externas dentro de un servicio adaptador.

\#\#\# Bloque 5: Seguridad en Frontend & Rendimiento

\* \*\*Enfoque Pedagógico:\*\* Endurecimiento del cliente web y optimización métrica para producción.

\* \*\*Conceptos Clave:\*\*

  \* Mitología de la compilación frontend: Entender por qué \`environment.ts\` no oculta secretos y cómo gestionar la configuración expuesta.

  \* Almacenamiento y transporte seguro: Mecanismos de protección contra XSS y CSRF. Manejo de tokens en cookies \`HttpOnly\` \+ \`SameSite\`.

  \* Implementación de Content Security Policy (CSP): Remoción de estilos inline y restricción estricta de dominios de ejecución.

  \* Estrategias de Rendimiento: Code splitting a través de \`Lazy Loading\`, estrategia de detección de cambios \`ChangeDetectionStrategy.OnPush\` e inspección con Chrome DevTools.

\* \*\*Hito Práctico 5:\*\* Realizar una auditoría completa de seguridad sobre la SPA y refactorizar los componentes clave del sistema hacia la estrategia \`OnPush\`.

\---

\#\# 4\. BANCO DE SIMULACRO PARA ENTREVISTAS (RESPUESTAS EN 20 SEGUNDOS)

| \# | Pregunta Situacional | Respuesta Guionizada (20 Segundos) |

| :--- | :--- | :--- |

| \*\*1\*\* | \*\*¿Cómo reutilizar una lista de transacciones en Cuentas y Tarjetas?\*\* | \*\*Problema:\*\* Acoplar la lista a un servicio genera duplicidad o dependencia rígida.\<br\>\*\*Herramienta:\*\* Componente Dumb con \`@Input()\` para la data y \`@Output()\` para eventos.\<br\>\*\*Impacto:\*\* Permite reutilizar la UI en múltiples módulos pasando diferentes orígenes de datos. |

| \*\*2\*\* | \*\*¿Cómo diagnosticar y resolver un bundle inicial muy pesado?\*\* | \*\*Problema:\*\* Cargar toda la aplicación al inicio incrementa drásticamente el TTI.\<br\>\*\*Herramienta:\*\* Inspección con Lighthouse/Network y configuración de \`loadComponent\` con Lazy Loading.\<br\>\*\*Impacto:\*\* Reduce el tiempo de descarga inicial descargando JavaScript solo bajo demanda. |

| \*\*3\*\* | \*\*¿Dónde se debe almacenar un JWT de manera segura en el cliente?\*\* | \*\*Problema:\*\* Guardar el token en \`localStorage\` deja la sesión vulnerable a ataques XSS.\<br\>\*\*Herramienta:\*\* Transmisión mediante Cookies de servidor con atributos \`HttpOnly\`, \`Secure\` y \`SameSite\`.\<br\>\*\*Impacto:\*\* Bloquea el acceso al token mediante scripts inyectados en el navegador. |

| \*\*4\*\* | \*\*¿Cómo manejar la caché de un catálogo estático frente a un saldo bancario?\*\* | \*\*Problema:\*\* Reconsultar datos estáticos satura la red, mientras que cachear el saldo muestra datos obsoletos.\<br\>\*\*Herramienta:\*\* Operador \`shareReplay(1)\` para el catálogo y peticiones sin caché para el saldo.\<br\>\*\*Impacto:\*\* Optimiza el tráfico de red manteniendo la integridad de las cifras financieras. |

| \*\*5\*\* | \*\*Un desarrollador guardó una API Key privada en \`environment.ts\`. ¿Qué riesgo existe?\*\* | \*\*Problema:\*\* Los archivos de environment se compilan dentro del bundle público y pueden inspeccionarse.\<br\>\*\*Herramienta:\*\* Backend Proxy o API Gateway para ocultar llaves y delegar firmas.\<br\>\*\*Impacto:\*\* Previene la exposición e intercepción de credenciales privadas del negocio. |

| \*\*6\*\* | \*\*¿Cómo construir un formulario dinámico de 8 pasos con dependencias complejas?\*\* | \*\*Problema:\*\* Cambios en pasos iniciales pueden invalidar respuestas o romper estados posteriores.\<br\>\*\*Herramienta:\*\* \`ReactiveFormsModule\` escuchando cambios mediante \`valueChanges\` con reseteo en cascada.\<br\>\*\*Impacto:\*\* Mantiene la coherencia de los datos del formulario evitando estados inconsistentes. |

| \*\*7\*\* | \*\*¿Cómo evitar la pérdida de datos si el usuario navega fuera de una transferencia a medio llenar?\*\* | \*\*Problema:\*\* El usuario puede perder información valiosa ingresada al hacer clic por accidente en otra ruta.\<br\>\*\*Herramienta:\*\* Guard funcional \`canDeactivate\` comprobando el flag \`form.dirty\`.\<br\>\*\*Impacto:\*\* Muestra una alerta de confirmación bloqueando la navegación hasta confirmar la acción. |

| \*\*8\*\* | \*\*¿Cómo dar formato monetario a un input del Design System sin alterar el valor numérico base?\*\* | \*\*Problema:\*\* Modificar el valor interno del control a cadena formateada rompe la lógica matemática.\<br\>\*\*Herramienta:\*\* Directiva de atributo de Angular sobre los eventos \`focus\` y \`blur\`.\<br\>\*\*Impacto:\*\* Mantiene la presentación formateada en pantalla mientras retiene el número puro en el modelo. |

| \*\*9\*\* | \*\*¿Por qué la llamada \`lista.sort()\` dentro de un Signal causó un error inesperado?\*\* | \*\*Problema:\*\* \`.sort()\` muta el arreglo original por referencia, corrompiendo la detección de cambios.\<br\>\*\*Herramienta:\*\* Método inmutable \`toSorted()\` o copia previa mediante el operador spread \`\[...lista\]\`.\<br\>\*\*Impacto:\*\* Preserva la inmutabilidad de los datos garantizando una detección de cambios reactiva limpia. |

| \*\*10\*\* | \*\*¿Por qué falla el diseño al bloquear scripts e inline styles mediante CSP?\*\* | \*\*Problema:\*\* Las directivas CSP estrictas bloquean cualquier ejecución de código o estilo no autorizado implícitamente.\<br\>\*\*Herramienta:\*\* Configuración de directiva \`script-src\` y migración de estilos inline a CSS con hashes o nonces.\<br\>\*\*Impacto:\*\* Protege la aplicación contra inyecciones maliciosas manteniendo la integridad visual. |

\---

\#\# 5\. CHECKLIST DE DOMINIO Y HITOS DE VERIFICACIÓN

Esta matriz de autoevaluación permite validar el nivel de dominio técnico adquirido antes de finalizar cada bloque de trabajo:

\`\`\`markdown

\- \[ \] \*\*Bloque 1: Cimientos & Desacoplamiento\*\*

  \- \[ \] Implementación de componentes Standalone sin uso de NgModules.

  \- \[ \] Uso exclusivo de la sintaxis nativa \`@if\`, \`@for\` y \`@empty\` en plantillas.

  \- \[ \] Desacoplamiento total del componente \`TransactionListComponent\` (Smart vs Dumb).

\- \[ \] \*\*Bloque 2: Reactividad, Caché e Inmutabilidad\*\*

  \- \[ \] Inyección de dependencias mediante \`inject()\` en lugar de constructores tradicionales.

  \- \[ \] Uso correcto de Signals para estado síncrono y RxJS para flujos asíncronos.

  \- \[ \] Ausencia total de métodos mutadores (\`push\`, \`sort\`, \`splice\`) en el manejo de arreglos.

  \- \[ \] Implementación de \`shareReplay(1)\` en el catálogo de servicios públicos.

\- \[ \] \*\*Bloque 3: Formularios Reactivos & Flujos Dinámicos\*\*

  \- \[ \] Construcción de formularios con \`FormBuilder\` y validadores personalizados.

  \- \[ \] Subscripción a \`valueChanges\` para alterar la validación de campos dinámicos.

  \- \[ \] Manejo defensivo de errores retornando objetos de estado \`{ success, error }\`.

\- \[ \] \*\*Bloque 4: Rutas, Guards & Patrón Adapter\*\*

  \- \[ \] Configuración de rutas paramétricas y lectura vía \`ActivatedRoute\`.

  \- \[ \] Implementación exitosa del guard funcional \`canDeactivate\`.

  \- \[ \] Encapsulamiento de librerías UI de terceros mediante el patrón Adapter.

  \- \[ \] Creación de una directiva de atributo para el formateo de campos monetarios.

\- \[ \] \*\*Bloque 5: Auditoría de Seguridad & Rendimiento\*\*

  \- \[ \] Remoción de secretos del archivo \`environment.ts\`.

  \- \[ \] Configuración de la estrategia \`ChangeDetectionStrategy.OnPush\` en componentes de presentación.

  \- \[ \] Carga diferida (\`Lazy Loading\`) configurada en todas las rutas secundarias del sistema.

  \- \[ \] Compliance de Content Security Policy (CSP) en la auditoría final.  
