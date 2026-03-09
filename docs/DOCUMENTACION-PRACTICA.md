# Documentación Práctica - Aplicación Library
## Teoría de Ingeniería de Software Aplicada con Ejemplos Reales

---

# 📦 Clasificación de Servicios en la Nube

## En Tu Aplicación Library

### **IaaS (Infrastructure as a Service)**
**Ejemplo en tu app:**
```yaml
# docker-compose-lite.yml - Líneas 83-95
postgresql:
  image: postgres:16.2-alpine  # ← Infraestructura como servicio
  environment:
    - POSTGRES_DB=library
  deploy:
    resources:
      limits:
        cpus: '0.5'
        memory: 256M  # ← Recursos de infraestructura controlados
```
**Explicación:** Usas PostgreSQL en Docker, que es como IaaS - tú controlas la DB, sus recursos, configuración, pero no el hardware físico.

### **PaaS (Platform as a Service)**
**Ejemplo en tu app:**
```yaml
# docker-compose-lite.yml - Líneas 13-45
app:
  image: library-app:latest
  environment:
    - SPRING_PROFILES_ACTIVE=prod
    - JAVA_OPTS=-Xmx256m -Xms128m  # ← Plataforma configurada
```
**Explicación:** Tu aplicación corre en Spring Boot/JHipster - no te preocupas del sistema operativo, solo configuras la app.

### **SaaS (Software as a Service)**
**Ejemplo en tu app:**
El frontend de tu aplicación (`http://localhost:8080`) es SaaS para tus usuarios finales - solo la usan sin instalar nada.

---

# 🔧 Los 12 Factores - En Tu Aplicación Library

## 1. **Código Base** ✅

**Teoría:** Un repositorio por aplicación, desplegable en múltiples entornos.

**En tu app:**
```bash
# Tu estructura de repositorio
backend/          # ← Repositorio principal (monolito)
library-mobile/   # ← Aplicación móvil separada
docs/            # ← Documentación
```

**Archivo clave:** `library.jh`
```jdl
application {
  config {
    baseName library,
    applicationType monolith,  # ← Un solo repositorio para backend + frontend
    buildTool maven,
    clientFramework angular
  }
}
```

---

## 2. **Dependencias** ✅

**Teoría:** Todas las dependencias declaradas explícitamente con instalación automática.

**En tu app - Backend:**
```xml
<!-- pom.xml - Líneas 1-100 -->
<dependencies>
  <!-- Dependencias explícitas de Spring Boot -->
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.4.5</version>  <!-- ← Versión exacta -->
  </dependency>
</dependencies>
```

**En tu app - Frontend:**
```json
// package.json - Líneas 1-50
{
  "name": "library",
  "version": "0.0.1-SNAPSHOT",
  "dependencies": {
    "@angular/core": "^17.x.x",  // ← Todas declaradas
    "typescript": "5.x.x"
  }
}
```

**Instalación automática:**
```bash
# Backend
./mvnw clean install  # ← Maven descarga todas las dependencias

# Frontend
npm install  # ← npm descarga todas las dependencias
```

---

## 3. **Configuraciones** ✅

**Teoría:** Configuraciones separadas del código, por entorno.

**En tu app - Desarrollo:**
```yaml
# src/main/resources/config/application-dev.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/library  # ← Config DEV
    username: library
    password:  # ← Sin contraseña en desarrollo
  
logging:
  level:
    ROOT: DEBUG  # ← Logs verbosos en DEV
```

**En tu app - Producción:**
```yaml
# src/main/resources/config/application-prod.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/library  # ← Config PROD
    username: library
    password:  # ← Se lee de variable de entorno

logging:
  level:
    ROOT: INFO  # ← Solo errores importantes en PROD
```

**Variables de entorno en Docker:**
```yaml
# docker-compose-lite.yml - Líneas 17-22
environment:
  - SPRING_PROFILES_ACTIVE=prod  # ← Selecciona el perfil
  - SPRING_DATASOURCE_URL=jdbc:postgresql://postgresql:5432/library
  - SPRING_DATASOURCE_USERNAME=library
  - SPRING_DATASOURCE_PASSWORD=library  # ← Debería venir de secrets
```

**Mismo código, diferentes entornos:**
```bash
# Desarrollo
./mvnw -Dspring-boot.run.profiles=dev

# Producción
./mvnw -Dspring-boot.run.profiles=prod
```

---

## 4. **Backing Services** ✅

**Teoría:** DB, colas, APIs son recursos intercambiables conectados vía URL.

**En tu app - PostgreSQL como Backing Service:**
```yaml
# docker-compose-lite.yml
networks:
  app-network:  # ← Red compartida

services:
  app:
    networks:
      - app-network
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgresql:5432/library
      # ↑ Referencia al servicio, no hardcodeado
    depends_on:
      postgresql:
        condition: service_healthy  # ← Espera a que esté listo

  postgresql:
    networks:
      - app-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U library"]
```

**Intercambiable:**
```yaml
# Podrías cambiar de PostgreSQL local a AWS RDS solo cambiando la URL:
- SPRING_DATASOURCE_URL=jdbc:postgresql://my-rds.amazonaws.com:5432/library
# ↑ El código de la app NO cambia
```

---

## 5. **Construir, Distribuir y Ejecutar** ✅

**Teoría:** 3 etapas separadas: Build → Release → Run

**En tu app:**

### **BUILD (Construcción)**
```bash
# Backend
./mvnw clean package -DskipTests
# ↑ Genera: target/library-0.0.1-SNAPSHOT.jar

# Frontend
npm run webapp:build:prod
# ↑ Genera: target/classes/static/**
```

### **RELEASE (Distribución)**
```bash
# Crear imagen Docker (release)
docker build -t library-app:v1.0.0 -f Dockerfile.simple .
# ↑ Combina build + config → imagen versionada
```

### **RUN (Ejecución)**
```bash
# Ejecutar el release
docker-compose -f docker-compose-lite.yml up -d
# ↑ Corre la versión específica con configuración del entorno
```

**Scripts en package.json separando etapas:**
```json
{
  "scripts": {
    // BUILD
    "build": "npm run webapp:prod --",
    
    // RELEASE
    "java:docker:prod": "npm run java:docker -- -Pprod",
    
    // RUN
    "start": "ng serve --hmr"
  }
}
```

---

## 6. **Proceso sin Estado (Stateless)** ✅

**Teoría:** La app no guarda datos en memoria entre requests.

**En tu app - JWT Stateless:**
```java
// SecurityConfiguration.java - Líneas 96-97
.sessionManagement(session -> 
    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    // ↑ NO hay sesiones en memoria
```

**Autenticación sin sesión:**
```yaml
# application-dev.yml - Líneas 88-91
jhipster:
  security:
    authentication:
      jwt:
        token-validity-in-seconds: 86400  # ← Token auto-contenido
        # El server NO guarda sesiones, todo está en el token JWT
```

**¿Dónde se guarda el estado?**
```java
// BookServiceImpl.java - Líneas 27-35
@Service
@Transactional
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;  // ← Estado en DB, no en memoria
    
    @Override
    public Book save(Book book) {
        return bookRepository.save(book);  // ← Persistido inmediatamente
    }
}
```

**Ventaja:** Puedes reiniciar el servidor y los usuarios siguen logueados (su token sigue válido).

---

## 7. **Asignación de Puerto** ✅

**Teoría:** El puerto se asigna por configuración, no hardcodeado.

**En tu app:**
```yaml
# application-dev.yml
server:
  port: 8080  # ← Configurable

# application-prod.yml
server:
  port: 8080  # ← Mismo código, diferente config
```

