# 🎯 Guía Completa: ELK Stack para Presentaciones con Recursos Limitados

## 📋 Resumen Ejecutivo

Tienes **4 soluciones** para presentar ELK en tu laptop sin que colapse:

| Solución | Dónde | Consumo | Mejor para |
|----------|-------|---------|------------|
| **ELK Lite** | Todo en laptop | ~1.5-2GB RAM | ✅ **RECOMENDADO** - Presentación en 1 dispositivo |
| **ELK Remoto** | PC + Laptop | ~600MB (laptop) | Presentación en 2 dispositivos (misma red) |
| **ELK Optimizado Full** | Laptop | ~2.5GB RAM | Laptop con buenos recursos |
| **Demo Temporal** | Laptop | Variable | Solo encender ELK al presentar |

---

## 🚀 SOLUCIÓN 1: ELK LITE (Recomendada)

### ¿Qué es?
Stack ELK completo OPTIMIZADO que consume **50% menos recursos** que la versión normal.

### Consumo de recursos
```
Componente          Original    ELK Lite    Ahorro
─────────────────────────────────────────────────
Elasticsearch       512MB       256MB       -50%
Logstash            256MB       128MB       -50%
Kibana              600MB       512MB       -15%
Filebeat            100MB       -           -100% (removido)
App                 512MB       256MB       -50%
PostgreSQL          150MB       128MB       -15%
─────────────────────────────────────────────────
TOTAL:              ~2.1GB      ~1.3GB      ~38% menos
CPU:                ~320%       ~150-200%   ~50% menos
```

### ✅ Ventajas
- Todo funciona localmente (no necesitas internet ni red)
- Autónomo para presentaciones
- Funcionalidad completa de ELK
- ~1.5GB RAM total

### ⚠️ Desventajas
- Puede ser lento en búsquedas grandes
- Kibana tarda ~60-90s en iniciar
- Indexación más lenta

### 📝 Cómo usar

```powershell
# 1. Ir al directorio backend
cd backend

# 2. Iniciar ELK Lite
.\docker-lite-manager.ps1 elk-lite

# 3. Esperar ~90 segundos para que todo inicie

# 4. Verificar que todo esté funcionando
.\docker-lite-manager.ps1 stats
```

### 🎨 Accesos durante la presentación

```
✅ Aplicación:     http://localhost:8080
✅ Kibana:         http://localhost:5601
✅ Elasticsearch:  http://localhost:9200
✅ PostgreSQL:     localhost:5432
```

### 🎓 Tips para la presentación

1. **Inicia 5 minutos antes**: Kibana tarda en arrancar
2. **Pre-configura dashboards**: Ten visualizaciones listas en Kibana
3. **Genera logs de prueba**: Haz requests a la app antes de presentar
4. **Ten estadísticas abiertas**: `docker stats` en otra terminal se ve profesional

---

## 🌐 SOLUCIÓN 2: ELK REMOTO (2 Dispositivos)

### ¿Qué es?
ELK corre en tu PC principal (en casa/universidad), tu laptop solo corre la app.

### Arquitectura
```
┌─────────────────────┐          ┌──────────────────────┐
│   PC PRINCIPAL      │   WiFi   │      LAPTOP          │
│   (Servidor)        │◄────────►│    (Cliente)         │
├─────────────────────┤          ├──────────────────────┤
│ - Elasticsearch     │          │ - Aplicación         │
│ - Logstash          │          │ - PostgreSQL         │
│ - Kibana            │          │                      │
│ - Filebeat          │          │ Consumo: ~600MB      │
│                     │          │ CPU: ~80%            │
│ Consumo: ~2.5GB     │          │                      │
│ CPU: ~250%          │          │                      │
└─────────────────────┘          └──────────────────────┘
```

### ✅ Ventajas
- Laptop consume **MUCHO** menos
- ELK completo sin compromisos
- Puedes apagar ELK del laptop (más autonomía batería)

### ⚠️ Desventajas
- Necesitas 2 dispositivos
- Ambos en la misma red WiFi
- Configuración inicial más compleja
- Dependes de la conectividad

### 📝 Cómo configurar

#### PASO 1: En el PC Principal (Servidor)

```powershell
cd backend

# Obtener tu IP local
ipconfig
# Busca "IPv4 Address" de tu adaptador WiFi/Ethernet
# Ejemplo: 192.168.1.100

# Iniciar servidor ELK
.\docker-lite-manager.ps1 elk-server
```

**Configurar firewall de Windows:**
```powershell
# Permitir puertos ELK en el firewall
New-NetFirewallRule -DisplayName "ELK Elasticsearch" -Direction Inbound -LocalPort 9200 -Protocol TCP -Action Allow
New-NetFirewallRule -DisplayName "ELK Kibana" -Direction Inbound -LocalPort 5601 -Protocol TCP -Action Allow
New-NetFirewallRule -DisplayName "ELK Logstash" -Direction Inbound -LocalPort 5000 -Protocol TCP -Action Allow
```

