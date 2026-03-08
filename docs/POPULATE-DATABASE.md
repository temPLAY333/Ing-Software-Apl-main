# 📚 Script de Población de Base de Datos - Library

Este documento explica cómo usar los scripts para llenar la base de datos con datos realistas.

## 📋 Contenido

Se han creado los siguientes archivos:

1. **`populate-database.sql`** - Script SQL con datos realistas
2. **`populate-database.ps1`** - Script PowerShell para ejecutar el SQL fácilmente
3. **Archivos CSV actualizados** en `src/main/resources/config/liquibase/fake-data/`:
   - `client.csv` - 15 clientes realistas
   - `author.csv` - 20 autores (literatura en español y técnica)
   - `publisher.csv` - 15 editoriales reconocidas
   - `book.csv` - 25 libros (literatura + libros técnicos de programación)
   - `borrowed_book.csv` - 15 préstamos a diferentes clientes
   - `rel_book__author.csv` - Relaciones libro-autor

## 🚀 Métodos de Población

### Método 1: Usando el Script PowerShell (Recomendado)

#### Para Docker (Recomendado):
```powershell
# Ejecutar el script hacia el contenedor Docker
.\populate-database.ps1 -Target docker

# Con verificación automática de los datos
.\populate-database.ps1 -Target docker -Verify
```

#### Para PostgreSQL Local:
```powershell
# Si tienes PostgreSQL instalado localmente
.\populate-database.ps1 -Target local -Verify
```

### Método 2: Ejecutar SQL Directamente

#### Desde Docker:
```powershell
# Copiar el archivo al contenedor
docker cp populate-database.sql library-postgres:/tmp/

# Ejecutar el script
docker exec -it library-postgres psql -U library -d library -f /tmp/populate-database.sql

# Verificar los datos
docker exec -it library-postgres psql -U library -d library -c "SELECT COUNT(*) FROM borrowed_book;"
```

#### Desde PostgreSQL Local (psql):
```powershell
# Si tienes psql instalado
$env:PGPASSWORD = "library"
psql -h localhost -p 5432 -U library -d library -f populate-database.sql
```

### Método 3: Usando Liquibase (Automático al Iniciar la App)

Los archivos CSV mejorados se cargarán automáticamente cuando inicies la aplicación en modo desarrollo:

```powershell
# Desde el directorio backend/
.\mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

**Nota:** La aplicación debe tener el perfil `dev` y el contexto de Liquibase debe incluir `faker`:

```yaml
# En application-dev.yml
spring:
  liquibase:
    contexts: dev, faker
```

## 📊 Datos Incluidos

### Clientes (15 personas)
- **NO son usuarios del sistema**
- Son personas que toman libros prestados
- Incluyen: Carlos Fernández, María González, Juan Rodríguez, etc.
- Cada uno con email, dirección y teléfono

### Autores (20 autores)
**Literatura en Español (10):**
- Gabriel García Márquez
- Isabel Allende
- Jorge Luis Borges
- Julio Cortázar
- Mario Vargas Llosa
- Miguel de Cervantes
- Federico García Lorca
- Pablo Neruda
- Octavio Paz
- Carlos Fuentes

**Libros Técnicos de Programación (10):**
- Robert Martin (Clean Code, Clean Coder, Clean Architecture)
- Martin Fowler (Refactoring, Patterns of Enterprise)
- Gang of Four (Design Patterns)
- Kent Beck (TDD)
- Eric Evans (DDD)
- Joshua Bloch (Effective Java)
- Andrew Hunt & David Thomas (Pragmatic Programmer)
- Brian Goetz (Java Concurrency)
- Grady Booch (Object-Oriented Analysis)

### Libros (25 libros)
**Literatura en Español:**
- Cien Años de Soledad
- La Casa de los Espíritus
- Ficciones
- Rayuela
- Don Quijote de la Mancha
- El Amor en los Tiempos del Cólera
- El Aleph
- Y más...

**Libros Técnicos:**
- Clean Code
- Refactoring
- Design Patterns
- Effective Java
- Domain-Driven Design
- The Pragmatic Programmer
- Clean Architecture
- Y más...

### Préstamos (15 libros prestados)
- **Préstamos recientes** (última semana - Marzo 2026):
  - Carlos tiene "Cien Años de Soledad" desde 2026-03-01
  - María tiene "Clean Code" desde 2026-03-02
  - Juan tiene "Ficciones" desde 2026-03-03
  - Ana tiene "Effective Java" desde 2026-03-04
  - Pedro tiene "Rayuela" desde 2026-03-05

- **Préstamos de hace 2 semanas**:
  - Laura tiene "Refactoring"
  - Diego tiene "Don Quijote"
  - Sofía tiene "Design Patterns"
  - Martín tiene "La Casa de los Espíritus"

- **Préstamos de hace 3-4 semanas**:
  - Valentina, Lucas, Camila, Facundo, Agustina, Nicolás tienen varios libros

## ✅ Verificación de Datos

### Script de Verificación
```powershell
# Usando el script PowerShell
.\populate-database.ps1 -Verify
```

### Consultas SQL Manuales

**Ver libros prestados con clientes:**
```sql
SELECT 
    c.first_name || ' ' || c.last_name AS cliente,
    b.name AS libro,
    bb.borrow_date AS fecha_prestamo,
    CURRENT_DATE - bb.borrow_date AS dias_prestado
