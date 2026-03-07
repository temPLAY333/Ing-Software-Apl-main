# 📚 Documentación del Proyecto - Library Management System

> Sistema de gestión de biblioteca con arquitectura moderna, seguridad OWASP, testing automatizado y PWA

---

## 🎯 Documentación Disponible

Este proyecto cuenta con documentación completa para asegurar la calidad en todas las fases del desarrollo:

### 1. 🚀 [Inicio Rápido](./INICIO-RAPIDO.md)
**Tiempo estimado:** 15 minutos

Configura y ejecuta la aplicación rápidamente. Ideal para:
- Nuevos desarrolladores en el equipo
- Setup inicial del ambiente de desarrollo
- Verificación rápida del proyecto

**Incluye:**
- Pre-requisitos y instalación
- Comandos esenciales
- Solución de problemas comunes
- Primeros pasos

[👉 Ir al Inicio Rápido](./INICIO-RAPIDO.md)

---

### 2. 📖 [Manual de Creación de Aplicación](./MANUAL-CREACION-APLICACION.md)
**Documento de referencia principal**

Manual completo que cubre todos los aspectos para garantizar la calidad de la aplicación:

#### Temas Cubiertos:

- **📋 Principios Fundamentales**
  - 12 Factores para aplicaciones Cloud
  - Principios de desarrollo

- **🔒 Seguridad en Aplicaciones**
  - OWASP Top 10 - 2021
  - Implementación de seguridad JWT
  - Prevención de vulnerabilidades
  - Testing de seguridad con ZAP

- **🧪 Pruebas Automáticas**
  - Tests Unitarios (JUnit + Jest)
  - Tests de Integración
  - Tests E2E con Cypress
  - Test-Driven Development (TDD)
  - Cobertura de código

- **📝 Gestión de Logs**
  - Arquitectura ELK Stack
  - Configuración Logback
  - Mejores prácticas de logging
  - Monitoreo y alertas

- **🔄 Integración Continua (CI/CD)**
  - Pipeline completo
  - GitHub Actions / GitLab CI / Jenkins
  - SonarQube configuration
  - Deploy automatizado

- **📱 Progressive Web App (PWA)**
  - Web App Manifest
  - Service Workers
  - Estrategias de caché
  - Offline support
  - Optimización de rendimiento

- **🏗️ Arquitectura y Componentes**
  - Diagrama de arquitectura
  - Patrones de diseño
  - Capas de la aplicación

- **✅ Checklist de Calidad**
  - Pre-commit
  - Pre-Pull Request
  - Quality Gates
  - Métricas

[👉 Ir al Manual Completo](./MANUAL-CREACION-APLICACION.md)

---

### 3. ✅ [Checklist de Calidad Rápida](./CHECKLIST-CALIDAD-RAPIDA.md)
**Guía de referencia instantánea**

Checklist condensado para revisión diaria. Ideal para:
- Pre-commit checks
- Pull Request reviews
- Pre-deploy verification
- Mantenimiento regular

**Incluye:**
- ✅ Pre-Commit checklist
- ✅ Pre-Pull Request checklist
- ✅ Checklist de Seguridad OWASP
- ✅ Checklist de Testing
- ✅ Checklist de PWA
- ✅ Checklist de CI/CD
- ✅ Pre-Deploy checklist
- ✅ Comandos útiles
- ✅ Pull Request template

[👉 Ir al Checklist Rápido](./CHECKLIST-CALIDAD-RAPIDA.md)

---

### 4. 📄 [Ing. en Software Aplicada](./Ing.%20en%20Software%20Aplicada.md)
**Material teórico de referencia**

Documento original con conceptos teóricos de:
- Seguridad y Calidad
- 12 Factores
- OWASP Top 10
- JHipster
- Cypress y Selenium
- ELK Stack
- PWA
- Service Workers

---

## 🗺️ Flujo de Uso Recomendado

```
┌─────────────────────────────────────────────────────────┐
│  1. Nuevo en el proyecto                                │
│     └─> Leer INICIO-RAPIDO.md                          │
│         └─> Configurar ambiente                         │
│             └─> Ejecutar aplicación                     │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│  2. Desarrollo diario                                   │
│     └─> Usar CHECKLIST-CALIDAD-RAPIDA.md              │
│         └─> Antes de cada commit                        │
│         └─> Antes de cada PR                            │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│  3. Implementar nuevas features                         │
│     └─> Consultar MANUAL-CREACION-APLICACION.md       │
│         └─> Seguridad: Sección 3                        │
│         └─> Testing: Sección 4                          │
│         └─> PWA: Sección 7                              │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│  4. Deploy a producción                                 │
│     └─> Verificar CHECKLIST-CALIDAD-RAPIDA.md         │
│         └─> Pre-Deploy checklist                        │
│         └─> Ejecutar todos los tests                    │
│         └─> Verificar quality gates                     │
└─────────────────────────────────────────────────────────┘
```

