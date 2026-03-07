# Manual de Creación de Aplicación con Calidad Asegurada

> **Objetivo:** Este manual es una guía de referencia permanente para garantizar que cada aplicación desarrollada cumpla con los estándares de calidad, seguridad, testing, logs, CI/CD y PWA.

---

## Tabla de Contenidos

1. [Principios Fundamentales](#1-principios-fundamentales)
2. [Configuración Inicial del Proyecto](#2-configuración-inicial-del-proyecto)
3. [Seguridad en Aplicaciones](#3-seguridad-en-aplicaciones)
4. [Pruebas Automáticas](#4-pruebas-automáticas)
5. [Gestión de Logs](#5-gestión-de-logs)
6. [Integración Continua (CI/CD)](#6-integración-continua-cicd)
7. [Progressive Web App (PWA)](#7-progressive-web-app-pwa)
8. [Arquitectura y Componentes](#8-arquitectura-y-componentes)
9. [Checklist de Calidad](#9-checklist-de-calidad)
10. [Mantenimiento y Mejora Continua](#10-mantenimiento-y-mejora-continua)

---

## 1. Principios Fundamentales

### 1.1 Los 12 Factores

Toda aplicación debe cumplir con los 12 factores para ser compatible con la nube:

| Factor | Descripción | Implementación en el Proyecto |
|--------|-------------|-------------------------------|
| **Código Base** | Un único repositorio con versionado Git | ✓ Backend en repositorio Git |
| **Dependencias** | Declaradas explícitamente | ✓ `pom.xml` (Maven) y `package.json` (npm) |
| **Configuración** | Separada del código | ✓ `application-dev.yml`, `application-prod.yml` |
| **Backing Services** | Tratados como recursos adjuntos | ✓ PostgreSQL en contenedor Docker |
| **Build/Release/Run** | Etapas separadas | ✓ Maven build, Docker deploy |
| **Procesos** | Sin estado (stateless) | ✓ JWT para autenticación |
| **Asignación de Puerto** | Puerto configurado externamente | ✓ Configurado en `application.yml` |
| **Concurrencia** | Escalar mediante procesos | ✓ Hazelcast para caché distribuido |
| **Disponibilidad** | Inicio rápido, apagado controlado | ✓ Spring Boot graceful shutdown |
| **Paridad Dev/Prod** | Entornos similares | ✓ Docker Compose para desarrollo |
| **Logs** | Streams de eventos | ✓ Logback + ELK Stack |
| **Admin Processes** | Tareas de administración | ✓ Liquibase para migraciones |

### 1.2 Principios de Desarrollo

```markdown
✓ Mobile First: Diseñar primero para móvil
✓ Progressive Enhancement: Mejorar progresivamente
✓ Test-Driven Development: Tests antes que código
✓ Continuous Integration: Integrar frecuentemente
✓ Security by Design: Seguridad desde el diseño
```

---

## 2. Configuración Inicial del Proyecto

### 2.1 Generar con JHipster

```bash
# Instalar JHipster
npm install -g generator-jhipster

# Generar aplicación
jhipster

# Generar desde JDL
jhipster jdl library.jh
```

### 2.2 Estructura de Archivos Críticos

```
backend/
├── src/main/
│   ├── java/com/              # Código backend
│   ├── resources/
│   │   ├── config/            # Configuraciones por entorno
│   │   ├── logback-spring.xml # Configuración de logs
│   │   └── banner.txt
│   └── webapp/                # Frontend Angular
│       ├── app/
│       ├── content/
│       └── manifest.webapp    # PWA manifest
├── src/test/
│   ├── java/                  # Tests unitarios Java
│   └── javascript/cypress/    # Tests E2E Cypress
├── pom.xml                    # Dependencias Maven
├── package.json               # Dependencias npm
├── cypress.config.ts          # Configuración Cypress
└── sonar-project.properties   # Análisis de calidad
```

### 2.3 Configuración de Entornos

**application-dev.yml**
```yaml
spring:
  profiles:
    active: dev
  datasource:
    url: jdbc:postgresql://localhost:5432/library_dev
server:
  port: 8080
logging:
  level:
    ROOT: DEBUG
    com.sgaraba: DEBUG
```

**application-prod.yml**
```yaml
spring:
  profiles:
    active: prod
  datasource:
    url: ${DATABASE_URL}
server:
  port: ${PORT:8080}
logging:
  level:
    ROOT: INFO
    com.sgaraba: INFO
```

---

## 3. Seguridad en Aplicaciones

### 3.1 OWASP Top 10 - 2021

#### Checklist de Seguridad Obligatorio

| Vulnerabilidad | Prevención | Implementación |
|----------------|------------|----------------|
| **A1: Control de Acceso** | Validar permisos en backend | ✓ Spring Security + JWT |
| **A2: Fallos Criptográficos** | HTTPS, hashing seguro | ✓ BCrypt para passwords, TLS |
| **A3: Inyección** | Queries parametrizadas | ✓ JPA/Hibernate, PreparedStatements |
| **A4: Diseño Inseguro** | Modelado de amenazas | ✓ Security by Design |
| **A5: Configuración Incorrecta** | Hardening, sin defaults | ✓ Checkstyle, Sonar |
| **A6: Componentes Vulnerables** | Actualizar dependencias | ✓ `mvn versions:display-dependency-updates` |
| **A7: Fallas de Autenticación** | MFA, políticas de password | ✓ Spring Security configurado |
| **A8: Integridad de Datos** | Firmas digitales, CORS | ✓ CORS configurado en Spring |
| **A9: Logs y Monitoreo** | Logs centralizados | ✓ ELK Stack |
| **A10: SSRF** | Validar URLs remotas | ✓ Validación de inputs |

### 3.2 Implementación de Seguridad

#### 3.2.1 Autenticación JWT

```java
// SecurityConfiguration.java
@EnableWebSecurity
public class SecurityConfiguration {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/authenticate").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/**").authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

#### 3.2.2 Validación de Inputs

```java
// Siempre validar con Bean Validation
@NotNull
@Size(min = 3, max = 50)
@Pattern(regexp = "^[a-zA-Z0-9_-]*$")
private String username;

// En el controller
@PostMapping("/users")
public ResponseEntity<User> createUser(@Valid @RequestBody UserDTO userDTO) {
    // ...
}
```

#### 3.2.3 Prevención de Inyección SQL

```java
// ✓ CORRECTO - Usando JPA
@Query("SELECT u FROM User u WHERE u.email = :email")
Optional<User> findByEmail(@Param("email") String email);

// ✗ INCORRECTO - Concatenación de strings
// String query = "SELECT * FROM user WHERE email = '" + email + "'";
```

### 3.3 Headers de Seguridad

```java
// Configure security headers
http.headers(headers -> headers
    .frameOptions(frame -> frame.sameOrigin())
    .contentSecurityPolicy(csp -> 
        csp.policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline'")
    )
    .xssProtection(xss -> xss.enable())
);
```

### 3.4 Testing de Seguridad con ZAP

```bash
# Ejecutar ZAP en modo daemon
docker run -u zap -p 8090:8090 -i owasp/zap2docker-stable zap.sh \
  -daemon -host 0.0.0.0 -port 8090 \
  -config api.disablekey=true

# Escanear la aplicación
curl "http://localhost:8090/JSON/ascan/action/scan/?url=http://localhost:8080"
```

**Verificaciones Regulares:**
- ✓ Escaneo semanal con OWASP ZAP
- ✓ Revisión mensual de dependencias vulnerables
- ✓ Auditoría trimestral de seguridad

---

## 4. Pruebas Automáticas

### 4.1 Pirámide de Testing

```
        /\
       /E2E\          ← 10% Tests End-to-End (Cypress)
      /______\
     /        \
    /Integration\     ← 20% Tests de Integración (Spring Boot Test)
   /____________\
  /              \
 /   Unit Tests   \   ← 70% Tests Unitarios (JUnit + Jest)
/________________  \
```

### 4.2 Tests Unitarios

#### 4.2.1 Backend - JUnit 5

```java
@SpringBootTest
class UserServiceTest {
    
    @Autowired
    private UserService userService;
    
    @MockBean
    private UserRepository userRepository;
    
    @Test
    @DisplayName("Debe crear usuario con email válido")
    void shouldCreateUserWithValidEmail() {
        // Given
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        
        // When
        User user = userService.createUser(userDTO);
        
        // Then
        assertThat(user.getEmail()).isEqualTo("test@example.com");
    }
    
    @Test
    @DisplayName("Debe lanzar excepción con email inválido")
    void shouldThrowExceptionWithInvalidEmail() {
        // Given
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("invalid-email");
        
        // When & Then
        assertThrows(InvalidEmailException.class, () -> {
            userService.createUser(userDTO);
        });
    }
}
```

**Ejecutar:**
```bash
./mvnw test
```

#### 4.2.2 Frontend - Jest

```typescript
// user.service.spec.ts
describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UserService]
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should retrieve user by id', () => {
    const mockUser = { id: 1, name: 'Test User' };
    
    service.getUser(1).subscribe(user => {
      expect(user).toEqual(mockUser);
    });
    
    const req = httpMock.expectOne('/api/users/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockUser);
  });

  afterEach(() => {
    httpMock.verify();
  });
});
```

**Ejecutar:**
```bash
npm test
```

### 4.3 Tests de Integración

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class BookResourceIT {
    
    @Autowired
    private MockMvc restMockMvc;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Test
    @Transactional
    void createBook() throws Exception {
        int databaseSizeBeforeCreate = bookRepository.findAll().size();
        
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("New Book");
        
        restMockMvc.perform(post("/api/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(bookDTO)))
            .andExpect(status().isCreated());
        
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeCreate + 1);
    }
}
```

### 4.4 Tests E2E con Cypress

#### 4.4.1 Configuración Cypress

**cypress.config.ts** (ya existente en tu proyecto):
```typescript
export default defineConfig({
  video: false,
  retries: 2,
  viewportWidth: 1200,
  viewportHeight: 720,
  env: {
    authenticationUrl: '/api/authenticate',
    jwtStorageName: 'jhi-authenticationToken',
  },
  e2e: {
    baseUrl: 'http://localhost:8080/',
    specPattern: 'src/test/javascript/cypress/e2e/**/*.cy.ts',
  },
});
```

#### 4.4.2 Test E2E de Login

```typescript
// cypress/e2e/login.cy.ts
describe('Login', () => {
  beforeEach(() => {
    cy.visit('/');
  });

  it('should display login page', () => {
    cy.get('#login').should('be.visible');
  });

  it('should login with valid credentials', () => {
    cy.get('#username').type('admin');
    cy.get('#password').type('admin');
    cy.get('[type="submit"]').click();
    
    cy.url().should('include', '/');
    cy.get('.navbar-nav').should('contain', 'admin');
  });

  it('should fail with invalid credentials', () => {
    cy.get('#username').type('invalid');
    cy.get('#password').type('wrong');
    cy.get('[type="submit"]').click();
    
    cy.get('.alert-danger').should('be.visible');
  });
});
```

#### 4.4.3 Custom Commands

```typescript
// cypress/support/commands.ts
Cypress.Commands.add('login', (username: string, password: string) => {
  cy.request({
    method: 'POST',
    url: '/api/authenticate',
    body: { username, password }
  }).then(response => {
    window.localStorage.setItem('jhi-authenticationToken', response.body.id_token);
  });
});

// Uso en tests
cy.login('admin', 'admin');
cy.visit('/books');
```

#### 4.4.4 Test CRUD Completo

```typescript
// cypress/e2e/book.cy.ts
describe('Book CRUD', () => {
  beforeEach(() => {
    cy.login('admin', 'admin');
  });

  it('should create a new book', () => {
    cy.visit('/book');
    cy.get('[data-cy="entityCreateButton"]').click();
    
    cy.get('#book-title').type('Test Book');
    cy.get('#book-isbn').type('1234567890');
    cy.get('[data-cy="entityCreateSaveButton"]').click();
    
    cy.get('.alert-success').should('be.visible');
    cy.get('table').should('contain', 'Test Book');
  });

  it('should edit an existing book', () => {
    cy.visit('/book');
    cy.get('[data-cy="entityEditButton"]').first().click();
    
    cy.get('#book-title').clear().type('Updated Book');
    cy.get('[data-cy="entityCreateSaveButton"]').click();
    
    cy.get('table').should('contain', 'Updated Book');
  });

  it('should delete a book', () => {
    cy.visit('/book');
    cy.get('[data-cy="entityDeleteButton"]').first().click();
    cy.get('[data-cy="entityConfirmDeleteButton"]').click();
    
    cy.get('.alert-success').should('be.visible');
  });
});
```

### 4.5 Ejecutar Tests

```bash
# Tests unitarios backend
./mvnw test

# Tests unitarios frontend
npm test

# Tests E2E - headed (con interfaz)
npm run e2e

# Tests E2E - headless (sin interfaz)
npm run e2e:headless

# Todos los tests
npm run ci:frontend:test
npm run ci:backend:test
```

### 4.6 Cobertura de Código

**Objetivo:** Mínimo 80% de cobertura

```bash
# Backend - Genera reporte en target/site/jacoco/
./mvnw verify

# Frontend - Genera reporte en coverage/
npm test -- --coverage
```

**Configurar umbral en pom.xml:**
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <configuration>
        <rules>
            <rule>
                <element>BUNDLE</element>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.80</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</plugin>
```

### 4.7 Test-Driven Development (TDD)

**Ciclo Red-Green-Refactor:**

```java
// 1. RED - Escribir test que falla
@Test
void shouldCalculateBookPrice() {
    Book book = new Book();
    book.setBasePrice(100.0);
    book.setDiscount(0.2);
    
    assertThat(book.getFinalPrice()).isEqualTo(80.0);
}

// 2. GREEN - Implementar código mínimo
public Double getFinalPrice() {
    return basePrice * (1 - discount);
}

// 3. REFACTOR - Mejorar código
public Double getFinalPrice() {
    if (basePrice == null || discount == null) {
        throw new IllegalStateException("Price or discount not set");
    }
    return BigDecimal.valueOf(basePrice)
        .multiply(BigDecimal.ONE.subtract(BigDecimal.valueOf(discount)))
        .doubleValue();
}
```

---

## 5. Gestión de Logs

### 5.1 Arquitectura ELK Stack

```
Application (Logback) 
    ↓
FileBeat (Collector)
    ↓
Logstash (Transform)
    ↓
Elasticsearch (Store & Index)
    ↓
Kibana (Visualize)
```

### 5.2 Configuración Logback

**src/main/resources/logback-spring.xml**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    
    <!-- Console Appender -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- File Appender -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/application.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxHistory>30</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
            <timeBasedFileNamingAndTriggeringPolicy 
                class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>10MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
    </appender>
    
    <!-- JSON Format for ELK -->
    <appender name="LOGSTASH" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/application-logstash.json</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/application-logstash.%d{yyyy-MM-dd}.json</fileNamePattern>
            <maxHistory>7</maxHistory>
        </rollingPolicy>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <customFields>{"app":"library","environment":"${ENVIRONMENT:-dev}"}</customFields>
        </encoder>
    </appender>
    
    <!-- Loggers -->
    <logger name="com.sgaraba" level="DEBUG"/>
    <logger name="org.springframework" level="INFO"/>
    <logger name="org.hibernate" level="WARN"/>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
        <appender-ref ref="LOGSTASH"/>
    </root>
</configuration>
```

### 5.3 Niveles de Log

| Nivel | Uso | Ejemplo |
|-------|-----|---------|
| **ERROR** | Errores que impiden continuar | `log.error("Failed to connect to database", e)` |
| **WARN** | Advertencias, situación recuperable | `log.warn("Deprecated method used")` |
| **INFO** | Eventos importantes del sistema | `log.info("User {} logged in", username)` |
| **DEBUG** | Información de desarrollo | `log.debug("Entering method with params: {}", params)` |
| **TRACE** | Información muy detallada | `log.trace("Loop iteration {}", i)` |

### 5.4 Mejores Prácticas de Logging

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BookService {
    private static final Logger log = LoggerFactory.getLogger(BookService.class);
    
    public Book createBook(BookDTO bookDTO) {
        log.info("Creating book: {}", bookDTO.getTitle());
        
        try {
            Book book = bookMapper.toEntity(bookDTO);
            book = bookRepository.save(book);
            log.info("Book created successfully with ID: {}", book.getId());
            return book;
        } catch (Exception e) {
            log.error("Error creating book: {}", bookDTO, e);
            throw new BookCreationException("Failed to create book", e);
        }
    }
    
    // ✓ CORRECTO - Parametrizado
    log.info("User {} performed action {}", username, action);
    
    // ✗ INCORRECTO - Concatenación
    // log.info("User " + username + " performed action " + action);
}
```

**Reglas Obligatorias:**
- ✓ Usar placeholders `{}` en lugar de concatenación
- ✓ No logear passwords, tokens, datos sensibles
- ✓ Incluir request ID para tracking distribuido
- ✓ Usar MDC (Mapped Diagnostic Context) para contexto

```java
// Agregar Request ID al MDC
@Component
public class RequestIdFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("requestId");
        }
    }
}
```

### 5.5 Docker Compose para ELK Stack

**src/main/docker/elk-stack.yml**
```yaml
version: '3.8'

services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.11.0
    environment:
      - discovery.type=single-node
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
      - xpack.security.enabled=false
    ports:
      - "9200:9200"
    volumes:
      - es_data:/usr/share/elasticsearch/data

  logstash:
    image: docker.elastic.co/logstash/logstash:8.11.0
    volumes:
      - ./logstash/pipeline:/usr/share/logstash/pipeline
      - ./logstash/config/logstash.yml:/usr/share/logstash/config/logstash.yml
    ports:
      - "5000:5000"
    depends_on:
      - elasticsearch

  kibana:
    image: docker.elastic.co/kibana/kibana:8.11.0
    ports:
      - "5601:5601"
    environment:
      - ELASTICSEARCH_HOSTS=http://elasticsearch:9200
    depends_on:
      - elasticsearch

  filebeat:
    image: docker.elastic.co/beats/filebeat:8.11.0
    volumes:
      - ./filebeat/filebeat.yml:/usr/share/filebeat/filebeat.yml
      - ../../logs:/logs
    depends_on:
      - logstash

volumes:
  es_data:
```

**logstash/pipeline/logstash.conf**
```conf
input {
  beats {
    port => 5000
  }
}

filter {
  if [message] =~ /^\{/ {
    json {
      source => "message"
    }
  }
  
  date {
    match => [ "timestamp", "ISO8601" ]
    target => "@timestamp"
  }
}

output {
  elasticsearch {
    hosts => ["elasticsearch:9200"]
    index => "library-logs-%{+YYYY.MM.dd}"
  }
}
```

**Iniciar ELK Stack:**
```bash
docker compose -f src/main/docker/elk-stack.yml up -d

# Acceder a Kibana
open http://localhost:5601
```

### 5.6 Monitoreo y Alertas

**Crear alertas en Kibana:**
1. Acceder a Stack Management → Rules
2. Crear alerta para errores críticos:
   - Condición: `level:ERROR` más de 10 veces en 5 minutos
   - Acción: Enviar email o Slack notification

---

## 6. Integración Continua (CI/CD)

### 6.1 Pipeline Completo

```
┌─────────────┐
│   Commit    │
└──────┬──────┘
       ↓
┌─────────────┐
│   Build     │ ← Compile código
└──────┬──────┘
       ↓
┌─────────────┐
│ Unit Tests  │ ← JUnit + Jest
└──────┬──────┘
       ↓
┌─────────────┐
│ Code Quality│ ← SonarQube
└──────┬──────┘
       ↓
┌─────────────┐
│Integration  │ ← Spring Boot Tests
│   Tests     │
└──────┬──────┘
       ↓
┌─────────────┐
│  E2E Tests  │ ← Cypress
└──────┬──────┘
       ↓
┌─────────────┐
│   Package   │ ← Docker Image
└──────┬──────┘
       ↓
┌─────────────┐
│   Deploy    │ ← Dev/Staging/Prod
└─────────────┘
```

### 6.2 GitHub Actions Workflow

**.github/workflows/ci.yml**
```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - name: Checkout Code
      uses: actions/checkout@v3
      
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        
    - name: Cache Maven packages
      uses: actions/cache@v3
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
        
    - name: Build with Maven
      run: ./mvnw clean install -DskipTests
      
  test-backend:
    needs: build
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    - uses: actions/setup-java@v3
      with:
        java-version: '17'
        
    - name: Run Backend Tests
      run: ./mvnw test
      
    - name: Generate Coverage Report
      run: ./mvnw jacoco:report
      
    - name: Upload Coverage to Codecov
      uses: codecov/codecov-action@v3
      with:
        files: ./target/site/jacoco/jacoco.xml
        
  test-frontend:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    - uses: actions/setup-node@v3
      with:
        node-version: '18'
        
    - name: Install Dependencies
      run: npm ci
      
    - name: Run Frontend Tests
      run: npm test -- --coverage
      
  code-quality:
    needs: [test-backend, test-frontend]
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
      with:
        fetch-depth: 0  # Shallow clones should be disabled
        
    - name: SonarQube Scan
      uses: sonarsource/sonarqube-scan-action@master
      env:
        SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
        SONAR_HOST_URL: ${{ secrets.SONAR_HOST_URL }}
        
  e2e-tests:
    needs: code-quality
    runs-on: ubuntu-latest
    
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_DB: library_test
          POSTGRES_USER: library
          POSTGRES_PASSWORD: library
        ports:
          - 5432:5432
          
    steps:
    - uses: actions/checkout@v3
    - uses: actions/setup-java@v3
      with:
        java-version: '17'
    - uses: actions/setup-node@v3
      with:
        node-version: '18'
        
    - name: Start Application
      run: |
        ./mvnw -Pprod package -DskipTests
        java -jar target/*.jar &
        sleep 30
        
    - name: Run Cypress Tests
      uses: cypress-io/github-action@v5
      with:
        wait-on: 'http://localhost:8080'
        wait-on-timeout: 120
        
    - name: Upload Cypress Videos
      uses: actions/upload-artifact@v3
      if: failure()
      with:
        name: cypress-videos
        path: target/cypress/videos
        
  docker-build:
    needs: e2e-tests
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Build Docker Image
      run: ./mvnw -Pprod jib:dockerBuild
      
    - name: Login to Docker Hub
      uses: docker/login-action@v2
      with:
        username: ${{ secrets.DOCKER_USERNAME }}
        password: ${{ secrets.DOCKER_PASSWORD }}
        
    - name: Push to Docker Hub
      run: docker push yourusername/library:latest
      
  deploy:
    needs: docker-build
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    
    steps:
    - name: Deploy to Production
      run: |
        # SSH to server and pull new image
        ssh ${{ secrets.SSH_USER }}@${{ secrets.SSH_HOST }} \
          'docker pull yourusername/library:latest && \
           docker-compose -f /opt/library/docker-compose.yml up -d'
```

### 6.3 GitLab CI/CD

**.gitlab-ci.yml**
```yaml
stages:
  - build
  - test
  - quality
  - e2e
  - package
  - deploy

variables:
  MAVEN_OPTS: "-Dmaven.repo.local=$CI_PROJECT_DIR/.m2/repository"

cache:
  paths:
    - .m2/repository
    - node_modules/

build:
  stage: build
  image: maven:3.9-eclipse-temurin-17
  script:
    - ./mvnw clean install -DskipTests
  artifacts:
    paths:
      - target/

test:backend:
  stage: test
  image: maven:3.9-eclipse-temurin-17
  script:
    - ./mvnw test
  coverage: '/Total.*?([0-9]{1,3})%/'
  artifacts:
    reports:
      junit: target/surefire-reports/TEST-*.xml
      coverage_report:
        coverage_format: jacoco
        path: target/site/jacoco/jacoco.xml

test:frontend:
  stage: test
  image: node:18
  script:
    - npm ci
    - npm test -- --coverage
  coverage: '/Statements\s*:\s*([0-9.]+)%/'

sonarqube:
  stage: quality
  image: maven:3.9-eclipse-temurin-17
  script:
    - ./mvnw sonar:sonar 
      -Dsonar.projectKey=library 
      -Dsonar.host.url=$SONAR_HOST_URL 
      -Dsonar.login=$SONAR_TOKEN
  only:
    - main
    - develop

e2e:
  stage: e2e
  image: cypress/browsers:latest
  services:
    - postgres:15
  variables:
    POSTGRES_DB: library_test
    POSTGRES_USER: library
    POSTGRES_PASSWORD: library
  script:
    - npm ci
    - ./mvnw -Pprod package -DskipTests
    - java -jar target/*.jar &
    - npx wait-on http://localhost:8080
    - npm run e2e:headless
  artifacts:
    when: on_failure
    paths:
      - target/cypress/videos
      - target/cypress/screenshots

package:
  stage: package
  image: maven:3.9-eclipse-temurin-17
  script:
    - ./mvnw -Pprod jib:build
  only:
    - main

deploy:production:
  stage: deploy
  image: alpine:latest
  before_script:
    - apk add --no-cache openssh-client
    - eval $(ssh-agent -s)
    - echo "$SSH_PRIVATE_KEY" | tr -d '\r' | ssh-add -
  script:
    - ssh -o StrictHostKeyChecking=no $DEPLOY_USER@$DEPLOY_HOST 
      'cd /opt/library && docker-compose pull && docker-compose up -d'
  only:
    - main
  when: manual
```

### 6.4 Jenkins Pipeline

**Jenkinsfile**
```groovy
pipeline {
    agent any
    
    tools {
        maven 'Maven 3.9'
        jdk 'JDK 17'
        nodejs 'Node 18'
    }
    
    environment {
        SONAR_TOKEN = credentials('sonar-token')
        DOCKER_CREDENTIALS = credentials('docker-hub')
    }
    
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/yourrepo/library.git'
            }
        }
        
        stage('Build') {
            steps {
                sh './mvnw clean install -DskipTests'
            }
        }
        
        stage('Unit Tests') {
            parallel {
                stage('Backend Tests') {
                    steps {
                        sh './mvnw test'
                    }
                    post {
                        always {
                            junit 'target/surefire-reports/**/*.xml'
                            jacoco execPattern: 'target/jacoco.exec'
                        }
                    }
                }
                stage('Frontend Tests') {
                    steps {
                        sh 'npm ci'
                        sh 'npm test'
                    }
                }
            }
        }
        
        stage('Code Quality') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh './mvnw sonar:sonar'
                }
            }
        }
        
        stage('Quality Gate') {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        
        stage('E2E Tests') {
            steps {
                sh 'docker-compose -f src/main/docker/postgresql.yml up -d'
                sh 'npm run ci:e2e:run'
            }
            post {
                always {
                    sh 'docker-compose -f src/main/docker/postgresql.yml down'
                }
            }
        }
        
        stage('Build Docker Image') {
            when {
                branch 'main'
            }
            steps {
                sh './mvnw -Pprod jib:dockerBuild'
            }
        }
        
        stage('Push to Registry') {
            when {
                branch 'main'
            }
            steps {
                sh "echo $DOCKER_CREDENTIALS_PSW | docker login -u $DOCKER_CREDENTIALS_USR --password-stdin"
                sh 'docker push yourusername/library:latest'
            }
        }
        
        stage('Deploy') {
            when {
                branch 'main'
            }
            steps {
                input message: 'Deploy to production?', ok: 'Deploy'
                sshagent(['ssh-credentials']) {
                    sh '''
                        ssh user@production-server "
                            cd /opt/library &&
                            docker-compose pull &&
                            docker-compose up -d
                        "
                    '''
                }
            }
        }
    }
    
    post {
        success {
            slackSend color: 'good', message: "Build Successful: ${env.JOB_NAME} ${env.BUILD_NUMBER}"
        }
        failure {
            slackSend color: 'danger', message: "Build Failed: ${env.JOB_NAME} ${env.BUILD_NUMBER}"
        }
        always {
            cleanWs()
        }
    }
}
```

### 6.5 SonarQube Configuration

**sonar-project.properties**
```properties
sonar.projectKey=library
sonar.projectName=Library Application
sonar.projectVersion=1.0

