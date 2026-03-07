# ✅ Checklist de Calidad Rápida

> **Uso:** Revisar este checklist antes de cada commit, PR y deploy

---

## 🚀 Pre-Commit (Antes de cada commit)

```bash
# 1. Formateo de código
npm run prettier:format
./mvnw spotless:apply

# 2. Linting
npm run lint:fix

# 3. Tests unitarios
npm test
./mvnw test

# 4. Compilación
npm run build
./mvnw clean install -DskipTests
```

**Checklist Manual:**
- [ ] No hay `console.log()` o `System.out.println()` en código
- [ ] No hay código comentado innecesario
- [ ] Variables tienen nombres descriptivos
- [ ] No hay valores hardcodeados (usar configuración)

---

## 📝 Pre-Pull Request

### Código
- [ ] Sin duplicación de código (DRY principle)
- [ ] Funciones < 50 líneas
- [ ] Clases con responsabilidad única (SRP)
- [ ] Código revisado por otro desarrollador

### Tests
- [ ] Tests unitarios agregados para nuevo código
- [ ] Tests de integración actualizados
- [ ] Cobertura > 80%
- [ ] Tests E2E pasan (si aplica)

### Seguridad
- [ ] Sin passwords/tokens en código
- [ ] Inputs validados
- [ ] `npm audit` sin vulnerabilidades críticas
- [ ] Sin SQL concatenado (usar queries parametrizadas)

### Performance
- [ ] Sin queries N+1
- [ ] Lazy loading implementado
- [ ] Build size dentro de budget

### Documentación
- [ ] README actualizado (si aplica)
- [ ] Comentarios en lógica compleja
- [ ] CHANGELOG actualizado

---

## 🔒 Checklist de Seguridad (OWASP Top 10)

| # | Vulnerabilidad | Verificación |
|---|----------------|--------------|
| A1 | Control de Acceso | [ ] Roles verificados en backend, no solo frontend |
| A2 | Criptografía | [ ] HTTPS configurado, passwords hasheados con BCrypt |
| A3 | Inyección SQL | [ ] Queries parametrizadas, validación de inputs |
| A4 | Diseño Inseguro | [ ] Modelado de amenazas realizado |
| A5 | Configuración | [ ] Sin credenciales por defecto, headers de seguridad |
| A6 | Componentes Vulnerables | [ ] `npm audit` y `mvn dependency-check` sin críticos |
| A7 | Autenticación | [ ] JWT seguro, políticas de password |
| A8 | Integridad de Datos | [ ] CORS configurado, validación de firmas |
| A9 | Logs | [ ] Logs centralizados, no logear datos sensibles |
| A10 | SSRF | [ ] URLs remotas validadas |

```bash
# Ejecutar análisis de seguridad
npm audit
./mvnw org.owasp:dependency-check-maven:check

# Escaneo con ZAP (opcional)
docker run -t owasp/zap2docker-stable zap-baseline.py -t http://localhost:8080
```

---

## 🧪 Checklist de Testing

### Tests Unitarios
```bash
# Backend
./mvnw test
# Objetivo: > 80% cobertura

# Frontend  
npm test -- --coverage
# Objetivo: > 80% cobertura
```

- [ ] Tests para casos exitosos
- [ ] Tests para casos de error
- [ ] Tests para validaciones
- [ ] Tests para edge cases

### Tests de Integración
```bash
./mvnw verify
```

- [ ] Tests de endpoints REST
- [ ] Tests de transacciones
- [ ] Tests de relaciones entre entidades

### Tests E2E
```bash
npm run e2e:headless
```

- [ ] Flujo de login/logout
- [ ] CRUD completo de entidades principales
- [ ] Navegación entre páginas
- [ ] Validación de formularios

---

## 📊 Checklist de PWA

### Básico
- [ ] `manifest.webapp` configurado
- [ ] Service Worker activo
- [ ] Funciona offline (páginas críticas)
- [ ] HTTPS configurado
- [ ] Responsive design (mobile/tablet/desktop)

### Avanzado
- [ ] Estrategias de caché configuradas
- [ ] Actualización automática de SW
- [ ] Página de fallback offline
- [ ] Iconos de todos los tamaños
- [ ] Meta tags SEO

### Auditoría Lighthouse
```bash
# Instalar Lighthouse CI
npm install -g @lhci/cli

# Ejecutar
lhci autorun
```

**Objetivos:**
- [ ] Performance > 80
- [ ] Accessibility > 90
- [ ] Best Practices > 90
- [ ] SEO > 90
- [ ] PWA > 90

---

## 🔄 Checklist de CI/CD

### GitHub Actions / GitLab CI
- [ ] Pipeline de CI configurado
- [ ] Tests automáticos en cada PR
- [ ] Build de Docker exitoso
- [ ] Deploy automático a staging

### SonarQube
```bash
./mvnw sonar:sonar
```

**Quality Gates:**
- [ ] Coverage > 80%
- [ ] Duplications < 3%
- [ ] Maintainability = A
- [ ] Reliability = A
- [ ] Security = A

---

## 📝 Checklist de Logs

### Configuración
- [ ] Logback configurado (`logback-spring.xml`)
- [ ] Niveles apropiados (ERROR, WARN, INFO, DEBUG)
- [ ] Formato JSON para ELK
- [ ] Rotación de logs configurada

### Mejores Prácticas
- [ ] Usar placeholders: `log.info("User {} logged in", username)`
- [ ] No logear datos sensibles (passwords, tokens)
- [ ] Incluir request ID para tracing
- [ ] Logs estructurados con contexto

