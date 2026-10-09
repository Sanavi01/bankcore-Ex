# 🧭 BankCore: Pensum de Especialización y Bitácora de Progreso (Nivel Semisenior)

> **Perfil Objetivo:** Desarrollador Angular Semi-Senior (SSR) con Estándares Bancarios / Empresariales  
> **Proyecto:** BankCore - Single Page Application Bancaria de Grado Producción  
> **Rol del Tutor:** Tech Lead & Senior Angular Architect  
> **Estudiante:** Frontend Developer en Formación Acelerada  
> **Estado General:** 🚀 En Progreso (Módulo 0: Entorno y Cimientos Operativos)  
> **Última Actualización:** 2026-10-02

---

## 📌 Leyenda de Estados
- 🟩 **[x] Completado:** Tema dominado, código implementado y validado mediante desafío o simulacro.
- 🟨 **[/] En Progreso:** Tema actual en explicación, ejecución de comandos o resolución de código.
- ⬜ **[ ] Pendiente:** Siguiente paso planificado en la ruta pedagógica.

---

## 🛠️ Módulo 0: Entorno de Desarrollo, CLI y Cero Absoluto
*Objetivo: Dominar las herramientas profesionales desde la terminal, la anatomía de un proyecto Angular y los fundamentos de TypeScript estricto.*

- [x] **0.1 Ecosistema Frontend & Runtime:** ¿Qué es Node.js, npm, npx y cómo gestionan dependencias en proyectos reales?
- [x] **0.2 Angular CLI a Fondo:** Instalación, flags profesionales (`--standalone`, `--routing`, `--style=scss`, `--strict`) y comandos vitales (`ng new`, `ng serve`, `ng g c`, `ng g s`, `ng build`).
- [x] **0.3 Radiografía Quirúrgica de un Proyecto:** Desglose exacto de `package.json`, `angular.json`, `tsconfig.json`, `src/index.html`, `src/main.ts`, `src/app/app.config.ts`, `src/app/app.routes.ts`.
- [x] **0.4 TypeScript Moderno para Angular:** Tipado estricto, Interfaces vs Types, Generics básicos, modificadores de acceso (`readonly`, `private`), operadores seguros (`?.`, `??`, `!`).
- [x] **0.5 Inicialización Oficial de BankCore:** Creación del proyecto base en el repositorio con arquitectura limpia por capas (`core`, `shared`, `features`).

---

## 🧱 Módulo 1: Anatomía Quirúrgica de Componentes & Standalone Architecture
*Objetivo: Construir componentes desacoplados, dominar el nuevo Control Flow nativo y la carga diferida con `@defer`.*

- [x] **1.1 Anatomía Interna de un Componente:** El decorador `@Component` (`selector`, `standalone`, `imports`, `templateUrl`, `styleUrl`).
- [x] **1.2 Data Binding Exhaustivo:**
  - Interpolación: `{{ expresion }}`
  - Property Binding: `[propiedad]="valor"`
  - Event Binding: `(evento)="metodo()"`
  - Two-Way Binding: `[(ngModel)]` vs Two-Way con Signals (`model()`).
- [x] **1.3 Modern Control Flow:**
  - Condicionales nativos: `@if`, `@else if`, `@else`.
  - Bucles optimizados: `@for (item of items; track item.id)` y cláusula `@empty`.
  - Evaluaciones múltiples: `@switch`, `@case`, `@default`.
- [x] **1.4 Carga Diferida en Plantilla (`@defer`):** Optimización con `@defer (on viewport / on interaction / on idle)`, bloques `@placeholder`, `@loading` y `@error`.
- [x] **1.5 Ciclo de Vida del Componente (Lifecycle Hooks):** `ngOnInit`, `ngOnChanges`, `ngOnDestroy`, `afterNextRender` y migración a Signals (`input()`, `output()`, `model()`).
- [x] **1.6 Arquitectura Dumb vs. Smart (Presentational vs. Container):** Flujo unidireccional estricto con `@Input()` / `@Output()`.
- [x] **1.7 Proyección de Contenido & Plantillas Dinámicas:** `<ng-content>`, `ng-container`, `ng-template` y `TemplateRef`.
- [x] **1.8 🏆 Hito Práctico 1:** Construcción de `MovimientosTableComponent` (Dumb Component desacoplado en `src/app/shared/components/`).
- [x] **1.9 🎙️ Simulacro de Entrevista Técnica (Módulo 1):** Pregunta situacional resuelta en 20 segundos.