# Source directories
sonar.sources=src/main/java,src/main/webapp
sonar.tests=src/test/java,src/test/javascript

# Exclusions
sonar.exclusions=**/node_modules/**,**/target/**,**/*.spec.ts,**/*.cy.ts

# Java
sonar.java.binaries=target/classes
sonar.java.source=17
sonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml

# JavaScript/TypeScript
sonar.typescript.lcov.reportPaths=coverage/lcov.info

# Quality gates
sonar.qualitygate.wait=true
sonar.qualitygate.timeout=300

# Code smells
sonar.issue.ignore.multicriteria=e1,e2

# Ignore generated code
sonar.issue.ignore.multicriteria.e1.ruleKey=java:S1118
sonar.issue.ignore.multicriteria.e1.resourceKey=**/config/Constants.java

sonar.issue.ignore.multicriteria.e2.ruleKey=typescript:S1186
sonar.issue.ignore.multicriteria.e2.resourceKey=**/*.spec.ts
```

**Quality Gates (mínimos):**
- ✓ Coverage > 80%
- ✓ Duplications < 3%
- ✓ Maintainability Rating = A
- ✓ Reliability Rating = A
- ✓ Security Rating = A
- ✓ Security Hotspots Reviewed = 100%

---

## 7. Progressive Web App (PWA)

### 7.1 Checklist PWA Básica

| Requisito | Estado | Implementación |
|-----------|--------|----------------|
| ✓ Inicia rápido | ✓ | Service Worker + Cache |
| ✓ Funciona en cualquier navegador | ✓ | Progressive Enhancement |
| ✓ Responsive | ✓ | CSS Grid + Flexbox |
| ✓ Página sin conexión | ✓ | Offline fallback |
| ✓ Es instalable | ✓ | manifest.webapp |
| ✓ HTTPS | ✓ | TLS configurado |

### 7.2 Web App Manifest

**src/main/webapp/manifest.webapp**
```json
{
  "name": "Library Management System",
  "short_name": "Library",
  "description": "Sistema de gestión de biblioteca con PWA",
  "start_url": "/",
  "display": "standalone",
  "background_color": "#ffffff",
  "theme_color": "#3f51b5",
  "orientation": "portrait-primary",
  "icons": [
    {
      "src": "content/images/icon-72x72.png",
      "sizes": "72x72",
      "type": "image/png",
      "purpose": "any maskable"
    },
    {
      "src": "content/images/icon-96x96.png",
      "sizes": "96x96",
      "type": "image/png"
    },
    {
      "src": "content/images/icon-128x128.png",
      "sizes": "128x128",
      "type": "image/png"
    },
    {
      "src": "content/images/icon-144x144.png",
      "sizes": "144x144",
      "type": "image/png"
    },
    {
      "src": "content/images/icon-152x152.png",
      "sizes": "152x152",
      "type": "image/png"
    },
    {
      "src": "content/images/icon-192x192.png",
      "sizes": "192x192",
      "type": "image/png"
    },
    {
      "src": "content/images/icon-384x384.png",
      "sizes": "384x384",
      "type": "image/png"
    },
    {
      "src": "content/images/icon-512x512.png",
      "sizes": "512x512",
      "type": "image/png"
    }
  ],
  "shortcuts": [
    {
      "name": "Buscar libros",
      "short_name": "Buscar",
      "url": "/book?search",
      "icons": [{ "src": "content/images/search-icon.png", "sizes": "192x192" }]
    },
    {
      "name": "Agregar libro",
      "short_name": "Nuevo",
      "url": "/book/new",
      "icons": [{ "src": "content/images/add-icon.png", "sizes": "192x192" }]
    }
  ],
  "categories": ["books", "education", "productivity"],
  "screenshots": [
    {
      "src": "content/images/screenshot1.png",
      "sizes": "540x720",
      "type": "image/png"
    }
  ]
}
```

### 7.3 Service Worker

**ngsw-config.json**
```json
{
  "$schema": "./node_modules/@angular/service-worker/config/schema.json",
  "index": "/index.html",
  "assetGroups": [
    {
      "name": "app",
      "installMode": "prefetch",
      "resources": {
        "files": [
          "/favicon.ico",
          "/index.html",
          "/manifest.webapp",
          "/*.css",
          "/*.js"
        ]
      }
    },
    {
      "name": "assets",
      "installMode": "lazy",
      "updateMode": "prefetch",
      "resources": {
        "files": [
          "/content/**",
          "/*.(svg|cur|jpg|jpeg|png|apng|webp|avif|gif|otf|ttf|woff|woff2)"
        ]
      }
    }
  ],
  "dataGroups": [
    {
      "name": "api-cache",
      "urls": [
        "/api/books",
        "/api/authors"
      ],
      "cacheConfig": {
        "strategy": "freshness",
        "maxSize": 100,
        "maxAge": "1h",
        "timeout": "5s"
      }
    },
    {
      "name": "api-performance",
      "urls": [
        "/api/account"
      ],
      "cacheConfig": {
        "strategy": "performance",
        "maxSize": 10,
        "maxAge": "12h"
      }
    }
  ],
  "navigationUrls": [
    "/**",
    "!/**/*.*",
    "!/**/*__*",
    "!/**/*__*/**"
  ]
}
```

### 7.4 Estrategias de Caché

#### 7.4.1 Cache First (App Shell)

```typescript
// Para recursos estáticos que rara vez cambian
{
  "name": "app-shell",
  "installMode": "prefetch",
  "updateMode": "prefetch",
  "resources": {
    "files": [
      "/index.html",
      "/main.*.js",
      "/polyfills.*.js",
      "/styles.*.css"
    ]
  }
}
```

#### 7.4.2 Network First (Datos dinámicos)

```typescript
{
  "name": "api-fresh",
  "urls": ["/api/**"],
  "cacheConfig": {
    "strategy": "freshness",  // Network First
    "maxSize": 100,
    "maxAge": "1h",
    "timeout": "5s"  // Si la red no responde en 5s, usa caché
  }
}
```

#### 7.4.3 Stale While Revalidate

```typescript
// Implementación custom en service-worker.js
self.addEventListener('fetch', event => {
  if (event.request.url.includes('/api/books')) {
    event.respondWith(
      caches.open('books-cache').then(cache => {
        return cache.match(event.request).then(cachedResponse => {
          const fetchPromise = fetch(event.request).then(networkResponse => {
            cache.put(event.request, networkResponse.clone());
            return networkResponse;
          });
          return cachedResponse || fetchPromise;
        });
      })
    );
  }
});
```

### 7.5 Offline Fallback

**src/main/webapp/offline.html**
```html
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="utf-8">
  <title>Sin conexión - Library</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <style>
    body {
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
      display: flex;
      align-items: center;
      justify-content: center;
      height: 100vh;
      margin: 0;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      text-align: center;
    }
    .container {
      max-width: 500px;
      padding: 2rem;
    }
    h1 {
      font-size: 3rem;
      margin: 0 0 1rem;
    }
    p {
      font-size: 1.2rem;
      margin-bottom: 2rem;
    }
    .icon {
      font-size: 5rem;
      margin-bottom: 1rem;
    }
  </style>
