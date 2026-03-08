# ================================
# Gestor de Docker - Versiones Optimizadas
# ================================

param(
    [Parameter(Mandatory=$false)]
    [ValidateSet("elk-lite", "lite", "full", "stats", "stop", "clean", "help")]
    [string]$Mode = "help"
)

function Show-Help {
    Write-Host "`n=== GESTOR DE DOCKER ===" -ForegroundColor Cyan
    Write-Host "`nUso: .\docker-lite-manager.ps1 [modo]`n"

    Write-Host "╔════════════════════════════════════════════════════════════════╗" -ForegroundColor Yellow
    Write-Host "║  MODOS CON ELK (Para presentaciones/demos)                    ║" -ForegroundColor Yellow
    Write-Host "╚════════════════════════════════════════════════════════════════╝" -ForegroundColor Yellow

    Write-Host "`n  elk-lite   - 🎯 ELK ULTRA OPTIMIZADO para laptop" -ForegroundColor Green
    Write-Host "               Todo local: App + PostgreSQL + ELK reducido"
    Write-Host "               Consumo: ~1-1.2GB RAM, ~120-150% CPU" -ForegroundColor DarkGray
    Write-Host "               ⚡ Elasticsearch: 192MB, Logstash: 96MB, Kibana: 448MB" -ForegroundColor DarkGray
    Write-Host "               ⚠️  Más lento pero consume menos recursos" -ForegroundColor Yellow

    Write-Host "`n  full       - 📦 Stack COMPLETO original" -ForegroundColor Magenta
    Write-Host "               App + PostgreSQL + ELK + Filebeat completos"
    Write-Host "               Consumo: ~4GB RAM, ~400% CPU" -ForegroundColor DarkGray

    Write-Host "`n╔════════════════════════════════════════════════════════════════╗" -ForegroundColor Yellow
    Write-Host "║  MODOS SIN ELK (Desarrollo diario)                            ║" -ForegroundColor Yellow
    Write-Host "╚════════════════════════════════════════════════════════════════╝" -ForegroundColor Yellow

    Write-Host "`n  lite       - Versión ligera (App + PostgreSQL)" -ForegroundColor Green
    Write-Host "               Consumo: ~600MB RAM, ~100% CPU" -ForegroundColor DarkGray

    Write-Host "`n╔════════════════════════════════════════════════════════════════╗" -ForegroundColor Yellow
    Write-Host "║  UTILIDADES                                                    ║" -ForegroundColor Yellow
    Write-Host "╚════════════════════════════════════════════════════════════════╝" -ForegroundColor Yellow

    Write-Host "`n  stats      - Monitoreo de recursos en tiempo real" -ForegroundColor Cyan
    Write-Host "  stop       - Detener todos los contenedores" -ForegroundColor Red
    Write-Host "  clean      - Limpiar contenedores y volúmenes" -ForegroundColor DarkRed
    Write-Host "  help       - Muestra esta ayuda`n" -ForegroundColor White

    Write-Host "╔════════════════════════════════════════════════════════════════╗" -ForegroundColor Green
    Write-Host "║  EJEMPLOS DE USO                                              ║" -ForegroundColor Green
    Write-Host "╚════════════════════════════════════════════════════════════════╝" -ForegroundColor Green

    Write-Host "`n📚 PRESENTACIÓN EN LAPTOP:" -ForegroundColor Yellow
    Write-Host "   .\docker-lite-manager.ps1 elk-lite" -ForegroundColor White
    Write-Host "   O usa: .\modo-presentacion.ps1 (todo automático)" -ForegroundColor DarkGray

    Write-Host "`n💻 DESARROLLO DIARIO:" -ForegroundColor Yellow
    Write-Host "   .\docker-lite-manager.ps1 lite" -ForegroundColor White

    Write-Host "`n🖥️  PC PRINCIPAL (desarrollo completo):" -ForegroundColor Yellow
    Write-Host "   .\docker-lite-manager.ps1 full`n" -ForegroundColor White
}

function Start-ElkLite {
    Write-Host "`n[ELK-LITE] Iniciando ELK ULTRA OPTIMIZADO para laptop..." -ForegroundColor Green
    Write-Host "Consumo esperado: ~1-1.2GB RAM, ~120-150% CPU`n" -ForegroundColor Yellow
    Write-Host "⚠️  Nota: Más lento pero consume menos recursos`n" -ForegroundColor Yellow

    # Detener otras versiones
    docker-compose -f docker-compose-lite.yml down 2>$null
    docker-compose -f docker-compose-full.yml down 2>$null

    # Iniciar ELK Lite
    docker-compose -f docker-compose-elk-lite.yml up -d

    if ($LASTEXITCODE -eq 0) {
        Write-Host "`n✅ ELK ULTRA LITE iniciado correctamente" -ForegroundColor Green
        Write-Host "`nServicios disponibles:" -ForegroundColor Cyan
        Write-Host "  - Aplicación: http://localhost:8080"
        Write-Host "  - PostgreSQL: localhost:5432"
        Write-Host "  - Elasticsearch: http://localhost:9200"
        Write-Host "  - Kibana: http://localhost:5601"
        Write-Host "  - Logstash: localhost:5000"
        Write-Host "  - MailDev: http://localhost:1080`n"

        Write-Host "💡 Tips:" -ForegroundColor Yellow
        Write-Host "  - Espera 90-120 segundos para que Kibana inicie" -ForegroundColor White
        Write-Host "  - Elasticsearch indexa cada 30s (no en tiempo real)" -ForegroundColor White
        Write-Host "  - Logstash procesa en lotes cada 50ms`n" -ForegroundColor White

        Start-Sleep -Seconds 2
        Show-Stats
    }
}