---

## 📊 Estándares de Calidad del Proyecto

| Aspecto | Objetivo | Herramienta |
|---------|----------|-------------|
| **Cobertura de Código** | > 80% | JaCoCo + Jest |
| **Seguridad** | OWASP Top 10 | ZAP + SonarQube |
| **Tests E2E** | 100% críticos | Cypress |
| **PWA Score** | > 90 | Lighthouse |
| **Performance** | p95 < 500ms | Prometheus |
| **Uptime** | > 99.9% | Monitoring |
| **Quality Gate** | A | SonarQube |

---

## 🛠️ Stack Tecnológico

### Backend
- **Framework:** Spring Boot 3.x
- **Java:** 17
- **Database:** PostgreSQL 15
- **Build:** Maven 3.9
- **Security:** Spring Security + JWT
- **Cache:** Hazelcast
- **ORM:** Hibernate/JPA
- **Migrations:** Liquibase

### Frontend
- **Framework:** Angular 19
- **Language:** TypeScript
- **UI:** Bootstrap + FontAwesome
- **Build:** Angular CLI
- **PWA:** Angular Service Worker
- **State Management:** RxJS

### Testing
- **Unit (Backend):** JUnit 5 + Mockito
- **Unit (Frontend):** Jest + Jasmine
- **E2E:** Cypress
- **Coverage:** JaCoCo + Istanbul

### DevOps
- **CI/CD:** GitHub Actions / GitLab CI
- **Quality:** SonarQube
- **Containers:** Docker + Docker Compose
- **Monitoring:** Prometheus + Grafana
- **Logs:** ELK Stack (Elasticsearch + Logstash + Kibana)

---

## 🚀 Inicio Rápido (TL;DR)

```bash
# 1. Clonar proyecto
git clone [URL]
cd Ing-Software-Apl/backend

# 2. Iniciar base de datos
docker-compose -f src/main/docker/postgresql.yml up -d

# 3. Instalar dependencias
./mvnw clean install -DskipTests
npm install

# 4. Ejecutar aplicación
./mvnw        # Terminal 1 - Backend
npm start     # Terminal 2 - Frontend

# 5. Abrir en navegador
# http://localhost:9000
# Usuario: admin / Password: admin
```

---

## 📋 Comandos Esenciales

```bash
# Desarrollo
./mvnw                    # Backend
npm start                 # Frontend
npm run watch            # Backend + Frontend

# Testing
./mvnw test              # Unit tests backend
npm test                 # Unit tests frontend
npm run e2e              # E2E tests

# Build producción
./mvnw -Pprod package                # JAR
./mvnw -Pprod jib:dockerBuild       # Docker image

# Calidad
npm run lint:fix         # Fix linting
npm run prettier:format  # Format code
./mvnw sonar:sonar      # SonarQube analysis
```

---

## 🔐 Seguridad

Este proyecto implementa las recomendaciones de **OWASP Top 10 - 2021**:

- ✅ A1: Control de Acceso (Spring Security + JWT)
- ✅ A2: Fallos Criptográficos (BCrypt + HTTPS)
- ✅ A3: Inyección (Queries parametrizadas)
- ✅ A4: Diseño Inseguro (Security by Design)
- ✅ A5: Configuración Incorrecta (Hardening)
- ✅ A6: Componentes Vulnerables (Auditoría regular)
- ✅ A7: Fallas de Autenticación (JWT + Políticas)
- ✅ A8: Integridad de Datos (CORS + Validación)
- ✅ A9: Logs y Monitoreo (ELK Stack)
- ✅ A10: SSRF (Validación de URLs)

