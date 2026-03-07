# 🐳 Gestión de Contenedores Library

## Estado Actual
Todos los contenedores están organizados bajo el proyecto **"library"** y aparecerán agrupados en Docker Desktop.

## Servicios Disponibles

| Servicio | Puerto | URL |
|----------|--------|-----|
| Aplicación Spring Boot | 8080 | http://localhost:8080 |
| PostgreSQL | 5432 | localhost:5432 |
| Elasticsearch | 9200, 9300 | http://localhost:9200 |
| Logstash | 5000, 9600 | http://localhost:9600 |
| Kibana | 5601 | http://localhost:5601 |
| Filebeat | - | (recolector de logs) |

## Comandos Básicos

### Usando el script de gestión (Recomendado)
```powershell
# Ver estado
.\docker-manager.ps1 status

# Iniciar todo
.\docker-manager.ps1 up

# Detener todo
.\docker-manager.ps1 down

# Reiniciar
.\docker-manager.ps1 restart

# Ver logs de la aplicación
.\docker-manager.ps1 logs

# Limpiar todo (incluye volúmenes)
.\docker-manager.ps1 clean
```

### Usando docker-compose directamente
```powershell
# Iniciar todos los servicios
docker-compose -f docker-compose-full.yml up -d

# Ver estado
docker-compose -f docker-compose-full.yml ps

# Ver logs
docker-compose -f docker-compose-full.yml logs -f app

# Detener todo
docker-compose -f docker-compose-full.yml down

# Detener y eliminar volúmenes
docker-compose -f docker-compose-full.yml down -v
```

## Orden de Inicio Automático
Los servicios se inician en el orden correcto automáticamente:
1. PostgreSQL
2. Elasticsearch
3. Logstash (depende de Elasticsearch)
4. Kibana (depende de Elasticsearch)
5. Filebeat (depende de Logstash)
6. Aplicación (depende de PostgreSQL y Logstash)

## Archivo .env
El archivo `.env` contiene la variable `COMPOSE_PROJECT_NAME=library`, lo que asegura que todos los contenedores se agrupen bajo el mismo proyecto sin necesidad de especificar `--project-name` cada vez.

## Solución de Problemas

### Ver logs de un servicio específico
```powershell
docker logs library-app -f
docker logs library-postgres -f
docker logs library-elasticsearch -f
```

### Reiniciar un servicio específico
```powershell
docker-compose -f docker-compose-full.yml restart app
docker-compose -f docker-compose-full.yml restart postgresql
```

### Rebuild de la imagen
```powershell
# 1. Reconstruir el JAR
.\mvnw clean package -DskipTests

# 2. Reconstruir la imagen Docker
docker-compose -f docker-compose-full.yml build app

# 3. Reiniciar el servicio
docker-compose -f docker-compose-full.yml up -d app
```

## Notas Importantes
- **No uses `docker run` directamente** - siempre usa docker-compose para mantener la organización
- El archivo `.env` asegura que el proyecto se llame "library"
- Los healthchecks aseguran que los servicios se inicien en el orden correcto
- Los logs de la aplicación se envían automáticamente a Kibana para análisis
