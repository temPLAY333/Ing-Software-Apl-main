# 🌐 Deploy Público - Library Management System

## 📦 ¿Qué es esto?

Esta aplicación está **disponible públicamente** en DockerHub. **Cualquier persona** puede descargarla y ejecutarla en cualquier computadora con Docker instalado.

**NO necesitas:**
- ❌ Código fuente
- ❌ Java, Maven, Node.js instalados
- ❌ Jenkins
- ❌ Compilar nada

**Solo necesitas:**
- ✅ Docker instalado
- ✅ Conexión a internet (solo la primera vez)

---

## 🚀 Descargar y Ejecutar (En 2 comandos)

### **Opción 1: Comando único** (la más fácil)

```bash
# Descargar el docker-compose.yml
curl -o docker-compose.yml https://raw.githubusercontent.com/temPLAY333/Ing-Software-Apl-main/master/backend/docker-compose-public.yml

# Ejecutar
docker-compose up -d
```

**Acceder a:** http://localhost:8080

---

### **Opción 2: Crear archivo manualmente**

1. **Crear archivo `docker-compose.yml`** con este contenido:

```yaml
version: '3.8'

services:
  database:
    image: postgres:16-alpine
    environment:
      POSTGRES_USER: library
      POSTGRES_PASSWORD: library123
      POSTGRES_DB: library
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  app:
    image: templay333/library-app:latest
    depends_on:
      - database
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://database:5432/library
      SPRING_DATASOURCE_USERNAME: library
      SPRING_DATASOURCE_PASSWORD: library123
      SPRING_PROFILES_ACTIVE: prod
    ports:
      - "8080:8080"

volumes:
  postgres_data:
```

2. **Ejecutar:**
```bash
docker-compose up -d
```

3. **Esperar ~30 segundos** (primera vez descarga ~500MB)

4. **Acceder:** http://localhost:8080

---

## 🔐 Usuarios por defecto

| Usuario | Password | Rol |
|---------|----------|-----|
| admin | admin | Administrador |
| user | user | Usuario normal |

---

## 📊 Monitoreo

### Ver logs en tiempo real:
```bash
docker-compose logs -f app
```

### Ver estado:
```bash
docker-compose ps
```

### Health check:
```bash
curl http://localhost:8080/management/health
```

Deberías ver:
```json
{"status":"UP"}
```

---

## 🛑 Detener la aplicación

```bash
# Detener y borrar contenedores (preserva datos)
docker-compose down

# Detener y borrar TODO (incluye base de datos)
docker-compose down -v
```

---

## 🌍 Desplegar en servidor real

### En servidor Linux (AWS, Azure, DigitalOcean, etc.):

```bash
# 1. Instalar Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh

# 2. Instalar Docker Compose
sudo apt-get update
sudo apt-get install docker-compose-plugin

# 3. Crear directorio
mkdir library-app && cd library-app

# 4. Descargar docker-compose
curl -o docker-compose.yml https://raw.githubusercontent.com/temPLAY333/Ing-Software-Apl-main/master/backend/docker-compose-public.yml

# 5. Ejecutar
docker-compose up -d

# 6. Acceder desde navegador
http://<IP-DEL-SERVIDOR>:8080
```

### Abrir puerto en firewall:
```bash
# Ubuntu/Debian
sudo ufw allow 8080

# CentOS/RHEL
sudo firewall-cmd --add-port=8080/tcp --permanent
sudo firewall-cmd --reload
```

---

## 🔧 Configuración Avanzada

### Cambiar puerto (ej: puerto 80):
```yaml
services:
  app:
    ports:
      - "80:8080"  # ← Acceder en http://localhost (sin :8080)
```

### Usar base de datos externa:
```yaml
services:
  app:
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db-externo.com:5432/library
      SPRING_DATASOURCE_USERNAME: usuario
      SPRING_DATASOURCE_PASSWORD: password
```

### Agregar SSL/HTTPS:
```yaml
services:
  nginx:
    image: nginx:alpine
    ports:
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
      - ./ssl:/etc/nginx/ssl
```

---

## 📈 Prueba de Carga

### Con varios usuarios simultáneos:
```bash
# Instalar Apache Bench
sudo apt-get install apache2-utils

# 1000 requests, 10 concurrentes
ab -n 1000 -c 10 http://localhost:8080/
```

---

## 🆕 Actualizar a nueva versión

Cuando haya una nueva versión en DockerHub:

```bash
# 1. Descargar última versión
docker-compose pull

# 2. Recrear contenedor
docker-compose up -d
```

---

## ❓ Troubleshooting

### ⚠️ "Error: address already in use"
```bash
# Puerto 8080 ocupado - verificar qué lo usa
netstat -ano | findstr :8080    # Windows
lsof -i :8080                    # Linux/Mac

# Cambiar puerto en docker-compose.yml:
ports:
  - "8081:8080"  # ← Usa 8081 en vez de 8080
```

### ⚠️ "Connection refused to database"
```bash
# Esperar más tiempo (base de datos tarda en iniciar)
docker-compose logs database

# Ver si está healthy
docker-compose ps
```

### ⚠️ "Cannot pull image"
```bash
# Verificar conexión a internet
ping hub.docker.com

# Login a DockerHub (si la imagen es privada)
docker login
```

### ⚠️ "Application failed to start"
```bash
# Ver logs completos
docker-compose logs app

# Verificar variables de entorno
docker-compose config
```

---

## 📝 Notas Importantes

1. **Primera ejecución:** Descarga ~500MB (PostgreSQL + App)
2. **Siguientes ejecuciones:** Instantáneo (ya está descargado)
3. **Datos:** Persisten en volume `postgres_data` (sobrevive a `docker-compose down`)
4. **Seguridad:** Cambiar passwords en producción
5. **Backup:** `docker-compose exec database pg_dump -U library > backup.sql`

---

## 🔗 Links Útiles

- **DockerHub:** https://hub.docker.com/r/templay333/library-app
- **GitHub:** https://github.com/temPLAY333/Ing-Software-Apl-main
- **Documentación:** Ver README.md en el repositorio

---

## 📞 Soporte

Si tienes problemas:
1. Revisar logs: `docker-compose logs app`
2. Verificar health: `curl http://localhost:8080/management/health`
3. Recrear contenedores: `docker-compose down && docker-compose up -d`

---

## ✅ Verificar que funciona

Después de ejecutar `docker-compose up -d`:

1. **Esperar 30 segundos**

2. **Verificar estado:**
   ```bash
   docker-compose ps
   ```
   Deberías ver:
   ```
   NAME         STATUS         PORTS
   library-app  Up (healthy)   0.0.0.0:8080->8080/tcp
   library-db   Up (healthy)   0.0.0.0:5432->5432/tcp
   ```

3. **Verificar API:**
   ```bash
   curl http://localhost:8080/management/health
   ```
   Respuesta esperada: `{"status":"UP"}`

4. **Abrir navegador:**
   http://localhost:8080
   
   Deberías ver la pantalla de login de la aplicación

5. **Login:**
   - Usuario: `admin`
   - Password: `admin`

¡Listo! 🎉
