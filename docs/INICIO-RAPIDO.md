# 🚀 Guía de Inicio Rápido

> **Objetivo:** Configurar y ejecutar la aplicación en menos de 15 minutos

---

## 📋 Pre-requisitos

Verifica que tienes instalado:

```bash
# Java 17
java -version

# Node.js 18+
node -v
npm -v

# Docker (opcional pero recomendado)
docker -v
docker-compose -v

# Git
git --version
```

**Instalar lo que falte:**
- Java 17: https://adoptium.net/
- Node.js: https://nodejs.org/
- Docker: https://www.docker.com/get-started

---

## ⚡ Inicio en 5 Pasos

### 1️⃣ Clonar el Proyecto

```bash
git clone [URL_DEL_REPOSITORIO]
cd Ing-Software-Apl/backend
```

### 2️⃣ Iniciar Base de Datos

```bash
# Opción A: Docker (recomendado)
docker-compose -f src/main/docker/postgresql.yml up -d

# Opción B: PostgreSQL local
# Crear database: library
# Usuario: library
# Password: library
```

### 3️⃣ Instalar Dependencias

```bash
# Backend (Maven)
./mvnw clean install -DskipTests

# Frontend (npm)
npm install
```

### 4️⃣ Iniciar la Aplicación

```bash
# Terminal 1 - Backend
./mvnw

# Terminal 2 - Frontend (nueva terminal)
npm start
```

### 5️⃣ Abrir en el Navegador

```
http://localhost:9000
```

**Credenciales por defecto:**
- Usuario: `admin`
- Password: `admin`

---

## 🔧 Comandos Esenciales

### Desarrollo

```bash
# Backend (puerto 8080)
./mvnw

# Frontend con hot reload (puerto 9000)
npm start

# Ambos a la vez
npm run watch

# Base de datos
docker-compose -f src/main/docker/postgresql.yml up -d
docker-compose -f src/main/docker/postgresql.yml down
```

### Testing

```bash
# Tests unitarios backend
./mvnw test

# Tests unitarios frontend
npm test

# Tests E2E (requiere app corriendo)
npm run e2e

# Tests E2E sin interfaz
npm run e2e:headless
```

### Build de Producción

```bash
# JAR
./mvnw -Pprod package

# Docker Image
./mvnw -Pprod jib:dockerBuild

# Frontend optimizado
npm run build -- --configuration production
```

---

## 📁 Estructura del Proyecto

```
backend/
├── src/main/
│   ├── java/com/sgaraba/         # Código backend Java
│   │   ├── config/               # Configuraciones
│   │   ├── domain/               # Entidades JPA
│   │   ├── repository/           # Repositories
│   │   ├── service/              # Servicios
│   │   └── web/rest/             # Controllers REST
│   │
│   ├── resources/
│   │   ├── config/               # application.yml por entorno
│   │   └── logback-spring.xml    # Config de logs
│   │
│   └── webapp/                   # Frontend Angular
│       ├── app/                  # Componentes Angular
│       ├── content/              # Assets (CSS, imágenes)
│       └── manifest.webapp       # PWA manifest
│
├── src/test/
│   ├── java/                     # Tests unitarios Java
│   └── javascript/cypress/       # Tests E2E Cypress
│
├── pom.xml                       # Dependencias Maven
├── package.json                  # Dependencias npm
└── cypress.config.ts             # Config Cypress
```

---

## 🎯 Primeros Pasos Después de Instalar

### 1. Explorar la Aplicación

- ✅ Login con admin/admin
- ✅ Navegar por las entidades generadas
- ✅ Crear un nuevo registro
- ✅ Probar editar y eliminar

### 2. Revisar el Código

```bash
# Ver estructura de una entidad
cat src/main/java/com/sgaraba/library/domain/Book.java

# Ver un controller REST
cat src/main/java/com/sgaraba/library/web/rest/BookResource.java

# Ver un componente Angular
cat src/main/webapp/app/entities/book/list/book.component.ts
```

### 3. Ejecutar Tests

```bash
# Tests unitarios (deben pasar todos)
./mvnw test
npm test

# Tests E2E (con app corriendo en otra terminal)
npm run e2e
```

### 4. Revisar la Documentación

- API Swagger: http://localhost:8080/admin/docs
- Métricas: http://localhost:8080/admin/metrics
- Health: http://localhost:8080/admin/health

---

## 🐛 Solución de Problemas Comunes

### Puerto 8080 ya en uso

```bash
# Matar proceso en puerto 8080
# Windows
netstat -ano | findstr :8080
taskkill /PID [PID] /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9

# O cambiar puerto en application-dev.yml
server:
  port: 8081
```

### Puerto 9000 ya en uso

```bash
# Cambiar puerto de Angular
ng serve --port 9001
```

### Error de base de datos

```bash
# Verificar que PostgreSQL está corriendo
docker ps | grep postgres

# Ver logs
docker-compose -f src/main/docker/postgresql.yml logs

# Reiniciar contenedor
docker-compose -f src/main/docker/postgresql.yml restart
```

### Out of memory (Maven)

```bash
# Linux/Mac
export MAVEN_OPTS="-Xmx1024m"

# Windows PowerShell
$env:MAVEN_OPTS="-Xmx1024m"

# O editar .mvn/jvm.config
-Xmx1024m
```

### npm install falla

```bash
# Limpiar caché
npm cache clean --force

# Eliminar node_modules
rm -rf node_modules package-lock.json

# Reinstalar
npm install
```

---

## 🔥 Atajos de Desarrollo

### Scripts NPM Útiles

