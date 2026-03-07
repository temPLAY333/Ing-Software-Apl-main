# 🐳 Dockerfile, Jenkins y ELK Stack - Guía de Uso

## 📋 Tabla de Contenidos

- [Arquitectura](#arquitectura)
- [Prerequisitos](#prerequisitos)
- [Construcción y Despliegue](#construcción-y-despliegue)
- [Jenkins CI/CD](#jenkins-cicd)
- [ELK Stack - Logs Centralizados](#elk-stack---logs-centralizados)
- [Monitoreo](#monitoreo)

---

## 🏗️ Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│                      JENKINS CI/CD                           │
│  Build → Test → Quality → Security → Docker → Deploy        │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                  DOCKER CONTAINERS                           │
│                                                              │
│  ┌─────────────┐  ┌──────────────┐  ┌───────────────────┐  │
│  │   Library   │  │  PostgreSQL  │  │    ELK Stack      │  │
│  │     App     │→→│   Database   │  │ - Elasticsearch   │  │
│  │   (8080)    │  │    (5432)    │  │ - Logstash (5000) │  │
│  └──────┬──────┘  └──────────────┘  │ - Kibana (5601)   │  │
│         │                            │ - Filebeat        │  │
│         └────→ Logs ─────────────→→→│                   │  │
│                                      └───────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

---

## 📦 Prerequisitos

### Software Requerido

- **Docker** >= 20.10
- **Docker Compose** >= 2.0
- **Maven** >= 3.9
- **Java JDK** 17
- **Node.js** >= 18
- **Jenkins** (opcional, para CI/CD)

### Verificar Instalación

```bash
docker --version
docker-compose --version
java -version
mvn -version
node --version
```

---

## 🚀 Construcción y Despliegue

### 1. Construcción Local (Sin Docker)

```bash
cd backend

# Compilar aplicación
./mvnw clean package -Pprod -DskipTests

# Ejecutar aplicación
java -jar target/*.jar
```

### 2. Construcción con Docker

```bash
cd backend

# Construir imagen Docker
docker build -t library:latest .

# Verificar imagen
docker images | grep library
```

### 3. Despliegue Completo con Docker Compose

**Opción A: Solo Aplicación + Base de Datos**

```bash
cd backend
docker-compose -f src/main/docker/app.yml up -d
```

**Opción B: Stack Completo (App + DB + ELK)**

```bash
cd backend
docker-compose -f docker-compose-full.yml up -d
```

**Opción C: Solo ELK Stack**

```bash
cd backend
docker-compose -f src/main/docker/elk-stack.yml up -d
```

### 4. Verificar Servicios

```bash
# Ver logs de la aplicación
docker logs library-app -f

# Ver estado de contenedores
docker-compose -f docker-compose-full.yml ps

# Verificar salud de servicios
curl http://localhost:8080/management/health
curl http://localhost:9200/_cluster/health
curl http://localhost:5601/api/status
```

---

## 🔧 Jenkins CI/CD

### Configuración Inicial

1. **Instalar Jenkins**

```bash
docker run -d -p 8081:8080 -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  --name jenkins \
  jenkins/jenkins:lts
```

2. **Obtener Password Inicial**

```bash
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

3. **Configurar Credenciales en Jenkins**

Ir a: `Manage Jenkins → Credentials → Add Credentials`

- **Docker Hub**: `docker-hub-credentials`
- **SonarQube Token**: `sonar-token`
- **SSH Credentials**: `ssh-credentials` (para deploy)

### Crear Pipeline

1. **New Item → Pipeline**
2. **Pipeline → Definition**: Pipeline script from SCM
3. **SCM**: Git
4. **Repository URL**: `https://github.com/yourrepo/library.git`
5. **Script Path**: `backend/Jenkinsfile`

### Etapas del Pipeline

```groovy
1. ✓ Checkout         - Obtener código del repositorio
2. ✓ Build            - Compilar con Maven
3. ✓ Unit Tests       - Tests backend + frontend
4. ✓ Code Quality     - Análisis SonarQube
5. ✓ Quality Gate     - Verificar umbrales de calidad
6. ✓ Security Scan    - OWASP Dependency Check
7. ✓ Build Docker     - Construir imagen
8. ✓ Push Image       - Subir a registry
9. ✓ E2E Tests        - Tests Cypress
10. ✓ Deploy          - Despliegue a K8s/Producción
```

### Variables de Entorno en Jenkins

```groovy
environment {
    APP_NAME = 'library'
    DOCKER_IMAGE = 'library/app'
    DOCKER_REGISTRY = credentials('docker-registry-url')
    SONAR_TOKEN = credentials('sonar-token')
    K8S_NAMESPACE = 'production'
}
```

---

## 📊 ELK Stack - Logs Centralizados

### Componentes

| Componente      | Puerto | Función                          |
|-----------------|--------|----------------------------------|
| Elasticsearch   | 9200   | Almacenar e indexar logs         |
| Logstash        | 5000   | Procesar y transformar logs      |
| Kibana          | 5601   | Visualizar logs                  |
| Filebeat        | -      | Recolectar archivos de log       |

### Acceso a Kibana

1. **Abrir navegador**: http://localhost:5601
2. **Crear Index Pattern**:
   - Stack Management → Index Patterns
   - Pattern: `library-logs-*`
   - Time field: `@timestamp`
3. **Visualizar Logs**:
   - Analytics → Discover
   - Seleccionar `library-logs-*`

### Queries Útiles en Kibana

```json
# Filtrar por nivel de log
log_level: "ERROR"

# Filtrar por aplicación
application: "library"

# Buscar errores de un usuario
log_level: "ERROR" AND message: *authentication*

# Logs de las últimas 24h con errores
@timestamp:[now-24h TO now] AND log_level: "ERROR"
```

### Estructura de Logs JSON

```json
{
  "@timestamp": "2026-03-07T17:00:00.000Z",
  "app": "library",
  "environment": "production",
  "level": "ERROR",
  "logger_name": "com.sgaraba.library.service.BookService",
  "thread_name": "http-nio-8080-exec-1",
  "message": "Error creating book",
  "stack_trace": "...",
  "requestId": "abc-123-xyz"
}
```

### Exportar Dashboard Kibana

```bash
# Exportar configuración
curl -X POST "localhost:5601/api/saved_objects/_export" \
  -H 'kbn-xsrf: true' \
  -H 'Content-Type: application/json' \
  -d '{"type":"dashboard"}' > dashboard.ndjson

# Importar configuración
curl -X POST "localhost:5601/api/saved_objects/_import" \
  -H 'kbn-xsrf: true' \
  --form file=@dashboard.ndjson
```

---

## 📈 Monitoreo

### Health Checks

```bash
# Application health
curl http://localhost:8080/management/health | jq

# Metrics (Prometheus format)
curl http://localhost:8080/management/prometheus

# Info
curl http://localhost:8080/management/info | jq
```

### Ver Logs en Tiempo Real

```bash
# Logs de la aplicación
tail -f backend/logs/application.log

# Logs JSON para ELK
tail -f backend/logs/application-logstash.json | jq

# Logs de contenedor Docker
docker logs library-app -f --tail 100
```

### Métricas con Prometheus + Grafana (Opcional)

```bash
# Levantar stack completo con monitoring
docker-compose -f src/main/docker/monitoring.yml up -d

# Acceso
# Prometheus: http://localhost:9090
# Grafana: http://localhost:3000 (admin/admin)
```

---

## 🛠️ Troubleshooting

### Problema: Contenedor no arranca

```bash
# Ver logs detallados
docker logs library-app

# Verificar variables de entorno
docker inspect library-app | jq '.[0].Config.Env'

# Entrar al contenedor
docker exec -it library-app sh
```

### Problema: ELK Stack no recibe logs

```bash
# Verificar Filebeat
docker logs library-filebeat

# Verificar Logstash
curl http://localhost:9600/_node/stats | jq

# Ver índices de Elasticsearch
curl http://localhost:9200/_cat/indices?v

# Verificar conectividad
docker exec library-app ping -c 2 logstash
```

### Problema: Build falla en Jenkins

```bash
# Revisar logs de Jenkins
docker logs jenkins -f

# Verificar espacio en disco
df -h

# Limpiar workspace
./mvnw clean
docker system prune -a
```

---

## 🔒 Seguridad

### Mejores Prácticas Implementadas

- ✅ Imágenes base Alpine (menor superficie de ataque)
- ✅ Usuario no-root en contenedor
- ✅ Health checks configurados
- ✅ Secrets en variables de entorno
- ✅ Network isolation (app-network, elk)
- ✅ Logs sin datos sensibles
- ✅ OWASP Dependency Check en pipeline

### Secrets en Producción

```bash
# Usar Docker Secrets (Swarm) o Kubernetes Secrets
echo "my-secret-password" | docker secret create db_password -

# En docker-compose con secrets
services:
  app:
    secrets:
      - db_password
```

---

## 📚 Referencias

- **JHipster**: https://www.jhipster.tech/
- **Docker Best Practices**: https://docs.docker.com/develop/dev-best-practices/
- **Jenkins Pipeline**: https://www.jenkins.io/doc/book/pipeline/
- **ELK Stack**: https://www.elastic.co/guide/index.html
- **12 Factor App**: https://12factor.net/

---

## 📞 Soporte

Para problemas o preguntas:
1. Revisa los logs: `docker logs <container>`
2. Consulta el manual: `docs/MANUAL-CREACION-APLICACION.md`
3. Revisa issues en el repositorio

---

**Actualizado**: 7 de Marzo, 2026
