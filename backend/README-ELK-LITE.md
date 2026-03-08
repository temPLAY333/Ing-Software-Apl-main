# 🚀 Guía Rápida - ELK con Recursos Limitados

## 📊 Situación Actual

```
Consumo AHORA (ELK Full):
RAM: ~3.8GB
CPU: ~320%
```

**Problema:** Tu laptop no tiene suficientes recursos  
**Requerimiento:** Necesitas mostrar ELK funcionando

---

## ✅ SOLUCIONES DISPONIBLES

### 1️⃣ ELK LITE (RECOMENDADO) - Todo en tu laptop

```powershell
cd backend
.\docker-lite-manager.ps1 elk-lite
```

**Consumo:** ~1.5GB RAM, ~150% CPU (60% menos)  
**Ventaja:** Todo local, autónomo, funcional  
**Ideal para:** Presentación en 1 dispositivo

---

### 2️⃣ ELK REMOTO - PC Principal + Laptop

**En el PC Principal:**
```powershell
cd backend
.\configurar-elk-remoto.ps1  # Sigue el asistente
```

**En la Laptop:**
```powershell
cd backend
.\configurar-elk-remoto.ps1  # Sigue el asistente
```

**Consumo laptop:** ~600MB RAM, ~80% CPU (84% menos)  
**Ventaja:** Laptop ultra ligera  
**Ideal para:** Presentación con 2 dispositivos en la misma red

---

### 3️⃣ MODO PRESENTACIÓN - Un solo click

```powershell
cd backend
.\modo-presentacion.ps1
```

**Qué hace:**
- Detiene todo
- Inicia ELK Lite
- Genera logs de prueba
- Verifica todo funcione
- Te deja listo en 2 minutos

**Ideal para:** Preparación rápida antes de presentar

---

## 📋 Comparativa Rápida

| Solución | RAM Laptop | Complejidad | Autonomía |
|----------|------------|-------------|-----------|
| **ELK Lite** | 1.5GB | ⭐ Baja | ✅ Total |
| **ELK Remoto** | 600MB | ⭐⭐ Media | ⚠️  Depende red |
| **ELK Full** | 3.8GB | ⭐ Baja | ✅ Total |

---

## 🎯 ¿Cuál usar?

### Tienes solo tu laptop
```powershell
.\docker-lite-manager.ps1 elk-lite
```

### Tienes laptop + PC principal
```powershell
# PC: 
.\configurar-elk-remoto.ps1  # Opción 1 (Servidor)

# Laptop:
.\configurar-elk-remoto.ps1  # Opción 2 (Cliente)
```

### Presentas en 5 minutos
```powershell
.\modo-presentacion.ps1
```

---

## 📁 Archivos Creados

```
backend/
├── docker-compose-elk-lite.yml        # ELK optimizado (1.5GB)
├── docker-compose-elk-remote.yml      # Cliente remoto (600MB)
├── docker-compose-elk-server.yml      # Servidor remoto (PC)
├── docker-lite-manager.ps1            # Gestor principal
├── configurar-elk-remoto.ps1          # Configurador automático
└── modo-presentacion.ps1              # Preparación rápida

docs/
├── GUIA-ELK-PRESENTACIONES.md         # Guía completa
└── OPTIMIZACION-RECURSOS.md           # Optimizaciones detalladas
```

---

## 🆘 Comandos Útiles

```powershell
# Ver consumo
.\docker-lite-manager.ps1 stats

# Detener todo
.\docker-lite-manager.ps1 stop

# Ver logs
docker logs library-kibana --tail 50
docker logs library-elasticsearch --tail 50

# Reiniciar servicio
docker restart library-kibana

# Limpiar todo
.\docker-lite-manager.ps1 clean
```

---

## 🎓 Recomendación Final

### Para presentación en tu laptop:

```powershell
# Opción más simple
.\modo-presentacion.ps1
```

### O si prefieres control manual:

```powershell
.\docker-lite-manager.ps1 elk-lite
```

**Resultado esperado:**
- ✅ ~1.5GB RAM (60% menos)
- ✅ ~150% CPU (53% menos)
- ✅ ELK funcional
- ✅ Listo para presentar

---

## 📖 Documentación Completa

Lee [GUIA-ELK-PRESENTACIONES.md](../docs/GUIA-ELK-PRESENTACIONES.md) para:
- Troubleshooting detallado
- Optimizaciones adicionales
- Checklist pre-presentación
- Tips profesionales

---

## 💡 Ejemplo de Uso (Día a Día)

```powershell
# Desarrollo normal (sin ELK) - Lunes a Viernes
.\docker-lite-manager.ps1 lite
# Consumo: ~600MB

# Día de presentación
.\modo-presentacion.ps1
# Todo listo en 2 minutos

# Después de presentar
.\docker-lite-manager.ps1 lite
# Volver a desarrollo normal
```

---

¿Dudas? Lee la [guía completa](../docs/GUIA-ELK-PRESENTACIONES.md) 📚
