# 🚀 Guía de Optimización de Recursos Docker

## 📊 Análisis de Consumo

### Versión COMPLETA (docker-compose-full.yml)
```
Container Memory CPU
└─ Elasticsearch    512-700MB   ~100-150%
└─ Logstash         256-350MB   ~50-80%
└─ Kibana           400-600MB   ~40-60%
└─ Filebeat          50-100MB   ~10-20%
└─ App (Spring)     512-600MB   ~50-100%
└─ PostgreSQL       100-200MB   ~20-40%
└─ MailDev           30-60MB    ~5-10%
─────────────────────────────────────────
TOTAL:             ~2-3.5GB    ~300-400%
```

### Versión LITE (docker-compose-lite.yml) ✅
```
Container Memory CPU
└─ App (Spring)     256-384MB   ~50-80%
└─ PostgreSQL       128-256MB   ~15-30%
└─ MailDev           30-60MB    ~5-10%
─────────────────────────────────────────
TOTAL:             ~500-700MB  ~70-120%
```

**Ahorro: ~75% menos RAM y ~70% menos CPU**

---

## 🎯 Uso Rápido

### Opción 1: Script PowerShell (RECOMENDADO)

```powershell
# En tu laptop (versión ligera)
.\docker-lite-manager.ps1 lite

# En tu PC principal (versión completa con ELK)  
.\docker-lite-manager.ps1 full

# Ver consumo en tiempo real
.\docker-lite-manager.ps1 stats

# Detener todo
.\docker-lite-manager.ps1 stop

# Limpiar todo (incluye volúmenes)
.\docker-lite-manager.ps1 clean
```

### Opción 2: Docker Compose Manual

```powershell
# Versión LITE para laptop
docker-compose -f docker-compose-lite.yml up -d

# Versión FULL para PC principal
docker-compose -f docker-compose-full.yml up -d

# Detener
docker-compose -f docker-compose-lite.yml down
```

---

## 🔍 Monitoreo de Recursos

### Ver estadísticas actuales
```powershell
docker stats --no-stream
```

### Monitoreo continuo (actualización en vivo)
```powershell
docker stats
```

### Ver solo nombres, CPU y RAM
```powershell
docker stats --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}"
```

### Identificar el contenedor que más consume
```powershell
# CPU
docker stats --no-stream --format "table {{.Name}}\t{{.CPUPerc}}" | Sort-Object -Property @{Expression={[double]($_ -split '\s+')[1] -replace '%',''}} -Descending

# RAM
docker stats --no-stream --format "table {{.Name}}\t{{.MemUsage}}"
```

---

## ⚙️ Optimizaciones Adicionales para Laptop

### 1. Reducir memoria de la aplicación Spring Boot

Si aún consume mucho, edita `docker-compose-lite.yml`:

```yaml
environment:
  # Cambiar de 256MB a 192MB
  - JAVA_OPTS=-Xmx192m -Xms96m
```

### 2. Solo PostgreSQL (sin app en Docker)

Si tienes Java instalado localmente, corre solo PostgreSQL:

```yaml
# docker-compose-db-only.yml
version: '3.8'
services:
  postgresql:
    image: postgres:16.2-alpine
    container_name: library-postgres
    environment:
      - POSTGRES_DB=library
      - POSTGRES_USER=library
      - POSTGRES_PASSWORD=library
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    deploy:
      resources:
        limits:
          memory: 256M
          cpus: '0.5'

volumes:
  postgres_data:
```

Luego corre la app localmente:
```powershell
.\mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### 3. Desactivar MailDev si no lo necesitas

Comenta o elimina la sección `maildev` en `docker-compose-lite.yml`:

```yaml
  # maildev:
  #   image: maildev/maildev:latest
  #   ...
```

### 4. Usar PostgreSQL sin persistencia (más rápido, menos I/O)

```yaml
postgresql:
  # Comentar volumes para usar RAM
  # volumes:
  #   - postgres_data:/var/lib/postgresql/data
```

---

## 📱 Recomendaciones por Dispositivo

### Laptop (recursos limitados)
```powershell
# Usar versión LITE siempre
.\docker-lite-manager.ps1 lite