Ver [Manual de Seguridad](./MANUAL-CREACION-APLICACION.md#3-seguridad-en-aplicaciones) para detalles.

---

## 🧪 Testing

### Cobertura objetivo: > 80%

```bash
# Backend
./mvnw test
./mvnw jacoco:report
# Reporte: target/site/jacoco/index.html

# Frontend
npm test -- --coverage
# Reporte: coverage/index.html

# E2E
npm run e2e:headless
# Videos: target/cypress/videos
```

Ver [Manual de Testing](./MANUAL-CREACION-APLICACION.md#4-pruebas-automáticas) para detalles.

---

## 📱 Progressive Web App

La aplicación funciona como PWA con:
- ✅ Instalable en dispositivos
- ✅ Funciona offline
- ✅ Service Worker configurado
- ✅ Caché estratégica
- ✅ Manifest configurado
- ✅ Responsive design

Ver [Manual de PWA](./MANUAL-CREACION-APLICACION.md#7-progressive-web-app-pwa) para detalles.

---

## 🔄 CI/CD Pipeline

```
Commit → Build → Unit Tests → Code Quality → Integration Tests → E2E Tests → Package → Deploy
```

- **GitHub Actions:** `.github/workflows/ci.yml`
- **SonarQube:** Quality Gate con métricas estrictas
- **Docker:** Build y push automático

Ver [Manual de CI/CD](./MANUAL-CREACION-APLICACION.md#6-integración-continua-cicd) para detalles.

---

## 📝 Logs y Monitoreo

### ELK Stack configurado

```bash
# Iniciar ELK
docker-compose -f src/main/docker/elk-stack.yml up -d

# Acceder a Kibana
open http://localhost:5601

# Ver logs
tail -f logs/application.log
```

Ver [Manual de Logs](./MANUAL-CREACION-APLICACION.md#5-gestión-de-logs) para detalles.

---

## 👥 Para Nuevos Desarrolladores

1. **Día 1:** Lee [INICIO-RAPIDO.md](./INICIO-RAPIDO.md)
   - Configura tu ambiente
   - Ejecuta la aplicación
   - Explora el código

2. **Semana 1:** Lee [MANUAL-CREACION-APLICACION.md](./MANUAL-CREACION-APLICACION.md)
   - Comprende la arquitectura
   - Aprende las mejores prácticas
   - Conoce las herramientas

3. **Uso diario:** Usa [CHECKLIST-CALIDAD-RAPIDA.md](./CHECKLIST-CALIDAD-RAPIDA.md)
   - Antes de cada commit
   - Antes de cada PR
   - Pre-deploy

---

## 🆘 Soporte

### Documentación
- [Manual Completo](./MANUAL-CREACION-APLICACION.md)
- [Checklist Rápido](./CHECKLIST-CALIDAD-RAPIDA.md)
- [Inicio Rápido](./INICIO-RAPIDO.md)

### Recursos Externos
- [JHipster Docs](https://www.jhipster.tech/)
- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [Angular Docs](https://angular.io/)
- [Cypress Docs](https://www.cypress.io/)

### Issues
- Revisar issues existentes en el repositorio
- Crear nuevo issue con template completo
- Incluir logs y pasos para reproducir

---

## 📄 Licencia

[Especificar licencia del proyecto]

---

## 👨‍💻 Contribuir

1. Fork el proyecto
2. Crear feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a branch (`git push origin feature/AmazingFeature`)
5. Abrir Pull Request usando el [template](./CHECKLIST-CALIDAD-RAPIDA.md#-pull-request-template)

**Asegúrate de:**
- ✅ Seguir el [Checklist de PR](./CHECKLIST-CALIDAD-RAPIDA.md#-pre-pull-request)
- ✅ Tests pasan
- ✅ Cobertura > 80%
- ✅ SonarQube Quality Gate pasa

---

## 📅 Mantenimiento

| Tarea | Frecuencia |
|-------|------------|
| Actualizar dependencias | Mensual |
| Revisión de seguridad (npm audit) | Semanal |
| Auditoría de logs | Semanal |
| Review de performance | Quincenal |
| Disaster recovery test | Trimestral |

Ver [Mantenimiento](./MANUAL-CREACION-APLICACION.md#10-mantenimiento-y-mejora-continua) para detalles.

---

## 🎯 Objetivos del Proyecto

- ✅ Aplicación de alta calidad siguiendo estándares de la industria
- ✅ Seguridad robusta según OWASP Top 10
- ✅ Testing exhaustivo con > 80% cobertura
- ✅ PWA moderna y performante
- ✅ CI/CD completamente automatizado
- ✅ Logs centralizados y monitoreo en tiempo real
- ✅ Documentación completa y actualizada

---

**Versión:** 1.0  
**Última actualización:** 2026-03-07  
**Próxima revisión:** 2026-06-07

---

**¿Preguntas?** Abre un issue o contacta al equipo de arquitectura.

**¡Happy Coding!** 🚀