</head>
<body>
  <div class="container">
    <div class="icon">📡</div>
    <h1>Sin Conexión</h1>
    <p>No tienes conexión a internet. Algunas funciones pueden estar limitadas.</p>
    <button onclick="location.reload()" 
            style="padding: 1rem 2rem; font-size: 1rem; cursor: pointer; 
                   border: none; background: white; color: #667eea; 
                   border-radius: 5px; font-weight: bold;">
      Intentar de nuevo
    </button>
  </div>
</body>
</html>
```

### 7.6 Habilitar PWA en Angular

**app.module.ts**
```typescript
import { ServiceWorkerModule } from '@angular/service-worker';
import { environment } from '../environments/environment';

@NgModule({
  imports: [
    // ...
    ServiceWorkerModule.register('ngsw-worker.js', {
      enabled: environment.production,
      registrationStrategy: 'registerWhenStable:30000'
    })
  ]
})
export class AppModule { }
```

### 7.7 Actualización de Service Worker

**app.component.ts**
```typescript
import { SwUpdate, VersionReadyEvent } from '@angular/service-worker';
import { filter } from 'rxjs/operators';

export class AppComponent implements OnInit {
  constructor(private swUpdate: SwUpdate) {}

  ngOnInit() {
    if (this.swUpdate.isEnabled) {
      this.swUpdate.versionUpdates
        .pipe(filter((evt): evt is VersionReadyEvent => evt.type === 'VERSION_READY'))
        .subscribe(() => {
          if (confirm('Nueva versión disponible. ¿Actualizar ahora?')) {
            window.location.reload();
          }
        });
    }
  }
}
```

### 7.8 Testing PWA

```bash
# Lighthouse CI
npm install -g @lhci/cli