#### PASO 2: En la Laptop (Cliente)

```powershell
cd backend

# Editar archivo de configuración
notepad docker-compose-elk-remote.yml

# Buscar la sección "extra_hosts" y reemplazar:
# extra_hosts:
#   - "elasticsearch:192.168.1.100"  # <- Cambiar por la IP del servidor
#   - "logstash:192.168.1.100"
#   - "kibana:192.168.1.100"

# Guardar y cerrar

# Iniciar cliente
.\docker-lite-manager.ps1 elk-remote
```

#### PASO 3: Verificar conexión

Desde la laptop:
```powershell
# Probar Elasticsearch (reemplaza IP)
curl http://192.168.1.100:9200

# Deberías ver algo como:
# {
#   "name" : "library-elasticsearch",
#   "cluster_name" : "docker-cluster",
#   ...
# }
```

### 🎨 Accesos durante la presentación

**En el PC Principal:**
```
Kibana: http://localhost:5601
o
Kibana: http://TU_IP:5601  (ej: http://192.168.1.100:5601)
```

**En la Laptop:**
```
Aplicación: http://localhost:8080
Kibana (remoto): http://IP_PC_PRINCIPAL:5601
```

### 🎓 Tips para la presentación

1. **Prueba antes**: Conecta ambos dispositivos 1 día antes
2. **Misma WiFi**: Asegúrate de estar en la misma red
3. **IP estática**: Configura IP fija en el PC principal para que no cambie
4. **Hotspot móvil**: Si la WiFi falla, usa hotspot de tu celular

---

## ⚡ SOLUCIÓN 3: Optimizaciones Adicionales

### Reducir aún más la memoria de ELK Lite

Edita [docker-compose-elk-lite.yml](backend/docker-compose-elk-lite.yml):

```yaml
# Elasticsearch: 256MB → 192MB
environment:
  - "ES_JAVA_OPTS=-Xms192m -Xmx192m"

# Logstash: 128MB → 96MB
environment:
  - "LS_JAVA_OPTS=-Xmx96m -Xms96m"

# App: 256MB → 192MB
environment:
  - JAVA_OPTS=-Xmx192m -Xms96m
```

**Resultado:** ~1.2GB RAM total (pero más lento)

### Remover MailDev (ahorro de ~50MB)

Comenta la sección maildev en el docker-compose:

```yaml
# maildev:
#   image: maildev/maildev:latest
#   ...
```

### Configurar límites de WSL 2 (Windows)

Crea `C:\Users\<tu-usuario>\.wslconfig`:

```ini
[wsl2]
memory=3GB         # Límite para Docker Desktop
processors=2
swap=512MB
localhostForwarding=true
```

Reinicia WSL:
```powershell
wsl --shutdown
```

---

## 🎬 SOLUCIÓN 4: Modo Demo Temporal

### Estrategia
Usa ELK solo cuando vas a presentar, desarróllala sin ELK normalmente.

### Workflow diario

```powershell
# Lunes a Viernes: Desarrollo sin ELK
.\docker-lite-manager.ps1 lite
# Consumo: ~600MB

# Día de presentación: Iniciar ELK
.\docker-lite-manager.ps1 elk-lite
# Consumo: ~1.5GB

# Después de presentar: Volver a lite
.\docker-lite-manager.ps1 lite
```

### Script de "Modo Presentación"

Crea `modo-presentacion.ps1`:

```powershell
Write-Host "🎬 MODO PRESENTACIÓN ACTIVADO" -ForegroundColor Yellow
Write-Host ""

# Detener todo
docker-compose -f docker-compose-lite.yml down 2>$null
docker-compose -f docker-compose-full.yml down 2>$null

# Iniciar ELK Lite
Write-Host "Iniciando ELK Stack..." -ForegroundColor Cyan
docker-compose -f docker-compose-elk-lite.yml up -d

# Esperar que inicie
Write-Host "Esperando que los servicios estén listos..." -ForegroundColor Yellow
Start-Sleep -Seconds 90

# Generar logs de prueba
Write-Host "Generando logs de ejemplo..." -ForegroundColor Cyan
for ($i = 1; $i -le 20; $i++) {
    Invoke-WebRequest -Uri "http://localhost:8080/api/books" -Method GET 2>$null
    Start-Sleep -Milliseconds 500
}

Write-Host ""
Write-Host "✅ TODO LISTO PARA PRESENTAR" -ForegroundColor Green
Write-Host "  - Aplicación: http://localhost:8080" -ForegroundColor White
Write-Host "  - Kibana: http://localhost:5601" -ForegroundColor White
Write-Host ""
Write-Host "🎯 Abre Kibana y configura tus dashboards" -ForegroundColor Yellow
```

