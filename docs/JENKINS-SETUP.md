# 🚀 Guía de Configuración de Jenkins CI/CD

**Última actualización:** 9 de marzo de 2026

Esta guía te ayudará a configurar Jenkins para automatizar el build, testing y deployment de tu aplicación Library Management System.

---

## 📋 Tabla de Contenidos

1. [Prerequisitos](#prerequisitos)
2. [Levantar Jenkins](#levantar-jenkins)
3. [Configuración Inicial](#configuración-inicial)
4. [Configurar Credenciales](#configurar-credenciales)
5. [Crear Pipeline](#crear-pipeline)
6. [Ejecutar Pipeline](#ejecutar-pipeline)
7. [Troubleshooting](#troubleshooting)
8. [Apagar Jenkins](#apagar-jenkins)

---

## 📦 Prerequisitos

### ✅ Requisitos previos
- [x] Docker Desktop instalado y corriendo
- [x] Docker Compose instalado
- [x] Cuenta en DockerHub (https://hub.docker.com)
- [x] Repositorio Git del proyecto (local o remoto)
- [x] 4GB RAM disponible (mínimo)
- [x] Puerto 8090 libre (Jenkins UI)
- [x] Puerto 9000 libre (SonarQube)

### 📁 Archivos necesarios
- `docker-compose-jenkins.yml` - Configuración de Jenkins
- `Jenkinsfile` - Pipeline CI/CD

---

## 🚀 Levantar Jenkins

### Paso 1: Navegar al directorio del proyecto

```powershell
cd c:\Users\totob\Documents\VSCodes\Ing-Software-Apl\backend
```

### Paso 2: Levantar los servicios

```powershell
# Levantar Jenkins + SonarQube + PostgreSQL
docker-compose -f docker-compose-jenkins.yml up -d

# Ver logs en tiempo real (opcional)
docker-compose -f docker-compose-jenkins.yml logs -f jenkins
```

### Paso 3: Esperar a que Jenkins inicie

⏱️ **Primera vez:** 2-3 minutos  
⏱️ **Subsecuentes:** 30-60 segundos

Verás en los logs:
```
*************************************************************
Jenkins initial setup is required. An admin user has been created and a password generated.
Please use the following password to proceed to installation:

a1b2c3d4e5f6g7h8i9j0  <-- Este es tu password inicial

*************************************************************
```

### Paso 4: Verificar que está corriendo

```powershell
# Ver estado de contenedores
docker-compose -f docker-compose-jenkins.yml ps

# Debería mostrar:
# NAME        STATE    PORTS
# jenkins     Up       0.0.0.0:8090->8080/tcp, 0.0.0.0:50000->50000/tcp
# sonarqube   Up       0.0.0.0:9000->9000/tcp
# sonar-db    Up       5432/tcp
```

**URLs disponibles:**
- 🎯 Jenkins: http://localhost:8090/jenkins
- 📊 SonarQube: http://localhost:9000

---

## ⚙️ Configuración Inicial

### Paso 1: Obtener la contraseña inicial

**Opción A:** Desde los logs (ver arriba)

**Opción B:** Desde el contenedor
```powershell
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

Copia el password (ej: `a1b2c3d4e5f6g7h8i9j0`)

### Paso 2: Abrir Jenkins en el navegador

1. Ve a: http://localhost:8090/jenkins
2. Pega el password inicial
3. Click en **"Continue"**

### Paso 3: Instalar plugins

Cuando aparezca "Customize Jenkins":

**Opción recomendada:** Select plugins to install

Seleccionar estos plugins:
- ✅ **Git** - Para clonar repositorios
- ✅ **Pipeline** - Para ejecutar Jenkinsfiles
- ✅ **Docker Pipeline** - Para build de imágenes Docker
- ✅ **Docker** - Integración con Docker
- ✅ **SonarQube Scanner** - Análisis de código
- ✅ **OWASP Dependency-Check** - Seguridad
- ✅ **Blue Ocean** (opcional) - UI moderna

Click en **"Install"** y espera 3-5 minutos.

### Paso 4: Crear usuario administrador

Cuando termine la instalación:

```
Username:      admin
Password:      admin123
Confirm:       admin123
Full name:     Jenkins Admin
Email:         tu-email@ejemplo.com
```

Click **"Save and Continue"**

### Paso 5: Configurar URL de Jenkins

Dejar por defecto: `http://localhost:8090/jenkins`

Click **"Save and Finish"** → **"Start using Jenkins"**

---

## 🔑 Configurar Credenciales

### 1️⃣ Credenciales de DockerHub

#### Paso 1: Ir a Credentials

1. Dashboard → **Manage Jenkins** → **Credentials**
2. Click en **(global)** → **Add Credentials**

#### Paso 2: Crear credencial DockerHub

```
Kind:           Username with password
Scope:          Global
Username:       tu-usuario-dockerhub
Password:       tu-password-dockerhub  (o Access Token)
ID:             docker-hub-credentials
Description:    DockerHub Registry Credentials
```

Click **"Create"**

#### 📝 Nota: Crear Access Token en DockerHub (recomendado)

1. Ve a: https://hub.docker.com/settings/security
2. Click **"New Access Token"**
3. Token description: `Jenkins CI/CD`
4. Access permissions: `Read, Write, Delete`
5. Click **"Generate"**
6. **Copia el token** (solo se muestra una vez)
7. Usa el token como password en Jenkins

### 2️⃣ Credenciales de SonarQube

#### Paso 1: Configurar SonarQube

1. Ve a: http://localhost:9000
2. Login inicial:
   - Username: `admin`
   - Password: `admin`
3. Te pedirá cambiar password → usa: `admin123`

#### Paso 2: Crear token en SonarQube

1. Click en tu avatar (arriba derecha) → **My Account**
2. Click en **Security** tab
3. Generar token:
   - Name: `Jenkins`
   - Type: `User Token`
   - Expires in: `No expiration`
4. Click **"Generate"**
5. **Copia el token** (ej: `squ_1234567890abcdef`)

#### Paso 3: Crear credencial en Jenkins

Dashboard → Manage Jenkins → Credentials → Add Credentials

```
Kind:           Secret text
Scope:          Global
Secret:         squ_1234567890abcdef  (tu token de SonarQube)
ID:             sonar-token
Description:    SonarQube Analysis Token
```

Click **"Create"**

#### Paso 4: Configurar servidor SonarQube en Jenkins

1. Dashboard → **Manage Jenkins** → **System**
2. Scroll hasta **SonarQube servers**
3. Check ✅ **Environment variables**
4. Click **"Add SonarQube"**

```
Name:                   SonarQube
Server URL:             http://sonarqube:9000
Server authentication token:  (seleccionar: sonar-token)
```

5. Click **"Save"**

---

## 🎯 Crear Pipeline

### Opción A: Pipeline desde Git (Recomendado)

1. Dashboard → **New Item**
2. Nombre: `Library-Pipeline`
3. Tipo: **Pipeline**
4. Click **"OK"**

#### Configuración del Pipeline:

**General:**
- ✅ Discard old builds: Max # of builds to keep: `10`

**Pipeline:**
```
Definition:     Pipeline script from SCM
SCM:            Git
Repository URL: https://github.com/tu-usuario/tu-repo.git
             (o ruta local: file:///c:/Users/totob/Documents/VSCodes/Ing-Software-Apl)
Credentials:    (dejar en blanco si es repo público o local)
Branch:         */main  (o */master según tu rama)
Script Path:    backend/Jenkinsfile
```

Click **"Save"**

### Opción B: Pipeline script directo (Para testing rápido)

Si solo quieres probar:

1. Definition: **Pipeline script**
2. Copiar contenido de `backend/Jenkinsfile`
3. Click **"Save"**

---

## ▶️ Ejecutar Pipeline

### Primer Build

1. Click en tu pipeline **"Library-Pipeline"**
2. Click **"Build Now"** (izquierda)
3. Verás aparecer **#1** en "Build History"
4. Click en **#1** → **Console Output** para ver logs en tiempo real

### Etapas del Pipeline

```
✓ Checkout           - Clona el repositorio
✓ Build              - Compila con Maven
✓ Unit Tests         - Ejecuta tests (Backend + Frontend en paralelo)
✓ Code Quality       - Análisis con SonarQube
✓ Quality Gate       - Valida métricas de calidad
✓ Security Scan      - OWASP Dependency Check
✓ Build Docker Image - Crea imagen Docker
✓ Push Docker Image  - Sube a DockerHub
✓ E2E Tests          - Tests de Cypress
✓ Deploy to K8s      - (Opcional) Deploy a Kubernetes
```

### ⏱️ Tiempo estimado

- **Primera ejecución:** 15-20 minutos (descarga dependencias)
- **Ejecuciones subsecuentes:** 5-10 minutos (con caché)

### ✅ Build exitoso

Verás en la UI de Jenkins:
```
✓ #1  ══════════════════════  SUCCESS
```

Y en DockerHub aparecerá tu imagen:
- `tu-usuario/library:latest`
- `tu-usuario/library:abc1234` (con hash del commit)

---

## 📊 Ver Resultados

### SonarQube Dashboard

1. Ve a: http://localhost:9000
2. Deberías ver el proyecto **"library"**
3. Click para ver:
   - Bugs detectados
   - Vulnerabilidades
   - Code smells
   - Cobertura de tests
   - Duplicación de código

### Blue Ocean (UI moderna)

1. Click en **"Open Blue Ocean"** (menú lateral)
2. Verás una visualización gráfica del pipeline
3. Cada stage se muestra como un cuadro
4. Colores:
   - 🟢 Verde = Success
   - 🔴 Rojo = Failed
   - 🟡 Amarillo = Unstable

---

## 🐛 Troubleshooting

### Problema: "Permission denied" al conectar a Docker

**Síntoma:**
```
ERROR: Cannot connect to Docker daemon
```

**Solución Windows:**
1. Asegúrate de que Docker Desktop esté corriendo
2. En Docker Desktop Settings → General → ✅ "Expose daemon on tcp://localhost:2375"
3. Reinicia Jenkins: `docker-compose -f docker-compose-jenkins.yml restart jenkins`

**Solución alternativa:** Reiniciar contenedor como root (ya está configurado)

---

### Problema: Puerto 8090 ya está en uso

**Síntoma:**
```
Error starting userland proxy: listen tcp4 0.0.0.0:8090: bind: address already in use
```

**Solución:**
Editar `docker-compose-jenkins.yml`:
```yaml
ports:
  - "8091:8080"  # Cambiar 8090 por otro puerto libre
```

Luego acceder a: http://localhost:8091/jenkins

---

### Problema: Jenkins se queda sin memoria

**Síntoma:**
```
java.lang.OutOfMemoryError: Java heap space
```

**Solución:**
Editar `docker-compose-jenkins.yml`:
```yaml
environment:
  - JAVA_OPTS=-Xmx3g -Xms2g  # Aumentar de 2g a 3g
```

---

### Problema: SonarQube no inicia

**Síntoma:**
```
max virtual memory areas vm.max_map_count [65530] is too low
```

**Solución Windows (WSL2):**
```powershell
# En PowerShell como Administrador
wsl -d docker-desktop
sysctl -w vm.max_map_count=262144
exit
```

Luego reiniciar:
```powershell
docker-compose -f docker-compose-jenkins.yml restart sonarqube
```

---

### Problema: Tests Cypress fallan en pipeline

**Síntoma:**
```
Cypress could not verify that this server is running
```

**Solución:**
Los tests E2E necesitan que la aplicación esté corriendo. Opciones:

1. **Skipear E2E temporalmente** (editar Jenkinsfile):
```groovy
stage('E2E Tests') {
    when {
        expression { false }  // Deshabilita temporalmente
    }
    ...
}
```

2. **Levantar app antes del pipeline:**
```powershell
docker-compose -f docker-compose-full.yml up -d app
```

---

## 🛑 Apagar Jenkins

### Detener servicios (datos se preservan)

```powershell
docker-compose -f docker-compose-jenkins.yml stop
```

### Bajar contenedores (datos se preservan)

```powershell
docker-compose -f docker-compose-jenkins.yml down
```

### Eliminar TODO (incluyendo volúmenes y datos)

⚠️ **CUIDADO:** Esto borrará configuración, credenciales, histórico de builds

```powershell
docker-compose -f docker-compose-jenkins.yml down -v
```

### Ver recursos usados

```powershell
# Ver volúmenes
docker volume ls | findstr jenkins

# Ver cuánto espacio ocupan
docker system df -v
```

---

## 📸 Capturas de Pantalla para Documentación

Para tu entrega del proyecto, toma capturas de:

1. ✅ **Jenkins Dashboard** - Mostrando pipeline completado
2. ✅ **Console Output** - Logs del build exitoso
3. ✅ **Blue Ocean** - Pipeline visual con todas las stages en verde
4. ✅ **SonarQube** - Dashboard con análisis de código
5. ✅ **DockerHub** - Mostrando la imagen subida con tags
6. ✅ **Quality Gate** - Pasando los criterios de calidad

---

## 🎓 Comandos de Referencia Rápida

```powershell
# Levantar Jenkins
docker-compose -f docker-compose-jenkins.yml up -d

# Ver logs
docker-compose -f docker-compose-jenkins.yml logs -f jenkins

# Ver password inicial
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword

# Reiniciar Jenkins
docker-compose -f docker-compose-jenkins.yml restart jenkins

# Detener Jenkins
docker-compose -f docker-compose-jenkins.yml stop

# Bajar Jenkins (preserva datos)
docker-compose -f docker-compose-jenkins.yml down

# Estado de servicios
docker-compose -f docker-compose-jenkins.yml ps

# Acceder a shell de Jenkins
docker exec -it jenkins bash
```

---

## 📚 Recursos Adicionales

- **Jenkins Documentation:** https://www.jenkins.io/doc/
- **Pipeline Syntax:** https://www.jenkins.io/doc/book/pipeline/syntax/
- **SonarQube Docs:** https://docs.sonarqube.org/
- **Docker Pipeline Plugin:** https://plugins.jenkins.io/docker-workflow/
- **Blue Ocean:** https://www.jenkins.io/doc/book/blueocean/

---

## ✅ Checklist de Verificación

Antes de considerar Jenkins completado, verifica:

- [ ] Jenkins levanta correctamente en http://localhost:8090/jenkins
- [ ] SonarQube levanta en http://localhost:9000
- [ ] Credenciales de DockerHub configuradas
- [ ] Credenciales de SonarQube configuradas
- [ ] Pipeline creado y vinculado al Jenkinsfile
- [ ] Pipeline ejecuta todos los stages exitosamente
- [ ] Imagen Docker se genera correctamente
- [ ] Imagen Docker se sube a DockerHub
- [ ] SonarQube muestra análisis de código
- [ ] Quality Gate pasa
- [ ] Capturas de pantalla tomadas para documentación

---

**¡Listo!** 🎉 Ahora tienes un servidor de CI/CD completo que automatiza todo el proceso desde commit hasta deploy.
