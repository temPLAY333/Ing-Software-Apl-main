# ================================
# Script de Inicio Rápido - Jenkins CI/CD
# ================================
# Levanta Jenkins + SonarQube para CI/CD
# Autor: Equipo de desarrollo
# Fecha: 9 de marzo de 2026

param(
    [switch]$Stop,
    [switch]$Restart,
    [switch]$Status,
    [switch]$Logs,
    [switch]$Clean,
    [switch]$Password
)

$ErrorActionPreference = "Continue"

# Colores para output
function Write-Success { Write-Host $args -ForegroundColor Green }
function Write-Info { Write-Host $args -ForegroundColor Cyan }
function Write-Warning { Write-Host $args -ForegroundColor Yellow }
function Write-Error { Write-Host $args -ForegroundColor Red }

# Banner
function Show-Banner {
    Write-Host ""
    Write-Host "╔════════════════════════════════════════╗" -ForegroundColor Cyan
    Write-Host "║     JENKINS CI/CD - Quick Start        ║" -ForegroundColor Cyan
    Write-Host "║   Library Management System Build      ║" -ForegroundColor Cyan
    Write-Host "╚════════════════════════════════════════╝" -ForegroundColor Cyan
    Write-Host ""
}

# Verificar Docker
function Test-Docker {
    Write-Info "⏳ Verificando Docker..."

    if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
        Write-Error "❌ Docker no está instalado o no está en PATH"
        Write-Warning "   Instala Docker Desktop desde: https://www.docker.com/products/docker-desktop"
        exit 1
    }

    docker info 2>&1 | Out-Null
    if ($LASTEXITCODE -ne 0) {
        Write-Error "❌ Docker no está corriendo"
        Write-Warning "   Inicia Docker Desktop y vuelve a intentar"
        exit 1
    }

    Write-Success "✓ Docker está corriendo"
}

# Verificar puertos
function Test-Ports {
    Write-Info "⏳ Verificando puertos disponibles..."

    $ports = @{
        "8090" = "Jenkins Web UI"
        "9000" = "SonarQube"
        "50000" = "Jenkins Agent"
    }

    $allFree = $true

    foreach ($port in $ports.Keys) {
        $connection = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
        if ($connection) {
            Write-Warning "⚠️  Puerto $port ($($ports[$port])) está en uso"
            $allFree = $false
        }
    }

    if ($allFree) {
        Write-Success "✓ Todos los puertos están disponibles"
    }
}

# Levantar servicios
function Start-Services {
    Show-Banner
    Test-Docker
    Test-Ports

    Write-Info "🚀 Levantando Jenkins CI/CD..."
    Write-Host ""

    docker-compose -f docker-compose-jenkins.yml up -d

    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Success "✓ Servicios iniciados correctamente"
        Write-Host ""
        Write-Info "📊 URLs disponibles:"
        Write-Host "   🎯 Jenkins:   " -NoNewline; Write-Host "http://localhost:8090/jenkins" -ForegroundColor Yellow
        Write-Host "   📊 SonarQube: " -NoNewline; Write-Host "http://localhost:9000" -ForegroundColor Yellow
        Write-Host ""
        Write-Info "⏳ Jenkins tardará 1-2 minutos en estar listo..."
        Write-Host ""
        Write-Info "🔑 Para obtener el password inicial de Jenkins:"
        Write-Host "   ./jenkins-start.ps1 -Password" -ForegroundColor Cyan
        Write-Host ""
        Write-Info "📖 Documentación completa:"
        Write-Host "   ..\docs\JENKINS-SETUP.md" -ForegroundColor Cyan
        Write-Host ""

        # Esperar 10 segundos y mostrar logs iniciales
        Write-Info "📋 Logs iniciales (Ctrl+C para salir):"
        Start-Sleep -Seconds 3
        docker-compose -f docker-compose-jenkins.yml logs --tail=20 jenkins
    } else {
        Write-Error "❌ Error al levantar servicios"
        exit 1
    }
}

# Detener servicios
function Stop-Services {
    Show-Banner
    Write-Info "🛑 Deteniendo Jenkins CI/CD..."

    docker-compose -f docker-compose-jenkins.yml stop

    if ($LASTEXITCODE -eq 0) {
        Write-Success "✓ Servicios detenidos (datos preservados)"
    } else {
        Write-Error "❌ Error al detener servicios"
    }
}

# Reiniciar servicios
function Restart-Services {
    Show-Banner
    Write-Info "🔄 Reiniciando Jenkins CI/CD..."

    docker-compose -f docker-compose-jenkins.yml restart

    if ($LASTEXITCODE -eq 0) {
        Write-Success "✓ Servicios reiniciados"
        Write-Host ""
        Write-Info "🎯 Jenkins:   http://localhost:8090/jenkins"
        Write-Info "📊 SonarQube: http://localhost:9000"
    } else {
        Write-Error "❌ Error al reiniciar servicios"
    }
}

# Ver estado
function Show-Status {
    Show-Banner
    Write-Info "📊 Estado de servicios Jenkins CI/CD:"
    Write-Host ""

    docker-compose -f docker-compose-jenkins.yml ps

    Write-Host ""
    Write-Info "💾 Uso de volúmenes:"
    Write-Host ""
    docker volume ls | Select-String "jenkins|sonar"

    Write-Host ""
    Write-Info "🌐 URLs:"
    Write-Host "   Jenkins:   http://localhost:8090/jenkins"
    Write-Host "   SonarQube: http://localhost:9000"
}