---

## ⚡ Módulo 2: Estado, Reactividad y Gestión Asíncrona (Signals + RxJS)
*Objetivo: Dominar el estado síncrono con Signals, la asincronía bancaria con RxJS y la inmutabilidad estricta de memoria.*

- [x] **2.1 Servicios Singleton & Principio de Responsabilidad Única (SRP):** Por qué los componentes nunca hacen llamadas HTTP directas.
- [x] **2.2 Inyección de Dependencias Moderna:** Uso exclusivo de la función `inject()` y tokens de inyección (`InjectionToken`).
- [ ] **2.3 Inmutabilidad Estricta de Memoria:** Análisis de fallos por referencia (`.push()`, `.sort()`, `.splice()`) vs operaciones inmutables (`toSorted()`, `toSpliced()`, `.map()`, spread `[...]`, Structured Clone).
- [ ] **2.4 Reactividad Síncrona con Signals:** `signal()`, `computed()`, `effect()`, `untracked()`, actualización inmutable con `set()` y `update()`.
- [ ] **2.5 Asincronía con RxJS & HttpClient:** Anatomía de un `Observable`, Observer, Subscription, ciclo de vida y desuscripción obligatoria.
- [ ] **2.6 Operadores Clave de RxJS en Producción:** `map`, `filter`, `tap`, `switchMap`, `concatMap`, `catchError`, `finalize`, `takeUntilDestroyed`.
- [ ] **2.7 Puente Signals ↔ RxJS:** Conversión bidireccional limpia con `toSignal()` y `toObservable()`.
- [ ] **2.8 Estrategia de Caché Financiera:** Caché persistente con `shareReplay({ bufferSize: 1, refCount: true })` para catálogos estáticos vs revalidación forzada de saldos volátiles.
- [ ] **2.9 🏆 Hito Práctico 2:** Implementación de `TransactionService` y `PublicCatalogService` en `src/app/core/services/`.
- [ ] **2.10 🎙️ Simulacro de Entrevista Técnica (Módulo 2):** Preguntas situacionales de inmutabilidad, RxJS y Signals en 20 segundos.

---

## 📋 Módulo 3: Formularios Empresariales y Validación Reactiva Compleja
*Objetivo: Manejar flujos de entrada financiera con validaciones avanzadas, dependencias en tiempo real y componentes CVA.*

- [ ] **3.1 Modelo Reactivo Tipado:** `ReactiveFormsModule`, `FormGroup`, `FormControl`, `FormArray`, `FormRecord`, `FormBuilder`, `NonNullableFormBuilder`.
- [ ] **3.2 Validaciones Síncronas y Asíncronas:** Validadores nativos (`Validators.required`, `Validators.min`) y validadores custom (ej. validación asíncrona de cuenta existente vía API).
- [ ] **3.3 Dependencias Dinámicas en Tiempo Real:** Reacción a `valueChanges` y `statusChanges` con `debounceTime` y `distinctUntilChanged` para habilitar/deshabilitar controles en caliente.
- [ ] **3.4 Patrón ControlValueAccessor (CVA):** Creación de componentes de formulario personalizados del Design System (inputs monetarios integrables en `formControlName`).
- [ ] **3.5 Manejo Defensivo de Respuestas Bancarias:** Tipo de retorno `Result<T>` `{ success: boolean, data?: T, error?: string }` para prevenir excepciones silenciosas.
- [ ] **3.6 🏆 Hito Práctico 3:** Formulario reactivo de transferencias bancarias y Asistente de Crédito (Wizard multi-paso de 8 estados) en `src/app/features/`.
- [ ] **3.7 🎙️ Simulacro de Entrevista Técnica (Módulo 3):** Manejo de formularios reactivos y dependencias complejas en 20 segundos.

---

## 🛡️ Módulo 4: Enrutamiento Avanzado, Guards Funcionales & Patrón Adapter
*Objetivo: Controlar la navegación en la SPA, proteger estados no confirmados y desacoplar dependencias externas.*

