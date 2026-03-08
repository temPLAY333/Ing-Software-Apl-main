# ============================================
# Script PowerShell para Poblar la Base de Datos
# ============================================

param(
    [Parameter(Mandatory=$false)]
    [ValidateSet("docker", "local")]
    [string]$Target = "docker",

    [Parameter(Mandatory=$false)]
    [switch]$Clean,

    [Parameter(Mandatory=$false)]
    [switch]$Verify
)

# Configuracion
$DB_NAME = "library"
$DB_USER = "library"
$DB_PASSWORD = "library"
$DB_HOST = "localhost"
$DB_PORT = "5432"
$DOCKER_CONTAINER = "library-postgres"
$SQL_FILE = Join-Path $PSScriptRoot "populate-database.sql"

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  Script de Poblacion de Base de Datos" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Verificar que el archivo SQL existe
if (-not (Test-Path $SQL_FILE)) {
    Write-Host "ERROR: No se encontro el archivo SQL: $SQL_FILE" -ForegroundColor Red
    exit 1
}

Write-Host "Archivo SQL encontrado: $SQL_FILE" -ForegroundColor Green
Write-Host "Target: $Target" -ForegroundColor Yellow
if ($Clean) {
    Write-Host "Modo: CLEAN (se borraran datos existentes)" -ForegroundColor Yellow
}
Write-Host ""

# Funcion para ejecutar SQL en Docker
function Execute-SQLDocker {
    param([string]$SqlFile)

    Write-Host "Ejecutando SQL en contenedor Docker..." -ForegroundColor Cyan

    # Verificar que el contenedor esta corriendo
    $containerStatus = docker ps --filter "name=$DOCKER_CONTAINER" --format "{{.Status}}"
    if ([string]::IsNullOrEmpty($containerStatus)) {
        Write-Host "ERROR: El contenedor $DOCKER_CONTAINER no esta corriendo" -ForegroundColor Red
        Write-Host "Inicia el contenedor con: docker-compose -f docker-compose-full.yml up -d" -ForegroundColor Yellow
        exit 1
    }

    Write-Host "Contenedor encontrado: $DOCKER_CONTAINER ($containerStatus)" -ForegroundColor Green

    # Copiar el archivo SQL al contenedor
    Write-Host "Copiando archivo SQL al contenedor..." -ForegroundColor Cyan
    docker cp $SqlFile "${DOCKER_CONTAINER}:/tmp/populate.sql"

    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: No se pudo copiar el archivo al contenedor" -ForegroundColor Red
        exit 1
    }

    # Ejecutar el SQL
    Write-Host "Ejecutando script SQL..." -ForegroundColor Cyan
    $command = "psql -U $DB_USER -d $DB_NAME -f /tmp/populate.sql"

    docker exec -it $DOCKER_CONTAINER bash -c $command

    if ($LASTEXITCODE -eq 0) {
        Write-Host "[OK] Script ejecutado correctamente" -ForegroundColor Green
    } else {
        Write-Host "[ERROR] Hubo errores al ejecutar el script" -ForegroundColor Red
        exit 1
    }

    # Limpiar archivo temporal
    docker exec $DOCKER_CONTAINER rm /tmp/populate.sql
}

# Funcion para verificar los datos
function Verify-Data {
    Write-Host ""
    Write-Host "============================================" -ForegroundColor Cyan
    Write-Host "  Verificando Datos Insertados" -ForegroundColor Cyan
    Write-Host "============================================" -ForegroundColor Cyan
    Write-Host ""

    $verifySQL = "SELECT 'Libros' AS tipo, COUNT(*)::TEXT AS cantidad FROM book UNION ALL SELECT 'Autores', COUNT(*)::TEXT FROM author UNION ALL SELECT 'Clientes', COUNT(*)::TEXT FROM client UNION ALL SELECT 'Editoriales', COUNT(*)::TEXT FROM publisher UNION ALL SELECT 'Libros Prestados', COUNT(*)::TEXT FROM borrowed_book;"

    if ($Target -eq "docker") {
        docker exec $DOCKER_CONTAINER psql -U $DB_USER -d $DB_NAME -c $verifySQL
    }

    Write-Host ""
    Write-Host "Primeros 5 prestamos:" -ForegroundColor Yellow

    $prestamosSQL = "SELECT c.first_name || ' ' || c.last_name AS cliente, b.name AS libro, bb.borrow_date AS fecha FROM borrowed_book bb JOIN client c ON bb.client_id = c.id JOIN book b ON bb.book_id = b.id ORDER BY bb.borrow_date DESC LIMIT 5;"

    if ($Target -eq "docker") {
        docker exec $DOCKER_CONTAINER psql -U $DB_USER -d $DB_NAME -c $prestamosSQL
    }
}

# Ejecutar segun el target
try {
    if ($Target -eq "docker") {
        Execute-SQLDocker -SqlFile $SQL_FILE
    }

    # Verificar si se solicito
    if ($Verify) {
        Verify-Data
    }

    Write-Host ""
    Write-Host "============================================" -ForegroundColor Green
    Write-Host "  [OK] Proceso Completado Exitosamente" -ForegroundColor Green
    Write-Host "============================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Para verificar los datos, ejecuta:" -ForegroundColor Yellow
    Write-Host "  .\populate-database.ps1 -Verify" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Usuarios del sistema (NO son clientes):" -ForegroundColor Yellow
    Write-Host "  - admin / admin" -ForegroundColor White
    Write-Host "  - user / user" -ForegroundColor White
    Write-Host ""

} catch {
    Write-Host ""
    Write-Host "ERROR al ejecutar el script" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Yellow
    exit 1
}