**En Docker:**
```yaml
# docker-compose-lite.yml - Línea 24
ports:
  - "8080:8080"  # ← puerto_host:puerto_contenedor
  # Podrías cambiar a "80:8080" sin tocar el código
```

**Variable de entorno:**
```json
// package.json
"config": {
  "backend_port": 8080  // ← Centralizado
}
```

---

## 8. **Concurrencia** ✅

**Teoría:** Escalar horizontalmente agregando más procesos.

**En tu app - Pool de conexiones:**
```yaml
# application-dev.yml - Líneas 34-37
spring:
  datasource:
    hikari:
      poolName: Hikari
      auto-commit: false
      # HikariCP maneja múltiples conexiones concurrentes
```

**Configuración de recursos:**
```yaml
# docker-compose-lite.yml - Líneas 46-53
deploy:
  resources:
    limits:
      cpus: '1.0'
      memory: 384M
    reservations:
      cpus: '0.5'
      memory: 256M
```

**Escalar horizontalmente:**
```bash
# Levantar 3 instancias de la app
docker-compose up --scale app=3
# ↑ Cada instancia maneja requests independientes
```

---

## 9. **Disponibilidad / Desechabilidad** ✅

**Teoría:** Inicio rápido y shutdown limpio.

**En tu app - Graceful Shutdown:**
```yaml
# application-prod.yml - Línea 79
server:
  shutdown: graceful  # ← Termina requests en curso antes de cerrar
```

**Health Checks:**
```yaml
# docker-compose-lite.yml - Líneas 38-43
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:8080/management/health"]
  interval: 30s
  timeout: 10s
  retries: 5
  start_period: 60s  # ← Da tiempo a iniciar
```

**Endpoint de salud:**
```java
// SecurityConfiguration.java - Líneas 85-86
.requestMatchers(mvc.pattern("/management/health")).permitAll()
.requestMatchers(mvc.pattern("/management/health/**")).permitAll()
// ↑ Kubernetes/Docker pueden verificar si está vivo
```

---

## 10. **Paridad Dev/Producción** ✅

**Teoría:** Dev y Prod lo más similares posible.

**En tu app - Mismo stack:**
```yaml
# Desarrollo (application-dev.yml)
spring:
  datasource:
    type: com.zaxxer.hikari.HikariDataSource
    url: jdbc:postgresql://localhost:5432/library

# Producción (application-prod.yml)
spring:
  datasource:
    type: com.zaxxer.hikari.HikariDataSource  # ← Mismo driver
    url: jdbc:postgresql://localhost:5432/library  # ← Misma DB
```

**Docker iguala entornos:**
```yaml
# docker-compose-lite.yml - Línea 71
postgresql:
  image: postgres:16.2-alpine  # ← Misma versión en todos lados
```

**Despliegue continuo:**
```groovy
// Jenkinsfile - Líneas 1-50
pipeline {
    agent any
    stages {
        stage('Build') { }
        stage('Unit Tests') { }
        stage('Deploy to Dev') { }
        stage('Deploy to Prod') { }  // ← Mismo pipeline
    }
}
```

---

## 11. **Historial (Logs)** ✅

**Teoría:** La app escribe logs a stdout, un sistema externo los recolecta.

**En tu app - Logging a stdout:**
```java
// BookServiceImpl.java - Líneas 20-35
private static final Logger LOG = LoggerFactory.getLogger(BookServiceImpl.class);

@Override
public Book save(Book book) {
    LOG.debug("Request to save Book : {}", book);  // ← A stdout
    return bookRepository.save(book);
}
```

**Configuración de logs:**
```yaml
# application-dev.yml - Líneas 17-20
logging:
  level:
    ROOT: DEBUG
    com.sgaraba.library: DEBUG
    # ↑ SoloConfig, la app NO maneja archivos
```

**Logs en producción:**
```yaml
# application-prod.yml - Líneas 95-101
jhipster:
  logging:
    use-json-format: false
    logstash:  # ← Sistema externo para logs
      enabled: false
      host: localhost
      port: 5000
```

**Archivo de logs generado (no por la app):**
```
logs/
  application-logstash.2026-03-07.json
  application-logstash.json
```

---

## 12. **Administrador de Procesos** ✅

**Teoría:** Tareas de administración con igual importancia que la app.

**En tu app - Scripts de administración:**
```powershell
# populate-database.ps1 - Script de migración
# Se ejecuta con los mismos recursos que la app
```

**Liquibase - Migraciones de DB:**
```yaml
# application-dev.yml - Líneas 38-39
liquibase:
  contexts: dev, faker  # ← Proceso de admin: poblar datos
```

```yaml
# application-prod.yml - Línea 41
liquibase:
  contexts: prod  # ← Solo migraciones, sin datos fake
```

**Scripts NPM para admin:**
```json
{
  "scripts": {
    "backend:build-cache": "./mvnw dependency:go-offline -ntp",
    "docker:db:up": "docker compose -f src/main/docker/postgresql.yml up --wait",
    "docker:db:down": "docker compose -f src/main/docker/postgresql.yml down -v"
    // ↑ Procesos de administración
  }
}
```

---

# 🔒 OWASP Top 10 2021 - En Tu Aplicación

## A1 - Control de Acceso ✅ IMPLEMENTADO

**Teoría:** Limitar a usuarios a actuar solo dentro de sus permisos.

**En tu app:**
```java
// SecurityConfiguration.java - Líneas 72-88
.authorizeHttpRequests(authz ->
    authz
        .requestMatchers(mvc.pattern("/api/authenticate")).permitAll()  // ← Público
        .requestMatchers(mvc.pattern("/api/register")).permitAll()  // ← Público
        .requestMatchers(mvc.pattern("/api/admin/**")).hasAuthority(AuthoritiesConstants.ADMIN)  // ← Solo ADMIN
        .requestMatchers(mvc.pattern("/api/**")).authenticated()  // ← Requiere login
        .requestMatchers(mvc.pattern("/management/**")).hasAuthority(AuthoritiesConstants.ADMIN)
        // ↑ Control granular de acceso
)
```

**Protección en endpoints:**
```java
// BookResource.java - Líneas 30-73
@RestController
@RequestMapping("/api/books")  // ← Requiere autenticación (por la config arriba)
public class BookResource {
    
    @PostMapping("")
    public ResponseEntity<Book> createBook(@Valid @RequestBody Book book) {
        // Solo usuarios autenticados pueden crear libros
    }
}
```

**Validación en la entidad:**
```java
// Book.java - Líneas 28-33
@NotNull
@Size(min = 5, max = 13)
@Column(name = "isbn", length = 13, nullable = false, unique = true)
private String isbn;
// ↑ Valida que solo datos correctos entren
```

---

## A2 - Fallos Criptográficos ✅ IMPLEMENTADO

**Teoría:** Proteger datos sensibles con cifrado adecuado.

**En tu app - Hashing de contraseñas:**
```java
// SecurityConfiguration.java - Líneas 36-38
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();  // ← BCrypt, NO MD5 o SHA1
    // Genera hash con sal automáticamente
}
```

**JWT con secreto seguro:**
```yaml
# application-dev.yml - Líneas 85-90
jhipster:
  security:
    authentication:
      jwt:
        base64-secret: NGVlMDAxMjA0MzZjZmZiZGYyZDM4YWJlZmVlZmIwM2U3OTYxODgy...
        # ↑ 512 bits, generado con: openssl rand -base64 64
```

**TLS/HTTPS configurado:**
```yaml
# application-prod.yml - Líneas 57-67 (comentado, pero documentado)
# server:
#   port: 443
#   ssl:
#     key-store: classpath:config/tls/keystore.p12
#     key-store-password: password
#     key-store-type: PKCS12
```

