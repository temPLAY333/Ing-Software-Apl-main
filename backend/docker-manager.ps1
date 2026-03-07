# ================================
# Script de gestión de contenedores Library
# ================================

param(
    [Parameter(Position=0)]
    [ValidateSet('up', 'down', 'restart', 'logs', 'status', 'clean')]
    [string]$Action = 'status'
)

$ComposeFile = "docker-compose-full.yml"

switch ($Action) {
    'up' {
        Write-Host "🚀 Iniciando todos los servicios de Library..." -ForegroundColor Green
        docker-compose -f $ComposeFile up -d
        Start-Sleep -Seconds 5
        docker-compose -f $ComposeFile ps
    }
    'down' {
        Write-Host "🛑 Deteniendo todos los servicios..." -ForegroundColor Yellow
        docker-compose -f $ComposeFile down
    }
    'restart' {
        Write-Host "🔄 Reiniciando todos los servicios..." -ForegroundColor Cyan
        docker-compose -f $ComposeFile restart
        Start-Sleep -Seconds 3
        docker-compose -f $ComposeFile ps
    }
    'logs' {
        Write-Host "📋 Mostrando logs de la aplicación..." -ForegroundColor Cyan
        docker-compose -f $ComposeFile logs -f app
    }
    'status' {
        Write-Host "📊 Estado de los servicios:" -ForegroundColor Green
        docker-compose -f $ComposeFile ps
    }
    'clean' {
        Write-Host "🧹 Limpiando contenedores y volúmenes..." -ForegroundColor Red
        docker-compose -f $ComposeFile down -v
        Write-Host "✅ Limpieza completada" -ForegroundColor Green
    }
}