# O solo base de datos + app local
docker-compose -f docker-compose-db-only.yml up -d
.\mvnw spring-boot:run
```

**Consumo esperado:** ~100-200MB RAM, ~30-50% CPU (1 core)

### PC Principal (desarrollo completo)
```powershell
# Usar versión FULL para logs y monitoreo avanzado
.\docker-lite-manager.ps1 full
```

**Consumo esperado:** ~2.5-3.5GB RAM, ~300-400% CPU

---

## 🛠️ Comandos Útiles de Diagnóstico

### Ver logs de un contenedor
```powershell
docker logs library-app
docker logs library-postgres
docker logs library-elasticsearch  # Solo en versión FULL
```

### Logs en tiempo real
```powershell
docker logs -f library-app
```

### Inspeccionar recursos de un contenedor específico
```powershell
docker stats library-app
```

### Ver procesos dentro del contenedor
```powershell
docker top library-app
```

### Limpiar recursos no utilizados
```powershell
# Limpiar imágenes, contenedores y redes huérfanas
docker system prune -a

# Ver cuánto espacio se puede liberar
docker system df
```

---

## ⚡ Optimizaciones Avanzadas

### 1. Limitar CPU y RAM en Docker Desktop

Docker Desktop → Settings → Resources:
- **CPUs:** 2-3 cores para lite, 4-6 para full
- **Memory:** 2GB para lite, 6GB para full
- **Swap:** 1GB
- **Disk:** 20GB mínimo

### 2. Configurar WSL 2 (si usas Windows)

Crea/edita `C:\Users\<tu-usuario>\.wslconfig`:

```ini
[wsl2]
memory=4GB          # Para laptop con versión LITE
processors=2        # Número de cores
swap=1GB
localhostForwarding=true
```

Para PC principal con versión FULL:
```ini
[wsl2]
memory=8GB
processors=4
swap=2GB
```

Reinicia WSL:
```powershell
wsl --shutdown
```

### 3. Deshabilitar servicios innecesarios en Windows

Servicios que consumen recursos mientras Docker corre:
- Windows Search (si no lo usas)
- SysMain (Superfetch)
- Windows Update (temporalmente)

---

## 🎓 Buenas Prácticas

### ✅ HACER:
- Usar `docker-compose-lite.yml` en laptop
- Monitorear recursos con `docker stats` regularmente
- Detener contenedores cuando no los uses
- Limpiar imágenes viejas periódicamente
- Usar PostgreSQL Alpine (más ligero)

### ❌ EVITAR:
- Correr el stack ELK en laptop sin suficiente RAM
- Dejar todos los contenedores corriendo 24/7
- Asignar más recursos de los necesarios
- Usar volúmenes sin limpiar periódicamente

---

## 🆘 Troubleshooting

### "Contenedor se reinicia constantemente"
```powershell
# Ver logs
docker logs library-app --tail 100

# Verificar salud
docker inspect library-app | Select-String -Pattern "Health"
```

### "Se queda sin memoria"
```powershell
# Aumentar límites en docker-compose-lite.yml
deploy:
  resources:
    limits:
      memory: 512M  # Aumentar de 384M
```

### "CPU al 100% constantemente"
- Verificar que no esté Elasticsearch corriendo
- Revisar logs de la aplicación
- Reducir workers/threads en Spring Boot

---

## 📞 Comandos de Emergencia

```powershell
# DETENER TODO INMEDIATAMENTE
docker stop $(docker ps -q)

# LIBERAR TODA LA RAM
docker system prune -a --volumes -f

# REINICIAR DOCKER (Windows)
Restart-Service docker
```

---

## 📈 Comparativa Final

| Característica | LITE | FULL |
|----------------|------|------|
| RAM mínima | 2GB | 6GB |
| CPU cores | 2 | 4+ |
| Tiempo inicio | ~30s | ~2min |
| Logs centralizados | ❌ | ✅ |
| Métricas avanzadas | ❌ | ✅ |
| Búsqueda de logs | ❌ | ✅ Kibana |
| Desarrollo local | ✅ | ✅ |
| Debugging | ✅ | ✅ |
| Email testing | ✅ | ✅ |

---

## 🎯 Conclusión

**Para tu laptop:** Usa `docker-compose-lite.yml`
```powershell
.\docker-lite-manager.ps1 lite
```

**Para tu PC principal:** Usa `docker-compose-full.yml`  
```powershell
.\docker-lite-manager.ps1 full
```

**Monitorear siempre:**
```powershell
.\docker-lite-manager.ps1 stats
```

¡Ahora tu laptop no sufrirá! 🎉