function Start-Lite {
    Write-Host "`n[LITE] Iniciando versión LIGERA (sin ELK)..." -ForegroundColor Green
    Write-Host "Consumo esperado: ~600MB RAM, ~100% CPU`n" -ForegroundColor Yellow

    # Detener otras versiones
    docker-compose -f docker-compose-elk-lite.yml down 2>$null
    docker-compose -f docker-compose-full.yml down 2>$null

    # Iniciar versión lite
    docker-compose -f docker-compose-lite.yml up -d

    if ($LASTEXITCODE -eq 0) {
        Write-Host "`n✅ Versión LITE iniciada correctamente" -ForegroundColor Green
        Write-Host "`nServicios disponibles:" -ForegroundColor Cyan
        Write-Host "  - Aplicación: http://localhost:8080"
        Write-Host "  - PostgreSQL: localhost:5432"
        Write-Host "  - MailDev: http://localhost:1080`n"

        Start-Sleep -Seconds 2
        Show-Stats
    }
}

function Start-Full {
    Write-Host "`n[FULL] Iniciando versión COMPLETA con ELK..." -ForegroundColor Magenta
    Write-Host "Consumo esperado: ~4GB RAM, ~400% CPU`n" -ForegroundColor Yellow
    Write-Host "⚠️  ADVERTENCIA: Requiere al menos 6GB RAM disponibles`n" -ForegroundColor Red

    # Detener otras versiones
    docker-compose -f docker-compose-elk-lite.yml down 2>$null
    docker-compose -f docker-compose-lite.yml down 2>$null

    # Iniciar versión full
    docker-compose -f docker-compose-full.yml up -d

    if ($LASTEXITCODE -eq 0) {
        Write-Host "`n✅ Versión FULL iniciada correctamente" -ForegroundColor Green
        Write-Host "`nServicios disponibles:" -ForegroundColor Cyan
        Write-Host "  - Aplicación: http://localhost:8080"
        Write-Host "  - PostgreSQL: localhost:5432"
        Write-Host "  - Elasticsearch: http://localhost:9200"
        Write-Host "  - Kibana: http://localhost:5601"
        Write-Host "  - Logstash: localhost:5000"
        Write-Host "  - MailDev: http://localhost:1080`n"

        Start-Sleep -Seconds 2
        Show-Stats
    }
}

function Show-Stats {
    Write-Host "`n=== ESTADÍSTICAS DE RECURSOS ===" -ForegroundColor Cyan
    Write-Host ""

    # Estadísticas en formato tabla
    docker stats --no-stream --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.MemPerc}}"

    Write-Host "`n💡 Tip: Para monitoreo continuo, usa: docker stats`n" -ForegroundColor Yellow
}

function Stop-All {
    Write-Host "`n[STOP] Deteniendo todos los contenedores..." -ForegroundColor Red

    docker-compose -f docker-compose-elk-lite.yml down 2>$null
    docker-compose -f docker-compose-lite.yml down 2>$null
    docker-compose -f docker-compose-full.yml down 2>$null

    Write-Host "✅ Contenedores detenidos`n" -ForegroundColor Green
}

function Clean-All {
    Write-Host "`n[CLEAN] Limpiando contenedores, redes y volúmenes..." -ForegroundColor DarkRed
    Write-Host "⚠️  ADVERTENCIA: Esto eliminará todos los datos de la base de datos`n" -ForegroundColor Red

    $confirmation = Read-Host "¿Estás seguro? (S/N)"
    if ($confirmation -eq 'S' -or $confirmation -eq 's') {
        docker-compose -f docker-compose-elk-lite.yml down -v 2>$null
        docker-compose -f docker-compose-lite.yml down -v 2>$null
        docker-compose -f docker-compose-full.yml down -v 2>$null

        # Limpiar recursos huérfanos
        docker system prune -f

        Write-Host "✅ Limpieza completada`n" -ForegroundColor Green
    } else {
        Write-Host "❌ Limpieza cancelada`n" -ForegroundColor Yellow
    }
}

# ================================
# MAIN
# ================================
switch ($Mode) {
    "elk-lite" { Start-ElkLite }
    "lite"     { Start-Lite }
    "full"     { Start-Full }
    "stats"    { Show-Stats }
    "stop"     { Stop-All }
    "clean"    { Clean-All }
    "help"     { Show-Help }
    default    { Show-Help }
}