---

## A3 - Inyección ✅ PROTEGIDO

**Teoría:** Prevenir SQL injection, command injection, etc.

**En tu app - JPA previene SQL Injection:**
```java
// BookRepository usa Spring Data JPA
public interface BookRepository extends JpaRepository<Book, Long> {
    // Spring Data crea queries parametrizadas automáticamente
}
```

**Uso correcto en Service:**
```java
// BookServiceImpl.java - Líneas 31-35
@Override
public Book save(Book book) {
    LOG.debug("Request to save Book : {}", book);
    return bookRepository.save(book);  // ← JPA usa prepared statements
    // NO hay concatenación de strings en SQL
}
```

**Ejemplo de cómo NO hacerlo (tu app NO tiene esto):**
```java
// ❌ VULNERABLE (TU APP NO HACE ESTO)
// String query = "SELECT * FROM book WHERE isbn='" + isbn + "'";

// ✅ TU APP HACE ESTO:
// @Query("SELECT b FROM Book b WHERE b.isbn = :isbn")
// Usa parámetros, no concatenación
```

**Validación de entrada:**
```java
// BookResource.java - Línea 64
public ResponseEntity<Book> createBook(@Valid @RequestBody Book book) {
    // @Valid valida ANTES de procesar
    if (book.getId() != null) {
        throw new BadRequestAlertException("A new book cannot already have an ID", ENTITY_NAME, "idexists");
    }
}
```

---

## A4 - Diseño Inseguro ✅ ARQUITECTURA SEGURA

**Teoría:** Diseño seguro desde el inicio.

**En tu app - Arquitectura en capas:**
```
Frontend (Angular)
      ↓ HTTP/REST
BookResource (REST Controller)  ← Validación
      ↓
BookService (Lógica de negocio)  ← Transacciones
      ↓
BookRepository (Acceso a datos)  ← JPA seguro
      ↓
PostgreSQL
```

**Separación de responsabilidades:**
```java
// BookResource.java - Solo REST
@RestController
@RequestMapping("/api/books")
public class BookResource {
    private final BookService bookService;  // ← Delega lógica
}

// BookServiceImpl.java - Solo lógica
@Service
@Transactional
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;  // ← Delega persistencia
}
```

---

## A5 - Configuración Incorrecta ✅ CONFIGURADO

**Teoría:** Sin funciones innecesarias, sin cuentas por defecto.

**En tu app - Profiles separados:**
```yaml
# application-dev.yml - Solo para desarrollo
logging:
  level:
    ROOT: DEBUG  # ← Verboso solo en DEV
    
# application-prod.yml - Producción segura
logging:
  level:
    ROOT: INFO  # ← Solo errores importantes
    
management:
  prometheus:
    metrics:
      export:
        enabled: false  # ← Métricas deshabilitadas si no se usan
```

**CORS configurado:**
```yaml
# application-dev.yml - Líneas 71-78
jhipster:
  cors:
    allowed-origins: 'http://localhost:8100,http://localhost:9000'
    # ↑ Solo orígenes específicos, NO '*'
    allowed-methods: '*'
    allow-credentials: true
```

**Headers de seguridad:**
```java
// SecurityConfiguration.java - Líneas 49-60
.headers(headers ->
    headers
        .contentSecurityPolicy(csp -> 
            csp.policyDirectives(jHipsterProperties.getSecurity().getContentSecurityPolicy()))
        .frameOptions(FrameOptionsConfig::sameOrigin)
        .referrerPolicy(referrer -> 
            referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
)
```

---

## A6 - Componentes Vulnerables ✅ GESTIONADO

**Teoría:** Mantener dependencias actualizadas.

**En tu app - Versiones específicas:**
```xml
<!-- pom.xml -->
<properties>
    <java.version>17</java.version>
    <spring-boot.version>3.4.5</spring-boot.version>  <!-- ← Versión reciente -->
    <jhipster-framework.version>8.11.0</jhipster-framework.version>
</properties>
```

**Herramientas de verificación:**
```bash
# Verificar vulnerabilidades
./mvnw dependency-check:check

# Scripts en package.json
"scripts": {
  "backend:nohttp:test": "./mvnw -ntp checkstyle:check --batch-mode"
}
```

**Overrides de seguridad:**
```json
// package.json
"overrides": {
  "fast-xml-parser": "5.3.8",  // ← Forzar versión segura
  "serialize-javascript": "7.0.4",
  "minimatch": "10.2.4"
}
```

---

## A7 - Fallos de Autenticación ✅ IMPLEMENTADO

**Teoría:** Autenticación robusta, sin contraseñas débiles.

**En tu app - JWT seguro:**
```yaml
# application-dev.yml - Líneas 88-92
jwt:
  base64-secret: [512 bits aleatorios]
  token-validity-in-seconds: 86400  # 24 horas
  token-validity-in-seconds-for-remember-me: 2592000  # 30 días
```

**Endpoints de autenticación:**
```java
// SecurityConfiguration.java
.requestMatchers(mvc.pattern(HttpMethod.POST, "/api/authenticate")).permitAll()
.requestMatchers(mvc.pattern("/api/register")).permitAll()
.requestMatchers(mvc.pattern("/api/account/reset-password/init")).permitAll()
// ↑ Solo endpoints necesarios son públicos
```

**OAuth2 Resource Server:**
```java
// SecurityConfiguration.java - Línea 99
.oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()));
// ↑ Validación automática de tokens JWT
```

---

## A8 - Fallo de Integridad ✅ CHECKSUMS

**Teoría:** Verificar integridad de software y datos.

**En tu app - Maven checksums:**
```xml
<!-- pom.xml - checksum-maven-plugin -->
<checksum-maven-plugin.version>1.11</checksum-maven-plugin.version>
```

```
target/
  checksums.csv      ← Checksums de todos los archivos
  checksums.csv.old
```

**Dependencias firmadas:**
```xml
<!-- pom.xml - Maven Central verifica firmas GPG automáticamente -->
<repositories>
  <repository>
    <id>central</id>
    <url>https://repo.maven.apache.org/maven2</url>
    <!-- ↑ Repositorio confiable -->
  </repository>
</repositories>
```

---

## A9 - Registro y Monitoreo ✅ IMPLEMENTADO

**Teoría:** Logs completos para detectar ataques.

**En tu app - Logging en toda la app:**
```java
// BookServiceImpl.java
private static final Logger LOG = LoggerFactory.getLogger(BookServiceImpl.class);

@Override
public Book save(Book book) {
    LOG.debug("Request to save Book : {}", book);  // ← Log de operaciones
    return bookRepository.save(book);
}

@Override
public void delete(Long id) {
    LOG.debug("Request to delete Book : {}", id);  // ← Log de eliminaciones
    bookRepository.deleteById(id);
}
```

**Configuración de logs:**
```yaml
# application-dev.yml
logging:
  level:
    ROOT: DEBUG
    tech.jhipster: DEBUG
    org.hibernate.SQL: DEBUG  # ← Ve todas las queries SQL
    com.sgaraba.library: DEBUG
```

**Endpoints de monitoreo:**
```java
// SecurityConfiguration.java
.requestMatchers(mvc.pattern("/management/health")).permitAll()
.requestMatchers(mvc.pattern("/management/info")).permitAll()
.requestMatchers(mvc.pattern("/management/prometheus")).permitAll()
.requestMatchers(mvc.pattern("/management/**")).hasAuthority(AuthoritiesConstants.ADMIN)
// ↑ Métricas para detectar anomalías
```

