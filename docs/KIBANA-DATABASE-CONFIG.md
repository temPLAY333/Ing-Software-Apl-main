# Configuración de Kibana y Base de Datos

## 🔧 Problema de Kibana - Fleet/Integrations

### Causa del Error
El error `Required kibana privilege ['Fleet', 'Integrations'] is missing` ocurre porque:
- Elasticsearch tiene seguridad deshabilitada (`xpack.security.enabled=false`)
- Fleet/Integrations requieren privilegios específicos que solo funcionan con autenticación habilitada
- No hay usuarios ni roles configurados

### Solución Aplicada
He actualizado la configuración de Kibana en `docker-compose-full.yml` para:
1. Habilitar Fleet: `XPACK_FLEET_ENABLED=true`
2. Deshabilitar seguridad (modo desarrollo): `XPACK_SECURITY_ENABLED=false`
3. Agregar clave de encriptación para objetos guardados

### Pasos para Aplicar los Cambios

```powershell
# 1. Detén los contenedores actuales
docker-compose -f docker-compose-full.yml down

# 2. Elimina los volúmenes de Elasticsearch (para limpiar configuración anterior)
docker volume rm library_es_data

# 3. Inicia nuevamente el stack completo
docker-compose -f docker-compose-full.yml up -d

# 4. Verifica que Kibana esté corriendo
docker logs library-kibana

# 5. Accede a Kibana
# http://localhost:5601
```

### Alternativa: Habilitar Seguridad Completa

Si prefieres habilitar seguridad (recomendado para producción):

1. **Actualiza Elasticsearch** en `docker-compose-full.yml`:
```yaml
elasticsearch:
  environment:
    - xpack.security.enabled=true
    - ELASTIC_PASSWORD=changeme
```

2. **Actualiza Kibana**:
```yaml
kibana:
  environment:
    - ELASTICSEARCH_USERNAME=elastic
    - ELASTICSEARCH_PASSWORD=changeme
    - XPACK_SECURITY_ENABLED=true
```

3. **Configura usuarios y roles**:
```bash
# Accede al contenedor de Elasticsearch
docker exec -it library-elasticsearch bash

# Crea un usuario con privilegios Fleet
elasticsearch-users useradd kibana_user -p password -r kibana_system,fleet_admin

# Sal del contenedor
exit
```

---

## 📊 Configuración de Base de Datos PostgreSQL

### Características de la BD

| Parámetro | Valor |
|-----------|-------|
| **Motor** | PostgreSQL 16.2-alpine |
| **Host** | `localhost` (local) o `postgresql` (en Docker) |
| **Puerto** | `5432` |
| **Base de Datos** | `library` |
| **Usuario** | `library` |
| **Contraseña** | `library` |
| **Pool de Conexiones** | HikariCP |

### Cadenas de Conexión

**JDBC (para aplicaciones Java):**
```
jdbc:postgresql://localhost:5432/library
```

**URI estándar PostgreSQL:**
```
postgresql://library:library@localhost:5432/library
```

**Docker (desde otros contenedores):**
```
postgresql://library:library@postgresql:5432/library
```

---

## 🖥️ Visualización en Tiempo Real

### 1. **DBeaver** (Recomendado - Gratis y Potente)

**Instalación:**
```powershell
# Con Chocolatey
choco install dbeaver

# O descargar desde: https://dbeaver.io/download/
```

**Configuración de Conexión:**
1. Nueva Conexión → PostgreSQL
2. Completa los datos:
   - **Host:** `localhost`
   - **Puerto:** `5432`
   - **Base de datos:** `library`
   - **Usuario:** `library`
   - **Contraseña:** `library`
3. Click en "Test Connection"
4. Click en "Finish"

**Características en Tiempo Real:**
- Auto-refresh de tablas (F5 o Click derecho → Refresh)
- Vista de datos en vivo
- Editor SQL con ejecución inmediata
- Monitor de sesiones y actividad
- Visualización de relaciones ER

### 2. **pgAdmin 4** (Interfaz Web Oficial)