# Crear configuración
# lighthouserc.js
module.exports = {
  ci: {
    collect: {
      url: ['http://localhost:8080/'],
      numberOfRuns: 3
    },
    assert: {
      preset: 'lighthouse:recommended',
      assertions: {
        'categories:performance': ['error', {minScore: 0.8}],
        'categories:accessibility': ['error', {minScore: 0.9}],
        'categories:best-practices': ['error', {minScore: 0.9}],
        'categories:seo': ['error', {minScore: 0.9}],
        'categories:pwa': ['error', {minScore: 0.9}]
      }
    }
  }
};

# Ejecutar
lhci autorun
```

**Herramientas de Auditoría:**
- ✓ Chrome DevTools > Lighthouse
- ✓ PWA Builder: https://www.pwabuilder.com/
- ✓ Workbox Wizard para optimizar strategies

### 7.9 Optimización de Rendimiento

**angular.json - Production Build**
```json
{
  "configurations": {
    "production": {
      "optimization": true,
      "outputHashing": "all",
      "sourceMap": false,
      "namedChunks": false,
      "aot": true,
      "extractLicenses": true,
      "buildOptimizer": true,
      "budgets": [
        {
          "type": "initial",
          "maximumWarning": "500kb",
          "maximumError": "1mb"
        },
        {
          "type": "anyComponentStyle",
          "maximumWarning": "2kb",
          "maximumError": "4kb"
        }
      ],
      "serviceWorker": true,
      "ngswConfigPath": "ngsw-config.json"
    }
  }
}
```

**Lazy Loading de Módulos:**
```typescript
const routes: Routes = [
  {
    path: 'books',
    loadChildren: () => import('./books/books.module').then(m => m.BooksModule)
  },
  {
    path: 'admin',
    loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule),
    canLoad: [AuthGuard]
  }
];
```

---

## 8. Arquitectura y Componentes

### 8.1 Diagrama de Arquitectura

```
┌──────────────────────────────────────────────────┐
│                    CDN / Nginx                    │
│              (Static Assets + SSL)                │
└────────────────────┬─────────────────────────────┘
                     │