- [ ] **4.1 Router Moderno & Lazy Loading:** `provideRouter()`, `withComponentInputBinding()`, `loadComponent` y `loadChildren`.
- [ ] **4.2 Arquitectura de Rutas Bancarias:** Layouts compartidos (Shell / Dashboard / Auth) y rutas paramétricas (`/cuentas/:id`) con `ActivatedRoute`.
- [ ] **4.3 Guards Funcionales Modernos:** `canActivate`, `canMatch` y `canDeactivate` (intercepción funcional con diálogo de alerta antes de descartar transferencias no guardadas).
- [ ] **4.4 Interceptores HTTP Funcionales (`HttpInterceptorFn`):** Inyección de cabeceras de autorización (Bearer Token), manejo centralizado de errores HTTP, retry automático y control global de spinner de carga.
- [ ] **4.5 Patrón Adapter / Facade (Inversión de Dependencias):** Aislamiento de librerías UI de terceros (alertas, modales, analítica) en `src/app/core/adapters/`.
- [ ] **4.6 Directivas de Atributo Personalizadas:** Creación de `CurrencyMaskDirective` con formateo en tiempo de escritura sin corromper el valor numérico base del modelo.
- [ ] **4.7 🏆 Hito Práctico 4:** Protección de transferencias, interceptores de sesión y adaptador de notificaciones.
- [ ] **4.8 🎙️ Simulacro de Entrevista Técnica (Módulo 4):** Preguntas de Guards, Interceptores y Patrón Adapter en 20 segundos.

---

## ⚡ Módulo 5: Rendimiento, Detección de Cambios y Pruebas Unitarias (Testing)
*Objetivo: Optimizar métricas de velocidad, comprender la detección de cambios a bajo nivel y asegurar la calidad del código.*

- [ ] **5.1 Ciclos de Detección de Cambios:** Cómo funciona Zone.js vs la nueva era Zoneless de Angular.
- [ ] **5.2 Estrategia `ChangeDetectionStrategy.OnPush`:** Por qué es obligatoria en entornos de alta exigencia, cómo se dispara y cómo depurar fallos de renderizado.
- [ ] **5.3 Optimización de Recursos:** `NgOptimizedImage`, fuentes locales, tree-shaking y reducción de bundle size.
- [ ] **5.4 Pruebas Unitarias Automatizadas (Testing):**
  - Configuración del entorno de testing (`TestBed`).
  - Pruebas unitarias de componentes Dumb (renderizado, `@Input`, `@Output`).
  - Pruebas unitarias de Servicios con mocks de `HttpClient` (`HttpTestingController`).
  - Pruebas de Pipes y Directivas.
- [ ] **5.5 🏆 Hito Práctico 5:** Suite de pruebas unitarias sobre componentes y servicios del core bancario.
- [ ] **5.6 🎙️ Simulacro de Entrevista Técnica (Módulo 5):** Preguntas de `OnPush`, zoneless y testing en 20 segundos.

---

## 🔒 Módulo 6: Seguridad Bancaria en Frontend & Auditoría de Producción
*Objetivo: Endurecer el cliente web frente a vectores de ataque reales y auditar métricas de grado bancario.*

- [ ] **6.1 Realidad de la Compilación Frontend:** Por qué `environment.ts` es público y cómo utilizar Backend For Frontend (BFF) o API Gateway para proteger credenciales.
- [ ] **6.2 Seguridad Web Defensiva:** Vectores de ataque XSS, mitigación con `DomSanitizer`, riesgos de `bypassSecurityTrust*` y protección CSRF.
- [ ] **6.3 Transporte y Almacenamiento Seguro:** Cookies `HttpOnly`, `SameSite=Strict`, `Secure` vs riesgos de `localStorage`.
- [ ] **6.4 Content Security Policy (CSP):** Eliminación total de inline styles/scripts y configuración de directivas `script-src` y nonces.
- [ ] **6.5 Auditoría de Producción:** Análisis de bundles con `source-map-explorer`, auditoría Lighthouse (Performance, Accessibility, Best Practices, SEO) y Core Web Vitals.
- [ ] **6.6 🏆 Hito Práctico 6:** Auditoría de seguridad final y optimización de entrega de BankCore.
- [ ] **6.7 🎙️ Simulacro Final de Entrevista Semisenior:** Batería de 10 preguntas de alta exigencia bajo la fórmula de 20 segundos.

---

## 📊 Bitácora de Sesiones
| Sesión | Fecha | Tema Abordado | Estado / Logro |
| :--- | :--- | :--- | :--- |
| #01 | 2026-10-02 | Módulo 0 Completo (Runtime, CLI, Anatomía, TypeScript, Clean Arch) | 🟩 Módulo 0 Conquistado al 100% |
| #02 | 2026-10-05 | Módulo 1 Completo (Componentes Standalone, Control Flow, Dumb vs Smart, Hito Práctico 1, Simulacro) | 🟩 Módulo 1 Conquistado al 100% |