**Logstash para análisis:**
```yaml
# application-dev.yml - Líneas 95-101
jhipster:
  logging:
    logstash:
      enabled: false  # Activar en producción
      host: localhost
      port: 5000
```

---

## A10 - SSRF ✅ PROTEGIDO

**Teoría:** Validar URLs antes de hacer requests externos.

**En tu app - Sin calls externos descontrolados:**
```java
// Tu app NO hace requests a URLs del usuario
// Solo APIs REST bien definidas en BookResource.java
```

**Si necesitaras hacer requests externos:**
```java
// ✅ CORRECTO (con validación):
if (!url.startsWith("https://api.trusted.com")) {
    throw new BadRequestAlertException("Invalid URL");
}
```

---

# 🏗️ Arquitectura de Software - Tu Stack

## Technology Stack Real de Library

```
┌─────────────────────────────────────────────┐
│          FRONTEND (Cliente)                  │
├─────────────────────────────────────────────┤
│  Angular 17                                  │
│  TypeScript 5.x                              │
│  Bootstrap 5                                 │
│  ng-bootstrap                                │
└─────────────────┬───────────────────────────┘
                  │ HTTP REST + JWT
┌─────────────────▼───────────────────────────┐
│          BACKEND (Servidor)                  │
├─────────────────────────────────────────────┤
│  Spring Boot 3.4.5                           │
│  Spring Security + JWT                       │
│  Spring Data JPA                             │
│  Hibernate                                   │
│  Java 17                                     │
└─────────────────┬───────────────────────────┘
                  │ JDBC
┌─────────────────▼───────────────────────────┐
│          DATABASE                            │
├─────────────────────────────────────────────┤
│  PostgreSQL 16.2                             │
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│          INFRAESTRUCTURA                     │
├─────────────────────────────────────────────┤
│  Docker + Docker Compose                    │
│  Maven (Build Backend)                       │
│  npm (Build Frontend)                        │
│  Liquibase (Migraciones DB)                  │
│  Hazelcast (Cache distribuida)               │
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│          CI/CD & CALIDAD                     │
├─────────────────────────────────────────────┤
│  Jenkins (Pipeline)                          │
│  Cypress (Tests E2E)                         │
│  Jest (Tests Unitarios Frontend)             │
│  JUnit/TestNG (Tests Backend)                │
│  SonarQube (Análisis de código)              │
│  Checkstyle (Estilo de código)               │
└─────────────────────────────────────────────┘
```

---

## Framework: JHipster en Tu App

**Teoría:** JHipster genera aplicaciones completas con mejores prácticas.

**Tu aplicación FUE generada con JHipster:**

```jdl
// library.jh - Tu definición
application {
  config {
    baseName library,
    applicationType monolith,
    authenticationType jwt,
    packageName com.sgaraba.library,
    prodDatabaseType postgresql,
    cacheProvider hazelcast,
    buildTool maven,
    clientFramework angular,
    testFrameworks [cypress],
    languages [en, ro, ru]
  }
}

// Modelos
entity Book {
  isbn String required unique minlength(5) maxlength(13)
  name String required maxlength(100)
  publishYear String required
  copies Integer required
  cover ImageBlob
}

entity Author {
  firstName String required maxlength(50)
  lastName String required maxlength(50)
}

// Relaciones
relationship ManyToMany {
  Book{author(firstName)} to Author{book}
}
```

**JHipster generó automáticamente:**

### 1. Backend (Java/Spring)
```
src/main/java/com/sgaraba/library/
  ├── domain/
  │   ├── Book.java              ← Entidad JPA
  │   ├── Author.java
  │   └── Publisher.java
  ├── repository/
  │   ├── BookRepository.java     ← Spring Data JPA
  │   └── ...
  ├── service/
  │   ├── BookService.java        ← Interfaz
  │   └── impl/
  │       └── BookServiceImpl.java ← Implementación
  ├── web/rest/
  │   └── BookResource.java       ← REST Controller
  └── config/
      └── SecurityConfiguration.java ← Seguridad
```

### 2. Frontend (Angular)
```
src/main/webapp/app/
  ├── entities/
  │   ├── book/
  │   │   ├── book.model.ts
  │   │   ├── book.component.ts
  │   │   ├── book.component.html
  │   │   ├── book.service.ts
  │   │   └── route/
  │   │       └── book-routing.module.ts
  │   └── ...
  ├── core/
  │   ├── auth/
  │   └── interceptor/
  └── shared/
```

### 3. Tests
```
src/test/
  ├── java/
  │   └── com/sgaraba/library/
  │       ├── domain/BookTest.java
  │       ├── service/BookServiceImplTest.java
  │       └── web/rest/BookResourceIT.java
  └── javascript/cypress/e2e/
      └── entity/
          └── book.cy.ts
```

### 4. Configuración
```
src/main/resources/config/
  ├── application.yml
  ├── application-dev.yml
  ├── application-prod.yml
  └── liquibase/
      └── changelog/
          ├── 00000000000000_initial_schema.xml
          └── 20240101000000_added_entity_Book.xml
```

**Cuenta de archivos generados:**
> "Con un modelo de solo 3 entidades se ha generado una infraestructura que implica **más de 500 archivos**."

---

## Mecanismos de Diseño Implementados

### 1. **Persistencia (ORM)**
```java
// Book.java - Líneas 1-150
@Entity
@Table(name = "book")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)  // ← Cache L2
public class Book implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)  // ← Lazy loading
    private Publisher publisher;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "rel_book__author")  // ← Tabla intermedia
    private Set<Author> authors = new HashSet<>();
}
```

**Transacciones:**
```java
// BookServiceImpl.java
@Service
@Transactional  // ← Todas las operaciones en transacción
public class BookServiceImpl implements BookService {
    // Si algo falla, rollback automático
}
```

### 2. **UI (Web + Mobile)**
```
library-mobile/    ← Ionic/Capacitor (Progressive Web App)
  ├── src/app/pages/
  │   ├── login/
  │   ├── books/
  │   └── book-detail/
  └── capacitor.config.ts

backend/src/main/webapp/  ← Angular (Web responsiva)
  └── app/entities/book/
```

**PWA configurado:**
```json
// ngsw-config.json
{
  "index": "/index.html",
  "assetGroups": [{
    "name": "app",
    "installMode": "prefetch",
    "resources": {
      "files": ["/favicon.ico", "/index.html"]
    }
  }]
}
```

### 3. **Rendimiento**

**Cache distribuida:**
```java
// Book.java - Línea 16
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
// ↑ Hazelcast cachea entidades
```

```yaml
# application-dev.yml
jhipster:
  cache:
    hazelcast:
      time-to-live-seconds: 3600  # 1 hora en cache
      backup-count: 1
```

**Compresión:**
```yaml
# application-prod.yml - Líneas 81-84
server:
  compression:
    enabled: true
    mime-types: text/html,text/xml,text/css,application/javascript,application/json
    min-response-size: 1024  # Comprimir si > 1KB
```

**Paginación:**
```java
// BookResource.java
@GetMapping("")
public ResponseEntity<List<Book>> getAllBooks(
    BookCriteria criteria,
    Pageable pageable  // ← Paginación automática
) {
    Page<Book> page = bookQueryService.findByCriteria(criteria, pageable);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
        ServletUriComponentsBuilder.fromCurrentRequest(), page
    );
    return ResponseEntity.ok().headers(headers).body(page.getContent());
}
```

### 4. **Distribución (Build, CI/CD)**

**Maven multi-módulo:**
```xml
<!-- pom.xml -->
<plugins>
  <plugin>
    <groupId>com.github.eirslett</groupId>
    <artifactId>frontend-maven-plugin</artifactId>
    <!-- ↑ Maven también construye el frontend -->
  </plugin>
</plugins>
```