┌────────────────────┴─────────────────────────────┐
│             Angular Frontend (PWA)                │
│  ┌─────────────┐  ┌───────────┐  ┌────────────┐ │
│  │  Components │  │ Services  │  │   Guards   │ │
│  └─────────────┘  └───────────┘  └────────────┘ │
│  ┌─────────────────────────────────────────────┐ │
│  │         Service Worker (Offline)            │ │
│  └─────────────────────────────────────────────┘ │
└────────────────────┬─────────────────────────────┘
                     │ HTTP/REST
┌────────────────────┴─────────────────────────────┐
│            Spring Boot Backend                    │
│  ┌──────────┐  ┌──────────┐  ┌─────────────────┐│
│  │Controllers│  │ Services │  │  Repositories   ││
│  └──────────┘  └──────────┘  └─────────────────┘│
│  ┌──────────────────────────────────────────────┐│
│  │         Spring Security (JWT)                ││
│  └──────────────────────────────────────────────┘│
└────────────────────┬─────────────────────────────┘
                     │
         ┌───────────┴──────────┬─────────────────┐
         │                      │                 │
┌────────┴────────┐  ┌──────────┴────┐  ┌────────┴────┐
│   PostgreSQL    │  │   Hazelcast   │  │     ELK     │
│   (Database)    │  │    (Cache)    │  │   (Logs)    │
└─────────────────┘  └───────────────┘  └─────────────┘
```

### 8.2 Patrones de Diseño Implementados

| Patrón | Uso | Ubicación |
|--------|-----|-----------|
| **MVC** | Separación de capas | Controller → Service → Repository |
| **DTO** | Transferencia de datos | `*.dto.ts` / `*DTO.java` |
| **Repository** | Abstracción de datos | `*Repository.java` |
| **Singleton** | Spring Beans | `@Service`, `@Component` |
| **Observer** | Eventos de dominio | Spring Events |
| **Strategy** | Estrategias de caché | Service Worker strategies |
| **Factory** | Creación de entidades | `*Mapper.java` |

### 8.3 Capas de la Aplicación

```java
// 1. CONTROLLER LAYER - Punto de entrada HTTP
@RestController
@RequestMapping("/api/books")
public class BookResource {
    private final BookService bookService;
    
    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks(Pageable pageable) {
        Page<BookDTO> page = bookService.findAll(pageable);
        return ResponseEntity.ok().body(page.getContent());
    }
}