```java
// ✓ CORRECTO
log.info("Book created with ID: {}", book.getId());

// ✗ INCORRECTO
log.info("Book created with ID: " + book.getId());
System.out.println("Debug: " + book);
```

---

## 🚢 Pre-Deploy a Producción

### 1. Verificaciones Técnicas
```bash
# Tests completos
npm run ci:backend:test
npm run ci:frontend:test
npm run e2e:headless

# Build de producción
./mvnw -Pprod package
npm run build -- --configuration production

# Análisis de seguridad
npm audit
./mvnw org.owasp:dependency-check-maven:check

# Quality gate
./mvnw sonar:sonar
```

- [ ] Todos los tests pasan (unit + integration + E2E)
- [ ] Build de producción exitoso
- [ ] Sin vulnerabilidades críticas
- [ ] SonarQube Quality Gate pasa
- [ ] Lighthouse score > 80

### 2. Configuración
- [ ] Variables de entorno configuradas
- [ ] Base de datos migrada (Liquibase)
- [ ] Certificados SSL válidos
- [ ] CORS configurado correctamente
- [ ] Logs apuntando a ELK

### 3. Backup & Rollback
- [ ] Backup de base de datos realizado
- [ ] Plan de rollback documentado
- [ ] Versión anterior disponible
- [ ] Equipo notificado del deploy

### 4. Monitoreo
- [ ] Alertas configuradas (Prometheus/Grafana)
- [ ] Dashboard de monitoreo activo
- [ ] Health checks funcionando
- [ ] Logs centralizados en Kibana

---

## 📈 Métricas de Calidad (Objetivos)

| Métrica | Objetivo | Herramienta |
|---------|----------|-------------|
| **Code Coverage** | > 80% | JaCoCo + Jest |
| **Build Time** | < 5 min | CI/CD |
| **Response Time p95** | < 500ms | Prometheus |
| **Error Rate** | < 0.1% | Logs/Monitoring |
| **Uptime** | > 99.9% | Monitoring |
| **Security Rating** | A | SonarQube |
| **Maintainability** | A | SonarQube |
| **Lighthouse PWA** | > 90 | Chrome DevTools |

---

## 🛠️ Comandos Útiles

```bash
# Inicio rápido
./mvnw                                    # Backend
npm start                                 # Frontend  
docker-compose -f src/main/docker/postgresql.yml up -d  # DB

# Testing
npm test                                  # Unit tests frontend
./mvnw test                               # Unit tests backend
npm run e2e                               # E2E con interfaz
npm run e2e:headless                      # E2E sin interfaz

# Build producción
./mvnw -Pprod package                     # JAR
./mvnw -Pprod jib:dockerBuild            # Docker image

# Calidad
npm run lint                              # Lint frontend
./mvnw checkstyle:check                  # Checkstyle backend
./mvnw sonar:sonar                       # SonarQube

# Seguridad
npm audit                                 # Audit npm
npm audit fix                             # Fix vulnerabilities
./mvnw org.owasp:dependency-check-maven:check  # OWASP check

# Actualizar dependencias
npm outdated                              # Ver actualizaciones npm
npm update                                # Actualizar npm
./mvnw versions:display-dependency-updates  # Ver actualizaciones Maven

# Logs
docker-compose -f src/main/docker/elk-stack.yml up -d  # Iniciar ELK
open http://localhost:5601                # Abrir Kibana
```

---

## 🔄 Mantenimiento Regular

### Diario
- [ ] Revisar logs de errores en Kibana
- [ ] Verificar estado de servicios
- [ ] Revisar métricas de performance

### Semanal
- [ ] Escaneo de seguridad (`npm audit`)
- [ ] Revisión de PRs pendientes
- [ ] Actualizar dependencias críticas

### Mensual
- [ ] Actualización de dependencias completa
- [ ] Revisión de documentación
- [ ] Auditoría de código técnico
- [ ] Review de performance

### Trimestral
- [ ] Test de disaster recovery
- [ ] Capacitación del equipo
- [ ] Revisión de arquitectura
- [ ] Actualización del manual

---

## 📚 Referencias Rápidas

- [Manual Completo](./MANUAL-CREACION-APLICACION.md)
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [JHipster Docs](https://www.jhipster.tech/)
- [Cypress Best Practices](https://docs.cypress.io/guides/references/best-practices)
- [Angular Style Guide](https://angular.io/guide/styleguide)
- [Spring Boot Best Practices](https://docs.spring.io/spring-boot/docs/current/reference/html/)

---

## ✅ Pull Request Template

```markdown
## Descripción
[Descripción clara de los cambios]

## Tipo de cambio
- [ ] Bug fix
- [ ] Nueva funcionalidad
- [ ] Breaking change
- [ ] Actualización de documentación

## Checklist
- [ ] Tests unitarios agregados/actualizados
- [ ] Tests E2E agregados/actualizados (si aplica)
- [ ] Cobertura de código > 80%
- [ ] Código revisado y formateado
- [ ] Sin vulnerabilidades de seguridad
- [ ] Documentación actualizada
- [ ] CHANGELOG actualizado

## Tests
- [ ] `npm test` ✓
- [ ] `./mvnw test` ✓
- [ ] `npm run e2e:headless` ✓

## Screenshots (si aplica)
[Agregar screenshots de cambios visuales]
```

---

**Versión:** 1.0  
**Última actualización:** 2026-03-07