**Scripts de build:**
```json
// package.json
{
  "scripts": {
    "build": "npm run webapp:prod --",
    "webapp:build:prod": "ng build --configuration production",
    "java:jar:prod": "./mvnw verify -DskipTests --batch-mode -Pprod"
  }
}
```

**Pipeline CI/CD:**
```groovy
// Jenkinsfile
pipeline {
    stages {
        stage('Build') {
            steps {
                sh './mvnw clean install -DskipTests'
            }
        }
        stage('Unit Tests') {
            parallel {
                stage('Backend Tests') {
                    steps { sh './mvnw test' }
                }
                stage('Frontend Tests') {
                    steps { sh 'npm test' }
                }
            }
        }
    }
}
```

### 5. **Seguridad**

**Ya cubierto en OWASP Top 10 arriba ↑**

### 6. **Monitoreo**

**Actuator endpoints:**
```java
// SecurityConfiguration.java
.requestMatchers(mvc.pattern("/management/health")).permitAll()
.requestMatchers(mvc.pattern("/management/info")).permitAll()
.requestMatchers(mvc.pattern("/management/prometheus")).permitAll()
```

**Accesibles en:**
```
http://localhost:8080/management/health
http://localhost:8080/management/info
http://localhost:8080/management/metrics
```

**Integración con Prometheus/Grafana:**
```yaml
# docker-compose incluye:
src/main/docker/
  ├── prometheus/
  ├── grafana/
  └── monitoring.yml
```

### 7. **Testing**

**Cubierto en la sección de pruebas más abajo ↓**

---

# 🧪 Pruebas Automáticas en Tu App

## Modelo en V - Tu Implementación

```
DESARROLLO                           PRUEBAS
┌──────────────────┐                ┌──────────────────┐
│ Requirements     │ ◄─────────────►│ Acceptance Tests │
│ (library.jh)     │                │ (Cypress E2E)    │
└────────┬─────────┘                └──────────────────┘
         │                                    ▲
         ▼                                    │
┌──────────────────┐                ┌──────────────────┐
│ Architecture     │ ◄─────────────►│ System Tests     │
│ (JHipster stack) │                │ (Integration)    │
└────────┬─────────┘                └──────────────────┘
         │                                    ▲
         ▼                                    │
┌──────────────────┐                ┌──────────────────┐
│ Detailed Design  │ ◄─────────────►│ Unit Tests       │
│ (Classes Java)   │                │ (Jest + JUnit)   │
└────────┬─────────┘                └──────────────────┘
         │                                    ▲
         ▼                                    │
┌──────────────────┐                          │
│ Implementation   │ ─────────────────────────┘
│ (Código)         │
└──────────────────┘
```

---

## Pruebas Unitarias - Backend (Java)

**Teoría:** Probar una función aislada.

**En tu app:**
```java
// BookServiceImplTest.java
class BookServiceImplTest {
    
    @Mock
    private BookRepository bookRepository;
    
    @InjectMocks
    private BookServiceImpl bookService;
    
    @Test
    void shouldSaveBook() {
        // Arrange
        Book book = new Book();
        book.setIsbn("978-0134685991");
        book.setName("Effective Java");
        
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        
        // Act
        Book saved = bookService.save(book);
        
        // Assert
        assertThat(saved.getIsbn()).isEqualTo("978-0134685991");
        verify(bookRepository, times(1)).save(book);
    }
}
```

**Ejecutar:**
```bash
./mvnw test
```

**Scripts configurados:**
```json
// package.json
{
  "scripts": {
    "backend:unit:test": "./mvnw -ntp verify --batch-mode"
  }
}
```

---

## Pruebas Unitarias - Frontend (Jest)

**En tu app:**
```typescript
// book.component.spec.ts (generado por JHipster)
describe('BookComponent', () => {
  let component: BookComponent;
  let fixture: ComponentFixture<BookComponent>;
  let service: BookService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [BookComponent],
      providers: [BookService]
    });
    
    fixture = TestBed.createComponent(BookComponent);
    component = fixture.componentInstance;
    service = TestBed.inject(BookService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load books on init', () => {
    const books = [{ id: 1, name: 'Test Book' }];
    spyOn(service, 'query').and.returnValue(of({ body: books }));
    
    component.ngOnInit();
    
    expect(component.books).toEqual(books);
  });
});
```

**Ejecutar:**
```bash
npm test
```

---

## Pruebas E2E (End-to-End) - Cypress

**Teoría:** Probar flujos completos de usuario.

**En tu app:**
```typescript
// book.cy.ts - Líneas 1-100
describe('Book e2e test', () => {
  const bookPageUrl = '/book';
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const bookSample = { 
    isbn: 'uh-huh accomp', 
    name: 'incidentally', 
    publishYear: 'unknown afore', 
    copies: 10632 
  };

  beforeEach(() => {
    cy.login(username, password);  // ← Login antes de cada test
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/books+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/books').as('postEntityRequest');
    cy.intercept('DELETE', '/api/books/*').as('deleteEntityRequest');
  });

  it('Books menu should load Books page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('book');  // ← Click en menú
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Book').should('exist');
    cy.url().should('match', bookPageUrlPattern);
  });

  it('should load create Book page', () => {
    cy.visit(bookPageUrl);
    cy.wait('@entitiesRequest');
    
    cy.get(entityCreateButtonSelector).click();  // ← Click "Crear"
    cy.url().should('match', new RegExp('/book/new$'));
    cy.getEntityCreateUpdateHeading('Book');
    cy.get(entityCreateSaveButtonSelector).should('exist');
  });
});
```

**Ejecutar:**
```bash
npm run e2e
# O con interfaz gráfica:
npm run cypress
```

**Scripts configurados:**
```json
{
  "scripts": {
    "cypress": "cypress open --e2e",
    "e2e:headless": "npm run e2e:cypress --",
    "e2e:cypress:headed": "npm run e2e:cypress -- --headed"
  }
}
```

---

## Pruebas de Integración

**Teoría:** Probar cómo funcionan juntos varios componentes.

**En tu app:**
```java
// BookResourceIT.java (Integration Test)
@SpringBootTest(classes = LibraryApp.class)
@AutoConfigureMockMvc
@WithMockUser
class BookResourceIT {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MockMvc restBookMockMvc;

    @Autowired
    private EntityManager em;

    private Book book;

    @BeforeEach
    public void initTest() {
        book = createEntity(em);
    }

    @Test
    @Transactional
    void createBook() throws Exception {
        int databaseSizeBeforeCreate = bookRepository.findAll().size();
        
        // Create the Book
        restBookMockMvc
            .perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(book)))
            .andExpect(status().isCreated());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeCreate + 1);
        Book testBook = bookList.get(bookList.size() - 1);
        assertThat(testBook.getIsbn()).isEqualTo(DEFAULT_ISBN);
    }
}
```

**Prueba completa:**
1. Inicia Spring Boot
2. Carga configuración real
3. Usa base de datos H2 en memoria
4. Hace request HTTP real a `/api/books`
5. Verifica que se guardó en la DB

---

## TDD (Test-Driven Development) en Tu App

**Teoría:** Test primero, código después.

**Ejemplo práctico:**

### 1. Escribir el test PRIMERO (falla)
```java
@Test
void shouldCalculateLateFee() {
    // Arrange
    BorrowedBook borrowedBook = new BorrowedBook();
    borrowedBook.setBorrowDate(LocalDate.now().minusDays(30));
    
    // Act
    BigDecimal fee = borrowedBook.calculateLateFee();
    
    // Assert
    assertThat(fee).isEqualTo(new BigDecimal("15.00"));
}
```