// 2. SERVICE LAYER - Lógica de negocio
@Service
@Transactional
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    
    public Page<BookDTO> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
            .map(bookMapper::toDto);
    }
}

// 3. REPOSITORY LAYER - Acceso a datos
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("SELECT b FROM Book b WHERE b.author.id = :authorId")
    List<Book> findByAuthorId(@Param("authorId") Long authorId);
}

// 4. DOMAIN LAYER - Entidades
@Entity
@Table(name = "book")
public class Book implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @NotNull
    @Column(nullable = false)
    private String title;
    
    @ManyToOne
    private Author author;
}
```

---

## 9. Checklist de Calidad

### 9.1 Antes de Cada Commit

```bash
# Checklist pre-commit
□ Código formateado (Prettier/Spotless)
□ Linter pasa sin errores
□ Tests unitarios pasan
□ Cobertura > 80%
□ No hay console.log/System.out en código

# Ejecutar
npm run lint:fix
npm run prettier:format
npm test
./mvnw test
```

### 9.2 Antes de Cada Pull Request

```markdown
## Checklist PR

### Código
- [ ] Código revisado y sin código comentado innecesario
- [ ] Nombres de variables/funciones descriptivos
- [ ] Sin hardcoding de valores (usar configuración)
- [ ] Sin duplicación de código (DRY)

