# 🐳 Configurar Push a DockerHub en Jenkins

## Beneficios
- ✅ Imagen accesible desde cualquier servidor
- ✅ Versionado automático (tags por commit)
- ✅ Historial completo de releases
- ✅ Deploy rápido en producción
- ✅ CI/CD profesional

---

## Pasos de Configuración

### 1. Crear cuenta en DockerHub (GRATIS)
1. Ir a https://hub.docker.com/signup
2. Crear cuenta con email
3. Verificar email

### 2. Obtener credenciales
```
Usuario: tu-usuario-dockerhub
Password: tu-contraseña
```

### 3. Configurar credenciales en Jenkins

**Opción A: Por interfaz web**
1. Ir a http://localhost:8090/jenkins
2. Manage Jenkins → Credentials → System → Global credentials
3. Add Credentials:
   - Kind: `Username with password`
   - Username: `tu-usuario-dockerhub`
   - Password: `tu-contraseña`
   - ID: `docker-hub-credentials`
   - Description: `DockerHub credentials`
4. Save

**Opción B: Por script (PowerShell)**
```powershell
# Desde tu terminal
$username = Read-Host "DockerHub username"
$password = Read-Host -AsSecureString "DockerHub password"
$passwordPlain = [System.Net.NetworkCredential]::new("", $password).Password

# Crear credential via Jenkins CLI
docker exec jenkins java -jar /var/jenkins_home/war/WEB-INF/jenkins-cli.jar `
  -s http://localhost:8080/jenkins/ `
  -auth admin:admin123 `
  create-credentials-by-xml system::system::jenkins "(global)" `
  < credentials.xml
```

### 4. Actualizar Jenkinsfile

Cambiar tu usuario en el Jenkinsfile:
```groovy
environment {
    DOCKER_IMAGE = "tu-usuario/library-app"  // ← Cambiar aquí
    DOCKER_CREDENTIALS = credentials('docker-hub-credentials')
}
```

Habilitar el stage de push:
```groovy
stage('Push Docker Image') {
    when {
        expression { true }  // ← Cambiar a true
    }
    steps {
        script {
            sh """
                echo \$DOCKER_CREDENTIALS_PSW | docker login -u \$DOCKER_CREDENTIALS_USR --password-stdin
                docker push ${DOCKER_IMAGE}:${IMAGE_TAG}
                docker push ${DOCKER_IMAGE}:latest
            """
        }
    }
}
```

### 5. Commit y Push
```powershell
git add backend/Jenkinsfile
git commit -m "Enable DockerHub push in Jenkins pipeline"
git push
```

### 6. Ejecutar Build
1. Ir a Jenkins → Library-Pipeline
2. Click "Build Now"
3. Ver logs → debería pushear a DockerHub

---

## Verificar que funcionó

### En DockerHub (web)
1. Ir a https://hub.docker.com
2. Login con tu cuenta
3. Repositories → deberías ver `tu-usuario/library-app`
4. Tags → deberías ver:
   - `latest`
   - Commit SHA (ej: `abc1234`)

### Desde cualquier máquina
```bash
# Pull la imagen
docker pull tu-usuario/library-app:latest

# Ver la imagen
docker images tu-usuario/library-app

# Ejecutar
docker run -d -p 8080:8080 tu-usuario/library-app:latest
```

---

## Uso en Producción

### Deploy simple
```bash
# En servidor de producción
docker pull tu-usuario/library-app:latest
docker stop library-prod || true
docker rm library-prod || true
docker run -d \
  --name library-prod \
  -p 80:8080 \
  --restart unless-stopped \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/library \
  tu-usuario/library-app:latest
```

### Con Docker Compose
```yaml
version: '3.8'
services:
  app:
    image: tu-usuario/library-app:latest
    ports:
      - "80:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
    restart: unless-stopped
```

### Rollback rápido
```bash
# Si la última versión tiene problemas
docker pull tu-usuario/library-app:v1.0.0  # versión anterior
docker run -d -p 80:8080 tu-usuario/library-app:v1.0.0
```

---

## Tagging Strategy

Jenkins creará automáticamente estos tags:

| Tag | Ejemplo | Uso |
|-----|---------|-----|
| `latest` | `library-app:latest` | Última versión siempre |
| Commit SHA | `library-app:abc1234` | Versión específica por commit |
| Branch | `library-app:master` | Versión de branch específico |
| Semantic | `library-app:v1.0.0` | Release versionado (manual) |

---

## Costos

| Tier | Precio | Repositorios | Límites |
|------|--------|--------------|---------|
| **Free** | $0/mes | 1 privado, ∞ públicos | Pull ilimitado |
| Personal | $5/mes | ∞ privados | Pull ilimitado |
| Team | $7/mes/usuario | ∞ privados + colaboración | Pull ilimitado |

**Recomendación:** Usar tier FREE con repositorio público es perfectamente válido para proyectos educativos/portfolio.

---

## Alternativas Gratuitas

Si no quieres usar DockerHub:

### GitHub Container Registry (GHCR)
```groovy
environment {
    DOCKER_IMAGE = "ghcr.io/tu-usuario/library-app"
}
```
- ✅ Gratis ilimitado
- ✅ Repositorios privados incluidos
- ✅ Integración directa con GitHub

### GitLab Container Registry
- ✅ Gratis ilimitado  
- ✅ Repositorios privados incluidos
- ✅ CI/CD incluido

### Azure Container Registry (ACR)
- ✅ $0.17/día (~$5/mes) tier Basic
- ✅ Pull/push rápido en Azure

---

## Troubleshooting

### Error: "unauthorized: incorrect username or password"
- Verificar credenciales en Jenkins
- ID debe ser exactamente: `docker-hub-credentials`
- Probar login manual: `docker login`

### Error: "denied: requested access to the resource is denied"
- El repositorio no existe en DockerHub
- Crear el repositorio manualmente primero
- O hacer push inicial desde tu máquina:
  ```bash
  docker tag library-app:latest tu-usuario/library-app:latest
  docker push tu-usuario/library-app:latest
  ```

### Error: "toomanyrequests: too many failed login attempts"
- Esperar 5 minutos
- Verificar que password sea correcto
- Revisar logs de Jenkins: `docker logs jenkins`

---

## Resumen

**Sin DockerHub:**
- ✅ Cumples requisito básico de Jenkins
- ❌ Imagen solo local
- ❌ No histórico de versiones

**Con DockerHub:**
- ✅ Deploy en cualquier servidor
- ✅ Versionado automático
- ✅ Trabajo en equipo
- ✅ CI/CD completo profesional
- ✅ Se ve mejor en portfolio/CV

**Tiempo de setup:** ~5 minutos  
**Costo:** $0 (tier FREE)  
**Dificultad:** Baja  
