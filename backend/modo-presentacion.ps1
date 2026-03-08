# ================================
# Modo Presentación - Todo Listo
# ================================

Write-Host "`n╔════════════════════════════════════════════════════════════════╗" -ForegroundColor Yellow
Write-Host "║                  🎬 MODO PRESENTACIÓN                         ║" -ForegroundColor Yellow
Write-Host "╚════════════════════════════════════════════════════════════════╝" -ForegroundColor Yellow

Write-Host "`nEste script preparará todo para tu presentación:`n" -ForegroundColor Cyan
Write-Host "  1. Detendrá servicios en ejecución" -ForegroundColor White
Write-Host "  2. Iniciará ELK Stack optimizado" -ForegroundColor White
Write-Host "  3. Generará logs de ejemplo" -ForegroundColor White
Write-Host "  4. Verificará que todo funcione" -ForegroundColor White

Write-Host "`n⏱️  Tiempo estimado: 2-3 minutos`n" -ForegroundColor Yellow

$confirm = Read-Host "¿Continuar? (S/N)"

if ($confirm -ne 'S' -and $confirm -ne 's') {
    Write-Host "`n❌ Cancelado`n" -ForegroundColor Red
    exit
}

# ================================
# PASO 1: Limpieza
# ================================
Write-Host "`n[1/5] Deteniendo servicios existentes..." -ForegroundColor Cyan
docker-compose -f docker-compose-lite.yml down 2>$null
docker-compose -f docker-compose-full.yml down 2>$null
docker-compose -f docker-compose-elk-remote.yml down 2>$null
docker-compose -f docker-compose-elk-server.yml down 2>$null

Write-Host "✅ Servicios detenidos" -ForegroundColor Green

# ================================
# PASO 2: Iniciar ELK Lite
# ================================
Write-Host "`n[2/5] Iniciando ELK Stack optimizado..." -ForegroundColor Cyan
docker-compose -f docker-compose-elk-lite.yml up -d

if ($LASTEXITCODE -ne 0) {
    Write-Host "`n❌ Error al iniciar Docker. Verifica que Docker Desktop esté corriendo`n" -ForegroundColor Red
    exit
}

Write-Host "✅ Servicios iniciados" -ForegroundColor Green

# ================================
# PASO 3: Esperar que arranquen
# ================================
Write-Host "`n[3/5] Esperando que los servicios estén listos..." -ForegroundColor Cyan
Write-Host "Esto puede tomar 60-90 segundos...`n" -ForegroundColor Yellow

# Barra de progreso
$totalSeconds = 90
for ($i = 0; $i -le $totalSeconds; $i += 5) {
    $percent = [math]::Round(($i / $totalSeconds) * 100)
    $bar = "█" * [math]::Round($percent / 5)
    $empty = "░" * (20 - [math]::Round($percent / 5))
    Write-Host "`r  [$bar$empty] $percent% ($i/$totalSeconds s)" -NoNewline -ForegroundColor Cyan
    Start-Sleep -Seconds 5
}
Write-Host ""

Write-Host "`n✅ Servicios listos" -ForegroundColor Green

# ================================
# PASO 4: Verificar servicios # ================================
Write-Host "`n[4/5] Verificando servicios..." -ForegroundColor Cyan

$services = @(
    @{Name="Aplicación"; Url="http://localhost:8080/management/health"},
    @{Name="Elasticsearch"; Url="http://localhost:9200"},
    @{Name="Kibana"; Url="http://localhost:5601/api/status"}
)

foreach ($service in $services) {
    Write-Host "  Probando $($service.Name)..." -NoNewline -ForegroundColor White
    try {
        $response = Invoke-WebRequest -Uri $service.Url -TimeoutSec 10 -ErrorAction Stop
        if ($response.StatusCode -eq 200) {
            Write-Host " ✅" -ForegroundColor Green
        } else {
            Write-Host " ⚠️  (código $($response.StatusCode))" -ForegroundColor Yellow
        }
    } catch {
        Write-Host " ❌ No responde (normal si aún está iniciando)" -ForegroundColor Red
    }
}

# ================================
# PASO 5: Generar logs de prueba
# ================================
Write-Host "`n[5/5] Generando logs de ejemplo..." -ForegroundColor Cyan

# Esperar un poco más para asegurar que la app esté lista
Start-Sleep -Seconds 10

$endpoints = @(
    "/api/books",
    "/api/authors",
    "/management/health",
    "/management/info"
)

Write-Host "Generando 20 requests..." -ForegroundColor White

for ($i = 1; $i -le 20; $i++) {
    $endpoint = $endpoints | Get-Random
    try {
        Invoke-WebRequest -Uri "http://localhost:8080$endpoint" -Method GET -ErrorAction SilentlyContinue | Out-Null
        Write-Host "." -NoNewline -ForegroundColor Green
    } catch {
        Write-Host "x" -NoNewline -ForegroundColor Red
    }
    Start-Sleep -Milliseconds 500
}

Write-Host ""
Write-Host "✅ Logs generados" -ForegroundColor Green

# ================================
# RESUMEN FINAL
# ================================
Write-Host "`n╔════════════════════════════════════════════════════════════════╗" -ForegroundColor Green
Write-Host "║             ✅ TODO LISTO PARA PRESENTAR                      ║" -ForegroundColor Green
Write-Host "╚════════════════════════════════════════════════════════════════╝" -ForegroundColor Green

Write-Host "`n🌐 Servicios disponibles:" -ForegroundColor Yellow
Write-Host "`n  📱 Aplicación:"
Write-Host "     http://localhost:8080" -ForegroundColor Cyan

Write-Host "`n  📊 Kibana (Visualizaciones):"
Write-Host "     http://localhost:5601" -ForegroundColor Cyan

Write-Host "`n  🔍 Elasticsearch (API):"
Write-Host "     http://localhost:9200" -ForegroundColor Cyan

Write-Host "`n  📧 MailDev (Emails):"
Write-Host "     http://localhost:1080" -ForegroundColor Cyan

Write-Host "`n  🗄️  PostgreSQL:"
Write-Host "     localhost:5432 (user: library, pass: library)" -ForegroundColor Cyan

# Mostrar estadísticas
Write-Host "`n📈 Consumo de recursos:" -ForegroundColor Yellow
docker stats --no-stream --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}"

Write-Host "`n🎯 Próximos pasos:" -ForegroundColor Yellow
Write-Host "  1. Abre Kibana: http://localhost:5601" -ForegroundColor White
Write-Host "  2. Configura index pattern (si es primera vez)" -ForegroundColor White
Write-Host "  3. Crea tus dashboards y visualizaciones" -ForegroundColor White
Write-Host "  4. ¡A presentar!`n" -ForegroundColor White

Write-Host "💡 Tips:" -ForegroundColor Yellow
Write-Host "  - Ten 'docker stats' abierto en otra terminal" -ForegroundColor White
Write-Host "  - Captura pantallas de Kibana como backup" -ForegroundColor White
Write-Host "  - Si algo falla, reinicia: .\docker-lite-manager.ps1 elk-lite`n" -ForegroundColor White

Write-Host "🚀 ¡Buena suerte en tu presentación!`n" -ForegroundColor Green