### Tests
- [ ] Tests unitarios agregados/actualizados
- [ ] Tests de integración pasan
- [ ] Tests E2E pasan (si aplica)
- [ ] Cobertura de código > 80%

### Seguridad
- [ ] Sin passwords/tokens hardcodeados
- [ ] Inputs validados
- [ ] Sin vulnerabilidades (npm audit / mvn dependency-check)
- [ ] Headers de seguridad configurados

### Documentación
- [ ] README actualizado (si aplica)
- [ ] Comentarios en código complejo
- [ ] API documentada (Swagger)
- [ ] CHANGELOG actualizado

### Performance
- [ ] No hay consultas N+1
- [ ] Lazy loading implementado
- [ ] Imágenes optimizadas
- [ ] Build size dentro de budget

### PWA
- [ ] Service Worker funcionando
- [ ] Funciona offline (páginas críticas)
- [ ] Lighthouse score > 90

### CI/CD
- [ ] Pipeline de CI pasa
- [ ] SonarQube Quality Gate pasa
- [ ] Docker build exitoso
```

### 9.3 Auditoría de Dependencias

```bash
# Backend - Maven
./mvnw versions:display-dependency-updates
./mvnw org.owasp:dependency-check-maven:check

# Frontend - npm
npm audit
npm audit fix