FROM borrowed_book bb
JOIN client c ON bb.client_id = c.id
JOIN book b ON bb.book_id = b.id
ORDER BY bb.borrow_date DESC;
```

**Ver libros con autores:**
```sql
SELECT 
    b.name AS libro,
    STRING_AGG(a.first_name || ' ' || a.last_name, ', ') AS autores,
    p.name AS editorial
FROM book b
LEFT JOIN rel_book__author rba ON b.id = rba.book_id
LEFT JOIN author a ON rba.author_id = a.id
LEFT JOIN publisher p ON b.publisher_id = p.id
GROUP BY b.id, b.name, p.name
ORDER BY b.id;
```

**Estadísticas generales:**
```sql
SELECT 'Total Libros' AS concepto, COUNT(*) AS cantidad FROM book
UNION ALL
SELECT 'Total Clientes', COUNT(*) FROM client
UNION ALL
SELECT 'Libros Prestados', COUNT(*) FROM borrowed_book
UNION ALL
SELECT 'Libros Disponibles', COUNT(*) - (SELECT COUNT(*) FROM borrowed_book) FROM book;
```

## 🔐 Usuarios del Sistema

**IMPORTANTE:** Los **Clientes** NO son **Usuarios** del sistema.

### Usuarios para Login (Sistema):
- **Admin:** `admin` / `admin` (Roles: ADMIN, USER)
- **User:** `user` / `user` (Rol: USER)

### Clientes (Toman libros prestados):
- Carlos Fernández, María González, Juan Rodríguez, etc.
- No pueden hacer login en el sistema
- Solo aparecen en los registros de préstamos

## 🗑️ Limpiar Datos

Si necesitas limpiar los datos existentes antes de poblar:

```sql
-- Ejecutar estas consultas ANTES del script populate-database.sql
TRUNCATE TABLE borrowed_book CASCADE;
TRUNCATE TABLE rel_book__author CASCADE;
TRUNCATE TABLE book CASCADE;
TRUNCATE TABLE client CASCADE;
TRUNCATE TABLE author CASCADE;
TRUNCATE TABLE publisher CASCADE;

-- Reiniciar secuencias
ALTER SEQUENCE book_seq RESTART WITH 1;
ALTER SEQUENCE client_seq RESTART WITH 1;
ALTER SEQUENCE author_seq RESTART WITH 1;
ALTER SEQUENCE publisher_seq RESTART WITH 1;
ALTER SEQUENCE borrowed_book_seq RESTART WITH 1;
```

O usando el script (descomentar las líneas de TRUNCATE al inicio del archivo SQL).

## 📝 Notas Adicionales

1. **Contexto Liquibase:** Los CSV solo se cargan si la aplicación se inicia con el contexto `faker`
2. **Fechas:** Las fechas de préstamo se calculan desde hoy (2026-03-08) hacia atrás
3. **Relaciones:** Cada libro prestado está vinculado a un cliente específico
4. **ISBNs:** Los ISBNs son simplificados pero únicos
5. **Copias:** Cada libro tiene varias copias disponibles

## 🛠️ Troubleshooting

### Error: "Contenedor no encontrado"
```powershell
# Verifica que el contenedor está corriendo
docker ps | Select-String "postgres"

# Inicia el stack completo
docker-compose -f docker-compose-full.yml up -d
```

### Error: "psql no encontrado"
```powershell
# Usa el método Docker en lugar de local
.\populate-database.ps1 -Target docker
```

### Error: "Violación de clave foránea"
- Asegúrate de ejecutar el script completo
- Las dependencias están en el orden correcto:
  1. Publisher
  2. Author
  3. Client
  4. Book
  5. rel_book__author
  6. BorrowedBook

## 📧 Soporte

Si tienes problemas:
1. Verifica los logs de PostgreSQL: `docker logs library-postgres`
2. Confirma que la BD está vacía o lista para recibir datos
3. Revisa que todas las relaciones estén correctas en el script SQL

---

**Creado para:** Proyecto Library - Ing. en Software Aplicada  
**Fecha:** Marzo 2026