**Instalación con Docker:**
```powershell
docker run -d `
  --name pgadmin `
  -p 5050:80 `
  -e "PGADMIN_DEFAULT_EMAIL=admin@admin.com" `
  -e "PGADMIN_DEFAULT_PASSWORD=admin" `
  --network app-network `
  dpage/pgadmin4
```

**Acceso:**
- URL: http://localhost:5050
- Email: `admin@admin.com`
- Password: `admin`

**Agregar Servidor:**
1. Click derecho en "Servers" → Create → Server
2. Pestaña "General":
   - Name: `Library Database`
3. Pestaña "Connection":
   - Host: `library-postgres` (nombre del contenedor)
   - Port: `5432`
   - Database: `library`
   - Username: `library`
   - Password: `library`

### 3. **DataGrip** (JetBrains - Pago pero Potente)

- Configuración similar a DBeaver
- Excelente para desarrollo profesional
- Trial de 30 días disponible

### 4. **psql** (Cliente de Línea de Comandos)

**Conexión directa:**
```powershell
# Desde tu máquina local (si tienes PostgreSQL instalado)
psql -h localhost -p 5432 -U library -d library

# Desde el contenedor Docker
docker exec -it library-postgres psql -U library -d library
```

**Comandos útiles:**
```sql
-- Listar tablas
\dt

-- Describir tabla
\d nombre_tabla

-- Ver datos en tiempo real (actualiza cada 2 segundos)
\watch 2
SELECT * FROM nombre_tabla;

-- Detener el watch
\watch

-- Salir
\q
```

### 5. **Visual Studio Code Extension**

**Extensión:** PostgreSQL by Chris Kolkman

**Instalación:**
```powershell
# O desde la paleta de comandos: Ctrl+P
ext install ckolkman.vscode-postgres
```

**Conexión:**
1. Click en el ícono de PostgreSQL en la barra lateral
2. "+" para nueva conexión
3. Completa los datos de conexión

---

## 📈 Monitoreo en Tiempo Real

### Consultas SQL para Monitoreo

```sql
-- Ver conexiones activas
SELECT pid, usename, application_name, client_addr, state, query
FROM pg_stat_activity
WHERE datname = 'library';

-- Ver tamaño de la base de datos
SELECT pg_size_pretty(pg_database_size('library'));

-- Ver tablas más grandes
SELECT schemaname, tablename, 
       pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables
WHERE schemaname NOT IN ('pg_catalog', 'information_schema')
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC
LIMIT 10;

-- Ver actividad en tiempo real
SELECT now() as timestamp,
       (SELECT count(*) FROM pg_stat_activity WHERE state = 'active') as active_queries,
       (SELECT count(*) FROM pg_stat_activity WHERE state = 'idle') as idle_connections;
```

### Configurar Auto-Refresh en DBeaver

1. Abre una tabla
2. Click derecho en la pestaña de datos
3. Selecciona "Auto-refresh" → Configura el intervalo (ej: 5 segundos)

---

## 🚀 Verificación de Configuración

```powershell
# Verificar que PostgreSQL esté corriendo
docker ps | Select-String "postgres"

# Ver logs de PostgreSQL
docker logs library-postgres

# Verificar conectividad
docker exec library-postgres pg_isready -U library

# Test de conexión desde PowerShell
# (requiere tener psql instalado localmente)
& "C:\Program Files\PostgreSQL\16\bin\psql.exe" -h localhost -p 5432 -U library -d library -c "SELECT version();"
```

---

## 📝 Notas Adicionales

### Configuración de Persistencia
Los datos se guardan en el volumen Docker `postgres_data`:
```powershell
# Ver información del volumen
docker volume inspect library_postgres_data

# Hacer backup del volumen
docker run --rm -v library_postgres_data:/data -v ${PWD}:/backup alpine tar czf /backup/postgres_backup.tar.gz /data
```

### Variables de Entorno
Todas las configuraciones están ahora en el archivo `.env` para fácil referencia y modificación.

### Seguridad
⚠️ **IMPORTANTE**: Las contraseñas actuales son para desarrollo. En producción:
- Cambia todas las contraseñas
- Habilita SSL/TLS
- Configura autenticación segura
- Usa secretos de Docker/Kubernetes