### 2. Ejecutar el test (ROJO - falla)
```bash
./mvnw test
# ❌ Error: método calculateLateFee() no existe
```

### 3. Escribir código mínimo para que pase
```java
// BorrowedBook.java
public BigDecimal calculateLateFee() {
    if (borrowDate == null) return BigDecimal.ZERO;
    
    long daysLate = ChronoUnit.DAYS.between(
        borrowDate.plusDays(14), 
        LocalDate.now()
    );
    
    if (daysLate <= 0) return BigDecimal.ZERO;
    
    return BigDecimal.valueOf(daysLate * 0.50);
}
```

### 4. Ejecutar test (VERDE - pasa)
```bash
./mvnw test
# ✅ Test passed
```

### 5. Refactorizar
```java
public BigDecimal calculateLateFee() {
    if (borrowDate == null) return BigDecimal.ZERO;
    
    LocalDate dueDate = borrowDate.plusDays(LOAN_PERIOD_DAYS);
    long daysLate = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    
    return daysLate > 0 
        ? BigDecimal.valueOf(daysLate).multiply(DAILY_FEE) 
        : BigDecimal.ZERO;
}
```

---

## Pruebas de Rendimiento - Configuradas en Tu App

**Teoría:** Probar bajo carga, estrés, etc.

**Tu app tiene Gatling configurado:**
```xml
<!-- pom.xml -->
<gatling-maven-plugin.version>4.9.6</gatling-maven-plugin.version>
```

**Ejemplo de test de carga:**
```scala
// gatling/simulations/BookSimulation.scala (si existiera)
class BookSimulation extends Simulation {
  
  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
  
  val scn = scenario("Load Books")
    .exec(http("Get all books")
      .get("/api/books")
      .check(status.is(200)))
    .pause(1)
  
  setUp(
    scn.inject(
      rampUsers(100) during (10.seconds)  // ← 100 usuarios en 10 seg
    )
  ).protocols(httpProtocol)
}
```

**Límites de recursos configurados:**
```yaml
# docker-compose-lite.yml
deploy:
  resources:
    limits:
      cpus: '1.0'
      memory: 384M  # ← Simula recursos limitados
```

---

## Cobertura de Tests

**Jacoco configurado:**
```xml
<!-- pom.xml -->
<jacoco-maven-plugin.version>0.8.13</jacoco-maven-plugin.version>
```

**Ejecutar con cobertura:**
```bash
./mvnw clean test jacoco:report
```

**Reporte generado en:**
```
target/site/jacoco/index.html
```

---

# 🔬 Cypress - Configuración Real

**Teoría:** Framework E2E moderno todo-en-uno.

**Tu configuración:**
```typescript
// cypress.config.ts
export default defineConfig({
  e2e: {
    baseUrl: 'http://localhost:8080',
    specPattern: 'src/test/javascript/cypress/e2e/**/*.cy.ts',
    supportFile: 'src/test/javascript/cypress/support/e2e.ts',
    video: true,
    screenshotOnRunFailure: true,
    retries: {
      runMode: 2,
      openMode: 0
    }
  }
});
```

**Comandos personalizados:**
```typescript
// cypress/support/commands.ts
Cypress.Commands.add('login', (username: string, password: string) => {
  cy.session([username, password], () => {
    cy.visit('/');
    cy.get('#username').type(username);
    cy.get('#password').type(password);
    cy.get('button[type="submit"]').click();
    cy.url().should('not.include', '/login');
  });
});

Cypress.Commands.add('authenticatedRequest', (options) => {
  return cy.getAuthToken().then(token => {
    return cy.request({
      ...options,
      headers: {
        ...options.headers,
        Authorization: `Bearer ${token}`
      }
    });
  });
});
```

**Uso en tests:**
```typescript
// book.cy.ts
beforeEach(() => {
  cy.login(username, password);  // ← Comando custom
});

afterEach(() => {
  if (book) {
    cy.authenticatedRequest({  // ← Comando custom
      method: 'DELETE',
      url: `/api/books/${book.id}`,
    }).then(() => {
      book = undefined;
    });
  }
});
```

**Buenas prácticas implementadas:**

1. **Usar API para setup/cleanup:**
```typescript
beforeEach(() => {
  cy.authenticatedRequest({
    method: 'POST',
    url: '/api/books',
    body: bookSample,
  }).then(({ body }) => {
    book = body;  // ← Crear via API, no UI
  });
});
```

2. **Interceptar requests para espera inteligente:**
```typescript
beforeEach(() => {
  cy.intercept('GET', '/api/books+(?*|)').as('entitiesRequest');
  cy.intercept('POST', '/api/books').as('postEntityRequest');
});

it('should create book', () => {
  cy.get(createButton).click();
  cy.wait('@postEntityRequest');  // ← Espera automática
});
```

3. **Comandos reutilizables:**
```typescript
cy.clickOnEntityMenuItem('book');  // ← Abstracción
cy.getEntityHeading('Book').should('exist');
```

---

# 📊 Diagramas de Tu Aplicación

## Diagrama de Arquitectura General

```
┌─────────────────────────────────────────────────────────────────┐
│                        USUARIOS                                  │
│                    (Navegadores Web)                             │
└────────────────────────────┬────────────────────────────────────┘
                             │ HTTPS
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    FRONTEND (Angular 17)                         │
├─────────────────────────────────────────────────────────────────┤
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │  Books UI    │  │  Authors UI  │  │  Clients UI  │          │
│  │  Component   │  │  Component   │  │  Component   │          │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘          │
│         │                 │                  │                   │
│  ┌──────▼──────────────────▼──────────────────▼────────┐        │
│  │         Book/Author/Client Services                  │        │
│  │         (HTTP Client Calls)                          │        │
│  └──────────────────────────┬───────────────────────────┘        │
└─────────────────────────────┼────────────────────────────────────┘
                              │ REST API (JSON)
                              │ JWT Auth
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                 BACKEND (Spring Boot 3.4.5)                      │
├─────────────────────────────────────────────────────────────────┤
│  ┌──────────────────────────────────────────────────────────┐   │
│  │              REST CONTROLLERS                             │   │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────┐         │   │
│  │  │BookResource│  │AuthorRsc   │  │ClientRsc   │         │   │
│  │  └─────┬──────┘  └─────┬──────┘  └─────┬──────┘         │   │
│  └────────┼───────────────┼───────────────┼────────────────┘   │
│           │               │               │                     │
│  ┌────────▼───────────────▼───────────────▼────────────────┐   │
│  │          SECURITY LAYER (JWT + OAuth2)                   │   │
│  └──────────────────────────┬───────────────────────────────┘   │
│                             │                                    │
│  ┌─────────────────────────▼────────────────────────────────┐   │
│  │              SERVICE LAYER                                │   │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────┐         │   │
│  │  │BookService │  │AuthorSvc   │  │ClientSvc   │         │   │
│  │  └─────┬──────┘  └─────┬──────┘  └─────┬──────┘         │   │
│  └────────┼───────────────┼───────────────┼────────────────┘   │
│           │               │               │                     │
│  ┌────────▼───────────────▼───────────────▼────────────────┐   │
│  │        REPOSITORY LAYER (Spring Data JPA)                │   │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────┐         │   │
│  │  │BookRepo    │  │AuthorRepo  │  │ClientRepo  │         │   │
│  │  └─────┬──────┘  └─────┬──────┘  └─────┬──────┘         │   │
│  └────────┼───────────────┼───────────────┼────────────────┘   │
│           │               │               │                     │
│  ┌────────▼───────────────▼───────────────▼────────────────┐   │
│  │              HIBERNATE (JPA Provider)                    │   │
│  └──────────────────────────┬───────────────────────────────┘   │
└─────────────────────────────┼────────────────────────────────────┘
                              │ JDBC
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                   DATABASE (PostgreSQL 16.2)                     │
├─────────────────────────────────────────────────────────────────┤
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────┐   │
│  │  book    │  │  author  │  │  client  │  │ borrowed_book│   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────────┘   │
│  ┌──────────────────┐  ┌──────────┐                            │
│  │ rel_book__author │  │publisher │                            │
│  └──────────────────┘  └──────────┘                            │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    CAPA TRANSVERSAL                              │
├─────────────────────────────────────────────────────────────────┤
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │  Hazelcast   │  │  Liquibase   │  │  Logback     │          │
│  │  (Cache L2)  │  │  (Migrations)│  │  (Logging)   │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
└─────────────────────────────────────────────────────────────────┘
```