# Ver logs
function Show-Logs {
    Show-Banner
    Write-Info "📋 Logs de Jenkins (Ctrl+C para salir):"
    Write-Host ""

    docker-compose -f docker-compose-jenkins.yml logs -f jenkins
}

# Obtener password inicial
function Get-InitialPassword {
    Show-Banner
    Write-Info "🔑 Obteniendo password inicial de Jenkins..."
    Write-Host ""

    $password = docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword 2>&1

    if ($LASTEXITCODE -eq 0) {
        Write-Success "✓ Password inicial de Jenkins:"
        Write-Host ""
        Write-Host "   ╔════════════════════════════════════════╗" -ForegroundColor Yellow
        Write-Host "   ║  " -NoNewline -ForegroundColor Yellow
        Write-Host $password -NoNewline -ForegroundColor White
        Write-Host "  ║" -ForegroundColor Yellow
        Write-Host "   ╚════════════════════════════════════════╝" -ForegroundColor Yellow
        Write-Host ""
        Write-Info "📋 Copia este password y úsalo en:"
        Write-Host "   http://localhost:8090/jenkins" -ForegroundColor Cyan
        Write-Host ""
    } else {
        Write-Warning "⚠️  No se pudo obtener el password"
        Write-Info "   Posibles razones:"
        Write-Host "   - Jenkins aún no ha iniciado completamente (espera 2 minutos)"
        Write-Host "   - Jenkins ya fue configurado (usa admin/admin123)"
        Write-Host "   - El contenedor no está corriendo"
        Write-Host ""
        Write-Info "   Verifica el estado con: ./jenkins-start.ps1 -Status"
    }
}

# Limpiar todo
function Clean-All {
    Show-Banner
    Write-Warning "⚠️  ADVERTENCIA: Esto eliminará TODOS los datos de Jenkins y SonarQube"
    Write-Host "   - Configuración de Jenkins"
    Write-Host "   - Credenciales guardadas"
    Write-Host "   - Historial de builds"
    Write-Host "   - Datos de SonarQube"
    Write-Host ""

    $confirmation = Read-Host "¿Estás seguro? (escribe 'SI' para confirmar)"

    if ($confirmation -eq "SI") {
        Write-Info "🗑️  Eliminando contenedores y volúmenes..."
        docker-compose -f docker-compose-jenkins.yml down -v

        if ($LASTEXITCODE -eq 0) {
            Write-Success "✓ Todo limpiado correctamente"
            Write-Info "   Para volver a empezar, ejecuta: ./jenkins-start.ps1"
        } else {
            Write-Error "❌ Error al limpiar"
        }
    } else {
        Write-Info "❌ Operación cancelada"
    }
}

# Mostrar ayuda
function Show-Help {
    Show-Banner
    Write-Host "USO:" -ForegroundColor Yellow
    Write-Host "  ./jenkins-start.ps1 [OPCIÓN]"
    Write-Host ""
    Write-Host "OPCIONES:" -ForegroundColor Yellow
    Write-Host "  (sin parámetros)  Levantar Jenkins + SonarQube"
    Write-Host "  -Stop             Detener servicios (preserva datos)"
    Write-Host "  -Restart          Reiniciar servicios"
    Write-Host "  -Status           Ver estado de servicios"
    Write-Host "  -Logs             Ver logs en tiempo real"
    Write-Host "  -Password         Obtener password inicial de Jenkins"
    Write-Host "  -Clean            Eliminar TODO (contenedores + volúmenes + datos)"
    Write-Host ""
    Write-Host "EJEMPLOS:" -ForegroundColor Yellow
    Write-Host "  ./jenkins-start.ps1              # Levantar Jenkins"
    Write-Host "  ./jenkins-start.ps1 -Password    # Ver password inicial"
    Write-Host "  ./jenkins-start.ps1 -Status      # Ver estado"
    Write-Host "  ./jenkins-start.ps1 -Logs        # Ver logs"
    Write-Host "  ./jenkins-start.ps1 -Stop        # Detener"
    Write-Host ""
    Write-Host "DOCUMENTACIÓN:" -ForegroundColor Yellow
    Write-Host "  ..\docs\JENKINS-SETUP.md - Guía completa de configuración"
    Write-Host ""
    Write-Host "URLs:" -ForegroundColor Yellow
    Write-Host "  Jenkins:   http://localhost:8090/jenkins"
    Write-Host "  SonarQube: http://localhost:9000"
    Write-Host ""
}

# ================================
# MAIN
# ================================

# Cambiar al directorio del script
Set-Location $PSScriptRoot

# Ejecutar acción según parámetro
if ($Stop) {
    Stop-Services
}
elseif ($Restart) {
    Restart-Services
}
elseif ($Status) {
    Show-Status
}
elseif ($Logs) {
    Show-Logs
}
elseif ($Password) {
    Get-InitialPassword
}
elseif ($Clean) {
    Clean-All
}
elseif ($args.Count -gt 0) {
    Show-Help
}
else {
    Start-Services
}