# Actualizar dependencias
npm outdated
npm update
```

### 9.4 Quality Gates

**Métricas Obligatorias:**

| Métrica | Objetivo | Herramienta |
|---------|----------|-------------|
| Code Coverage | > 80% | JaCoCo + Jest |
| Maintainability | A | SonarQube |
| Reliability | A | SonarQube |
| Security | A | SonarQube + OWASP |
| Duplications | < 3% | SonarQube |
| Complexity | < 15 | SonarQube |
| Lighthouse PWA | > 90 | Chrome DevTools |
| Lighthouse Performance | > 80 | Chrome DevTools |
| Build Time | < 5 min | CI/CD |
| E2E Success Rate | 100% | Cypress |

---

## 10. Mantenimiento y Mejora Continua

### 10.1 Calendario de Mantenimiento

| Tarea | Frecuencia | Responsable |
|-------|------------|-------------|
| Actualizar dependencias | Mensual | Dev Team |
| Revisión de seguridad | Semanal | Security Lead |
| Auditoría de logs | Semanal | DevOps |
| Review de performance | Quincenal | Tech Lead |
| Backup de base de datos | Diario | DevOps |
| Disaster recovery test | Trimestral | DevOps |
| Revisión de documentación | Mensual | Dev Team |

### 10.2 Monitoreo en Producción

**Métricas Clave (KPIs):**
- ✓ Uptime > 99.9%
- ✓ Response Time p95 < 500ms
- ✓ Error Rate < 0.1%
- ✓ CPU Usage < 70%
- ✓ Memory Usage < 80%
- ✓ Disk Usage < 75%

**Herramientas:**
```yaml
# docker-compose-monitoring.yml
version: '3.8'

services:
  prometheus:
    image: prom/prometheus:latest
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
    ports:
      - "9090:9090"

  grafana:
    image: grafana/grafana:latest
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
    volumes:
      - grafana_data:/var/lib/grafana

  alertmanager:
    image: prom/alertmanager:latest
    ports:
      - "9093:9093"
    volumes:
      - ./alertmanager.yml:/etc/alertmanager/alertmanager.yml

volumes:
  grafana_data:
```

### 10.3 Proceso de Actualización

```bash
# 1. Crear rama de actualización
git checkout -b update/dependencies-2026-03

# 2. Actualizar backend
./mvnw versions:use-latest-releases
./mvnw clean test

# 3. Actualizar frontend
npx npm-check-updates -u
npm install
npm test

# 4. Ejecutar tests completos
npm run ci:backend:test
npm run ci:frontend:test
npm run e2e:headless

# 5. Verificar seguridad
./mvnw org.owasp:dependency-check-maven:check
npm audit

# 6. Crear PR y ejecutar CI
git commit -m "chore: update dependencies"
git push origin update/dependencies-2026-03
```

### 10.4 Gestión de Incidentes

**Proceso:**
1. **Detección** → Alerta automática (Prometheus/Kibana)
2. **Clasificación** → Severidad (Critical/High/Medium/Low)
3. **Respuesta** → Equipo de guardia notificado
4. **Resolución** → Fix aplicado
5. **Post-mortem** → Documento de aprendizaje

**Plantilla Post-Mortem:**
```markdown
# Post-Mortem: [Título del Incidente]

**Fecha:** 2026-03-07
**Duración:** 15 minutos
**Severidad:** High
**Impacto:** 50 usuarios afectados

## Qué pasó
[Descripción detallada]

## Línea de tiempo
- 10:00 - Primer reporte de error
- 10:05 - Equipo notificado
- 10:10 - Causa raíz identificada
- 10:15 - Fix deployado

## Causa raíz
[Explicación técnica]

## Resolución
[Qué se hizo para arreglar]

## Acciones de mejora
- [ ] Agregar alerta para detectar antes
- [ ] Mejorar documentación de rollback
- [ ] Agregar test E2E para este case
```

### 10.5 Capacitación del Equipo

**Temas Obligatorios:**
- ✓ OWASP Top 10 (anual)
- ✓ Git Flow y mejores prácticas (onboarding)
- ✓ Testing strategies (trimestral)
- ✓ Docker y Kubernetes (semestral)
- ✓ Performance optimization (semestral)

---

## Recursos Adicionales

### Documentación Oficial
- [JHipster](https://www.jhipster.tech/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Angular](https://angular.io/)
- [Cypress](https://www.cypress.io/)
- [OWASP](https://owasp.org/)

### Herramientas Recomendadas
- **IDE:** IntelliJ IDEA / VS Code
- **API Testing:** Postman / Insomnia
- **Database:** DBeaver / pgAdmin
- **Monitoring:** Grafana + Prometheus
- **Logs:** Kibana
- **CI/CD:** GitHub Actions / GitLab CI / Jenkins

### Comandos Rápidos

```bash
# Desarrollo
./mvnw                          # Backend
npm start                       # Frontend
docker-compose -f src/main/docker/postgresql.yml up -d  # Database

# Testing
npm test                        # Unit tests frontend
./mvnw test                     # Unit tests backend
npm run e2e                     # E2E tests

# Production Build
./mvnw -Pprod package           # JAR
./mvnw -Pprod jib:dockerBuild   # Docker image

# Quality
npm run lint
./mvnw checkstyle:check
./mvnw sonar:sonar
```

---

## Conclusión

Este manual debe ser **revisado y actualizado** cada vez que:
- ✓ Se agregue una nueva tecnología
- ✓ Cambien los estándares de la industria
- ✓ Se identifiquen nuevas vulnerabilidades
- ✓ El equipo aprenda nuevas mejores prácticas

**Versión:** 1.0  
**Última actualización:** 2026-03-07  
**Próxima revisión:** 2026-06-07

---

**¿Preguntas o sugerencias?**  
Abrir un issue en el repositorio o contactar al equipo de arquitectura.