```bash
# Formatear código
npm run prettier:format

# Lint y fix
npm run lint:fix

# Ver cobertura de tests
npm test -- --coverage

# Build y watch
npm run build-watch

# Ejecutar backend desde npm
npm run app:start
```

### Maven Útiles

```bash
# Compilar sin tests
./mvnw install -DskipTests

# Solo compilar
./mvnw compile

# Limpiar target
./mvnw clean

# Ver dependencias
./mvnw dependency:tree
```

---

## 📚 Crear Nueva Entidad con JHipster

```bash
# Opción 1: Interactivo
jhipster entity Author

# Opción 2: Desde JDL
# Editar library.jdl y agregar:
# entity Author {
#   name String required
#   biography TextBlob
# }

jhipster jdl library.jdl
```

---

## 🔒 Configurar Perfiles de Usuario

```java
// Agregar rol en SecurityConfiguration.java
.requestMatchers("/api/books/**").hasAnyRole("USER", "ADMIN")
.requestMatchers("/api/admin/**").hasRole("ADMIN")

// Crear usuario via API o SQL
INSERT INTO jhi_user (id, login, password_hash, email, activated)
VALUES (2, 'user', '$2a$10$...', 'user@localhost', true);

INSERT INTO jhi_user_authority (user_id, authority_name)
VALUES (2, 'ROLE_USER');
```

---

## 🎨 Personalizar la Aplicación

### Cambiar Título

```typescript
// src/main/webapp/index.html
<title>Mi Aplicación</title>

// src/main/webapp/app/layouts/navbar/navbar.component.html
<a class="navbar-brand">
  <span>Mi Aplicación</span>
</a>
```

### Cambiar Logo

```bash
# Reemplazar archivos en:
src/main/webapp/content/images/logo-jhipster.png
```

### Cambiar Estilos

```scss
// src/main/webapp/content/scss/global.scss
$primary-color: #3f51b5;
$font-family: 'Roboto', sans-serif;
```

---

## 🚀 Próximos Pasos

Después de configurar el proyecto:

1. **Lee el Manual Completo**
   - [MANUAL-CREACION-APLICACION.md](./MANUAL-CREACION-APLICACION.md)

2. **Usa el Checklist de Calidad**
   - [CHECKLIST-CALIDAD-RAPIDA.md](./CHECKLIST-CALIDAD-RAPIDA.md)

3. **Configura tu IDE**
   - Importar proyecto como Maven project
   - Instalar extensiones: Angular, Java, ESLint, Prettier

4. **Configura Git Hooks**
   ```bash
   npm run prepare  # Instala husky hooks
   ```

5. **Configura CI/CD**
   - GitHub Actions / GitLab CI / Jenkins
   - SonarQube
   - Docker Registry

---

## 📖 Recursos de Aprendizaje

### Documentación Oficial
- **JHipster:** https://www.jhipster.tech/
- **Spring Boot:** https://spring.io/projects/spring-boot
- **Angular:** https://angular.io/
- **Cypress:** https://www.cypress.io/

### Tutoriales
- JHipster Getting Started: https://www.jhipster.tech/creating-an-app/
- Spring Boot Guides: https://spring.io/guides
- Angular Tutorial: https://angular.io/tutorial

### Videos
- JHipster YouTube: https://www.youtube.com/c/JHipster
- Spring Boot YouTube: https://www.youtube.com/user/SpringSourceDev

---

## 💡 Tips de Productividad

### IntelliJ IDEA
```
Ctrl+Alt+L      # Formatear código
Ctrl+Shift+F10  # Run
Shift+F10       # Re-run
Ctrl+F9         # Build
```

### VS Code
```
Ctrl+Shift+P    # Command Palette
Ctrl+P          # Quick Open
F5              # Debug
Ctrl+Shift+F    # Search in files
```

### Terminal
```bash
# Alias útiles (agregar a .bashrc o .zshrc)
alias mci='./mvnw clean install'
alias mt='./mvnw test'
alias nt='npm test'
alias ns='npm start'
```

---

## 🆘 ¿Necesitas Ayuda?

1. **Revisar logs**
   ```bash
   # Backend
   tail -f logs/application.log
   
   # Frontend
   # Ver consola del navegador (F12)
   ```

2. **Documentación del proyecto**
   - README.md
   - MANUAL-CREACION-APLICACION.md
   - Código fuente con comentarios

3. **Stack Trace completo**
   ```bash
   ./mvnw test -X  # Maven verbose
   npm test -- --verbose  # npm verbose
   ```

4. **Buscar en Issues**
   - GitHub Issues del proyecto
   - JHipster GitHub Issues: https://github.com/jhipster/generator-jhipster/issues

---

## ✅ Verificación Final

Antes de empezar a desarrollar, verifica:

- [ ] ✅ Aplicación corre en http://localhost:9000
- [ ] ✅ Login funciona con admin/admin
- [ ] ✅ Base de datos conectada
- [ ] ✅ Tests unitarios pasan (`./mvnw test`)
- [ ] ✅ Tests frontend pasan (`npm test`)
- [ ] ✅ Build exitoso (`./mvnw install -DskipTests`)
- [ ] ✅ IDE configurado (imports, formatters)
- [ ] ✅ Git configurado (user.name, user.email)

---

## 🎉 ¡Listo para Desarrollar!

Ahora estás listo para:
- ✅ Crear nuevas entidades
- ✅ Agregar funcionalidades
- ✅ Escribir tests
- ✅ Seguir las mejores prácticas del manual

**¡Happy Coding!** 🚀

---

**Versión:** 1.0  
**Última actualización:** 2026-03-07