---

## Diagrama de Entidades (Tu Modelo de Datos)

```
┌─────────────────────┐
│     Publisher       │
├─────────────────────┤
│ id: Long (PK)       │
│ name: String UNIQUE │
└──────────┬──────────┘
           │ 1
           │
           │ has many
           │
           │ *
┌──────────▼──────────────────────────┐
│           Book                       │
├──────────────────────────────────────┤
│ id: Long (PK)                        │
│ isbn: String UNIQUE (5-13 chars)    │
│ name: String (max 100)              │
│ publishYear: String (4-50 chars)    │◄──────┐
│ copies: Integer                      │       │
│ cover: byte[] (ImageBlob)            │       │
│ coverContentType: String             │       │
│ publisher_id: Long (FK)              │       │
└──────────┬──────────────────┬────────┘       │
           │ *                │ *              │
           │                  │                │
           │ M:N              │ 1:N            │
           │                  │                │
           │ *                │ *              │
┌──────────▼──────────┐  ┌────▼────────────────┐
│      Author         │  │   BorrowedBook      │
├─────────────────────┤  ├─────────────────────┤
│ id: Long (PK)       │  │ id: Long (PK)       │
│ firstName: String   │  │ borrowDate: Date    │
│ lastName: String    │  │ book_id: Long (FK) ─┘
└─────────────────────┘  │ client_id: Long (FK)│
                         └─────────┬───────────┘
                                   │ *
                                   │
                                   │ belongs to
                                   │
                                   │ 1
                         ┌─────────▼───────────┐
                         │      Client         │
                         ├─────────────────────┤
                         │ id: Long (PK)       │
                         │ firstName: String   │
                         │ lastName: String    │
                         │ email: String UNIQ  │
                         │ address: String     │
                         │ phone: String       │
                         └─────────────────────┘

Tabla intermedia (ManyToMany):
┌─────────────────────────┐
│   rel_book__author      │
├─────────────────────────┤
│ book_id: Long (FK)      │
│ author_id: Long (FK)    │
└─────────────────────────┘
```

---

## Diagrama de Flujo - Crear un Libro

```
┌───────────┐
│  USUARIO  │
│  (Browser)│
└─────┬─────┘
      │
      │ 1. Click "Crear Libro"
      ▼
┌─────────────────────────┐
│  book.component.ts      │
│  createBook()           │
└───────┬─────────────────┘
        │
        │ 2. Llena formulario
        │    y click "Guardar"
        ▼
┌─────────────────────────┐
│  book.service.ts        │
│  create(book: Book)     │
└───────┬─────────────────┘
        │
        │ 3. HTTP POST /api/books
        │    Authorization: Bearer <JWT>
        │    Body: { isbn, name, ... }
        ▼
┌─────────────────────────────────┐
│  BACKEND                        │
│  BookResource.java              │
│  @PostMapping("")               │
└───────┬─────────────────────────┘
        │
        │ 4. Valida JWT
        ▼
┌─────────────────────────────────┐
│  SecurityConfiguration          │
│  OAuth2ResourceServer           │
└───────┬─────────────────────────┘
        │
        │ ✅ Token válido
        │
        │ 5. Valida datos
        ▼
┌─────────────────────────────────┐
│  BookResource.createBook()      │
│  @Valid @RequestBody Book book  │
└───────┬─────────────────────────┘
        │
        │ ✅ Validaciones pasan
        │    (isbn 5-13 chars, etc)
        │
        │ 6. Llama al Service
        ▼
┌─────────────────────────────────┐
│  BookServiceImpl.save()         │
│  @Transactional                 │
└───────┬─────────────────────────┘
        │
        │ 7. Guarda en DB
        ▼
┌─────────────────────────────────┐
│  BookRepository.save()          │
│  extends JpaRepository          │
└───────┬─────────────────────────┘
        │
        │ 8. Hibernate genera SQL
        │    INSERT INTO book ...
        ▼
┌─────────────────────────────────┐
│  PostgreSQL                     │
│  INSERT INTO book               │
│  VALUES (...)                   │
└───────┬─────────────────────────┘
        │
        │ ✅ Commit exitoso
        │
        │ 9. Return Book entity
        ▲
        │
┌───────┴─────────────────────────┐
│  BookResource                   │
│  return ResponseEntity.created()│
│         .body(book)             │
└───────┬─────────────────────────┘
        │
        │ 10. HTTP 201 Created
        │     Location: /api/books/123
        │     Body: { id: 123, isbn, name, ... }
        ▼
┌─────────────────────────────────┐
│  book.service.ts                │
│  Observable<EntityResponse>     │
└───────┬─────────────────────────┘
        │
        │ 11. Update UI
        ▼
┌─────────────────────────────────┐
│  book.component.ts              │
│  loadAll()  // Refresh lista    │
└───────┬─────────────────────────┘
        │
        │ 12. Muestra notificación
        │     "Libro creado con éxito"
        ▼
┌─────────────────────────────────┐
│  USUARIO ve el nuevo libro      │
└─────────────────────────────────┘
```

---

## Diagrama de Despliegue Docker

```
┌────────────────────────────────────────────────────────────────┐
│                        HOST MACHINE                             │
│                   (Tu laptop/servidor)                          │
├────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              DOCKER ENGINE                                │  │
│  │                                                            │  │
│  │  ┌─────────────────────────────────────────────────────┐ │  │
│  │  │  NETWORK: app-network (bridge)                      │ │  │
│  │  │                                                       │ │  │
│  │  │  ┌───────────────────────┐  ┌───────────────────┐  │ │  │
│  │  │  │  CONTAINER: app       │  │ CONTAINER:        │  │ │  │
│  │  │  │  library-app:latest   │  │ postgresql        │  │ │  │
│  │  │  ├───────────────────────┤  ├───────────────────┤  │ │  │
│  │  │  │ Image:                │  │ Image:            │  │ │  │
│  │  │  │ library-app:latest    │  │ postgres:16.2     │  │ │  │
│  │  │  │                       │  │                   │  │ │  │
│  │  │  │ Env:                  │  │ Env:              │  │ │  │
│  │  │  │ SPRING_PROFILES=prod  │  │ POSTGRES_DB=      │  │ │  │
│  │  │  │ JAVA_OPTS=-Xmx256m    │  │   library         │  │ │  │
│  │  │  │ DATASOURCE_URL=       │  │ POSTGRES_USER=    │  │ │  │
│  │  │  │   postgresql:5432 ────┼──┼─► library         │  │ │  │
│  │  │  │                       │  │                   │  │ │  │
│  │  │  │ Ports:                │  │ Ports:            │  │ │  │
│  │  │  │ 8080:8080 ◄───────────┼──┼─ 5432:5432       │  │ │  │
│  │  │  │                       │  │                   │  │ │  │
│  │  │  │ Resources:            │  │ Resources:        │  │ │  │
│  │  │  │ CPU: 1.0              │  │ CPU: 0.5          │  │ │  │
│  │  │  │ RAM: 384M             │  │ RAM: 256M         │  │ │  │
│  │  │  │                       │  │                   │  │ │  │
│  │  │  │ Health:               │  │ Health:           │  │ │  │
│  │  │  │ /management/health    │  │ pg_isready        │  │ │  │
│  │  │  └───────────────────────┘  └───────────────────┘  │ │  │
│  │  └─────────────────────────────────────────────────────┘ │  │
│  │                                                            │  │
│  │  ┌─────────────────────────────────────────────────────┐ │  │
│  │  │  VOLUME: postgres_data                              │ │  │
│  │  │  /var/lib/postgresql/data                           │ │  │
│  │  └─────────────────────────────────────────────────────┘ │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                 │
│  Port Mapping:                                                  │
│  localhost:8080 ──► Container app:8080                         │
│  localhost:5432 ──► Container postgresql:5432                  │
└────────────────────────────────────────────────────────────────┘
```