---

## 📊 Comparativa Final

| Solución | RAM | CPU | Complejidad | Autonomía | Mejor para |
|----------|-----|-----|-------------|-----------|------------|
| **ELK Lite** | 1.5GB | 150% | Baja | ✅ | Una laptop sola |
| **ELK Remoto** | 600MB | 80% | Media | ⚠️  Depende de red | Dos dispositivos |
| **ELK Full Optimizado** | 2.5GB | 250% | Baja | ✅ | Laptop potente |
| **Demo Temporal** | Variable | Variable | Baja | ✅ | Iniciar solo al presentar |

---

## 🔧 Troubleshooting

### "Kibana no inicia / error 503"

**Causa:** Kibana necesita más tiempo o Elasticsearch no está listo

**Solución:**
```powershell
# Esperar 60 segundos más
Start-Sleep -Seconds 60

# Verificar Elasticsearch
curl http://localhost:9200

# Reiniciar solo Kibana
docker restart library-kibana
```

### "Out of memory"

**Causa:** No hay suficiente RAM disponible

**Solución:**
```powershell
# Ver qué está consumiendo
docker stats

# Cerrar aplicaciones innecesarias (Chrome, VS Code, etc.)

# O usar versión más ligera
.\docker-lite-manager.ps1 lite  # Sin ELK
```

### "No se conecta al ELK remoto"

**Causa:** Firewall bloqueando o IP incorrecta

**Solución:**
```powershell
# Verificar IP del servidor
ipconfig

# Probar conectividad
ping IP_DEL_SERVIDOR

# Test de puerto
Test-NetConnection -ComputerName IP_DEL_SERVIDOR -Port 9200

# Desactivar firewall temporalmente (solo para pruebas)
Set-NetFirewallProfile -Profile Domain,Public,Private -Enabled False
```

### "Elasticsearch crashea constantemente"

**Causa:** No hay suficiente RAM para 256MB

**Solución:**
```yaml
# Reducir a 192MB en docker-compose-elk-lite.yml
environment:
  - "ES_JAVA_OPTS=-Xms192m -Xmx192m"
```

---

## 💡 Recomendación Final

### Para la presentación en tu laptop:

```powershell
# OPCIÓN RECOMENDADA: ELK Lite
.\docker-lite-manager.ps1 elk-lite
```

**Por qué:**
- ✅ Funciona sin internet/red
- ✅ Todo autónomo
- ✅ ~1.5GB RAM (manejable)
- ✅ Funcionalidad completa
- ✅ Simple de usar

### Para el día a día (desarrollo):

```powershell
# Versión ligera sin ELK
.\docker-lite-manager.ps1 lite
```

**Por qué:**
- ✅ Solo 600MB RAM
- ✅ Suficiente para desarrollar
- ✅ ELK no es necesario para codear

---

## 🎯 Checklist Pre-Presentación

### 1 Semana Antes
- [ ] Probar ELK Lite en la laptop
- [ ] Verificar que Kibana inicie correctamente
- [ ] Crear dashboards y visualizaciones
- [ ] Guardar queries importantes

### 1 Día Antes
- [ ] Limpiar Docker: `.\docker-lite-manager.ps1 clean`
- [ ] Probar inicio completo
- [ ] Cronometrar tiempo de inicio (~90s)
- [ ] Generar datos de prueba

### 30 Minutos Antes
- [ ] Cerrar aplicaciones innecesarias (Chrome tabs, VS Code extra, etc.)
- [ ] Iniciar ELK: `.\docker-lite-manager.ps1 elk-lite`
- [ ] Verificar Kibana funciona
- [ ] Tener backup: capturas de pantalla de Kibana

### Durante la Presentación
- [ ] Mostrar `docker stats` (se ve profesional)
- [ ] Tener terminal con logs abierto: `docker logs -f library-app`
- [ ] Explicar las optimizaciones que hiciste

---

## 📞 Contacto Rápido

### Ver estado actual
```powershell
.\docker-lite-manager.ps1 stats
```

### Ver logs
```powershell
docker logs library-elasticsearch --tail 50
docker logs library-kibana --tail 50
docker logs library-app --tail 50
```

### Reiniciar servicio específico
```powershell
docker restart library-kibana
docker restart library-elasticsearch
```

### Emergencia: Reiniciar todo
```powershell
.\docker-lite-manager.ps1 stop
.\docker-lite-manager.ps1 elk-lite
```

---

¡Buena suerte en tu presentación! 🚀