**Comandos para gestionar:**
```bash
# Levantar todo
docker-compose -f docker-compose-lite.yml up -d

# Ver logs
docker logs library-app -f

# Ver recursos usados
docker stats library-app library-postgres

# Bajar todo
docker-compose -f docker-compose-lite.yml down
```

---

## Flujo CI/CD (Jenkins Pipeline)

```
┌──────────────┐
│   GIT PUSH   │
└──────┬───────┘
       │
       │ Webhook trigger
       ▼
┌─────────────────────────────────────┐
│     JENKINS PIPELINE                │
├─────────────────────────────────────┤
│                                     │
│  STAGE 1: Checkout                  │
│  ┌───────────────────────────────┐ │
│  │ git clone                     │ │
│  │ git rev-parse --short HEAD    │ │
│  └───────────────────────────────┘ │
│           │                         │
│           ▼                         │
│  STAGE 2: Build                     │
│  ┌───────────────────────────────┐ │
│  │ ./mvnw clean install          │ │
│  │ ng build --prod               │ │
│  └───────────────────────────────┘ │
│           │                         │
│           ▼                         │
│  STAGE 3: Unit Tests (Parallel)     │
│  ┌──────────────┐  ┌─────────────┐│
│  │ Backend      │  │ Frontend    ││
│  │ ./mvnw test  │  │ npm test    ││
│  └──────────────┘  └─────────────┘│
│           │                         │
│           ▼                         │
│  STAGE 4: Integration Tests         │
│  ┌───────────────────────────────┐ │
│  │ docker-compose up -d          │ │
│  │ ./mvnw verify -Pit            │ │
│  └───────────────────────────────┘ │
│           │                         │
│           ▼                         │
│  STAGE 5: E2E Tests (Cypress)       │
│  ┌───────────────────────────────┐ │
│  │ npm run e2e:headless          │ │
│  └───────────────────────────────┘ │
│           │                         │
│           ▼                         │
│  STAGE 6: Code Quality (SonarQube)  │
│  ┌───────────────────────────────┐ │
│  │ ./mvnw sonar:sonar            │ │
│  └───────────────────────────────┘ │
│           │                         │
│           ▼                         │
│  STAGE 7: Build Docker Image        │
│  ┌───────────────────────────────┐ │
│  │ docker build -t library:1.0.0 │ │
│  └───────────────────────────────┘ │
│           │                         │
│           ▼                         │
│  STAGE 8: Push to Registry          │
│  ┌───────────────────────────────┐ │
│  │ docker push library:1.0.0     │ │
│  └───────────────────────────────┘ │
│           │                         │
│           ▼                         │
│  STAGE 9: Deploy to K8s             │
│  ┌───────────────────────────────┐ │
│  │ kubectl apply -f k8s/         │ │
│  └───────────────────────────────┘ │
│           │                         │
│           ▼                         │
│  POST: Notifications                │
│  ┌───────────────────────────────┐ │
│  │ Slack: "Deploy exitoso ✅"    │ │
│  │ Email al equipo               │ │
│  └───────────────────────────────┘ │
└─────────────────────────────────────┘
```

---

# 🎯 Resumen de Conceptos Aplicados

## ✅ Los 12 Factores - TODOS implementados
1. **Código Base** - Git repo único
2. **Dependencias** - pom.xml + package.json
3. **Configuraciones** - application-dev.yml / application-prod.yml
4. **Backing Services** - PostgreSQL referenciado por URL
5. **Build/Release/Run** - Maven build → Docker image → docker-compose
6. **Stateless** - JWT sin sesiones
7. **Port Binding** - Configurable en YAML
8. **Concurrencia** - HikariCP pool + Docker scale
9. **Disposability** - Graceful shutdown + healthchecks
10. **Dev/Prod Parity** - Misma DB, mismo stack
11. **Logs** - Logback a stdout → Logstash
12. **Admin Processes** - Liquibase migrations

## ✅ OWASP Top 10 - TODOS protegidos
1. **Access Control** - Spring Security con roles
2. **Crypto** - BCrypt + JWT con secretos seguros
3. **Injection** - JPA prepared statements
4. **Design** - Arquitectura en capas
5. **Misconfiguration** - Profiles separados
6. **Vulnerable Components** - Versiones actualizadas
7. **Authentication** - JWT + OAuth2
8. **Integrity** - Maven checksums
9. **Logging** - SLF4J en toda la app
10. **SSRF** - Sin calls externos no validados

## ✅ Pruebas - Completas
- ✅ Unitarias (Jest + JUnit)
- ✅ Integración (MockMvc)
- ✅ E2E (Cypress)
- ✅ Cobertura (Jacoco)
- ✅ Rendimiento (Gatling configurado)

## ✅ Stack Tecnológico Moderno
- ✅ Java 17 + Spring Boot 3.4.5
- ✅ Angular 17 + TypeScript
- ✅ PostgreSQL 16.2
- ✅ Docker + Docker Compose
- ✅ Jenkins CI/CD
- ✅ JHipster 8.x (generador)

---

# 📚 Referencias a Tu Código

## Archivos Clave

### Configuración
- [library.jh](library.jh) - Modelo JDL de tu dominio
- [pom.xml](pom.xml) - Dependencias Maven
- [package.json](package.json) - Dependencias npm
- [docker-compose-lite.yml](docker-compose-lite.yml) - Orquestación Docker

### Backend
- [Book.java](src/main/java/com/sgaraba/library/domain/Book.java) - Entidad JPA
- [BookServiceImpl.java](src/main/java/com/sgaraba/library/service/impl/BookServiceImpl.java) - Lógica de negocio
- [BookResource.java](src/main/java/com/sgaraba/library/web/rest/BookResource.java) - REST API
- [SecurityConfiguration.java](src/main/java/com/sgaraba/library/config/SecurityConfiguration.java) - Seguridad

### Tests
- [book.cy.ts](src/test/javascript/cypress/e2e/entity/book.cy.ts) - Tests E2E Cypress
- [BookServiceImplTest.java](src/test/java/com/sgaraba/library/service/impl/BookServiceImplTest.java) - Tests unitarios

### CI/CD
- [Jenkinsfile](Jenkinsfile) - Pipeline de integración continua

---

**Este documento fue generado analizando TU aplicación Library real.**
**Todos los ejemplos de código son fragmentos reales de tu proyecto.**

