# ================================
# Quick Start Script for Windows
# PowerShell version
# ================================

$ErrorActionPreference = "Stop"

Write-Host "🚀 Library Application - Quick Start" -ForegroundColor Cyan
Write-Host "====================================" -ForegroundColor Cyan
Write-Host ""

# Functions
function Log-Info {
    param([string]$message)
    Write-Host "[INFO] $message" -ForegroundColor Green
}

function Log-Warn {
    param([string]$message)
    Write-Host "[WARN] $message" -ForegroundColor Yellow
}

function Log-Error {
    param([string]$message)
    Write-Host "[ERROR] $message" -ForegroundColor Red
}

# Check prerequisites
function Check-Prerequisites {
    Log-Info "Checking prerequisites..."

    if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
        Log-Error "Docker is not installed"
        exit 1
    }

    if (-not (Get-Command docker-compose -ErrorAction SilentlyContinue)) {
        Log-Error "Docker Compose is not installed"
        exit 1
    }

    Log-Info "✓ Prerequisites OK"
}

# Build Docker image
function Build-Image {
    Log-Info "Building Docker image..."
    Set-Location backend
    docker build -t library:latest .
    Log-Info "✓ Image built successfully"
}

# Start services
function Start-Services {
    param([string]$mode)

    Log-Info "Starting services..."

    switch ($mode) {
        "full" {
            Log-Info "Starting full stack (App + DB + ELK)..."
            docker-compose -f docker-compose-full.yml up -d
        }
        "app" {
            Log-Info "Starting app only (App + DB)..."
            docker-compose -f src/main/docker/app.yml up -d
        }
        "elk" {
            Log-Info "Starting ELK stack only..."
            docker-compose -f src/main/docker/elk-stack.yml up -d
        }
        default {
            Log-Error "Invalid option: $mode"
            exit 1
        }
    }

    Log-Info "✓ Services started"
}

# Wait for services
function Wait-ForServices {
    Log-Info "Waiting for services to be healthy..."
    Start-Sleep -Seconds 10

    # Check app health
    if (docker ps | Select-String "library-app") {
        Log-Info "Checking application health..."
        for ($i = 1; $i -le 30; $i++) {
            try {
                $response = Invoke-WebRequest -Uri "http://localhost:8080/management/health" -TimeoutSec 2 -UseBasicParsing
                if ($response.StatusCode -eq 200) {
                    Log-Info "✓ Application is healthy"
                    break
                }
            } catch {
                Write-Host "." -NoNewline
                Start-Sleep -Seconds 2
            }
        }
    }

    # Check Elasticsearch
    if (docker ps | Select-String "elasticsearch") {
        Log-Info "Checking Elasticsearch..."
        for ($i = 1; $i -le 20; $i++) {
            try {
                $response = Invoke-WebRequest -Uri "http://localhost:9200/_cluster/health" -TimeoutSec 2 -UseBasicParsing
                if ($response.StatusCode -eq 200) {
                    Log-Info "✓ Elasticsearch is healthy"
                    break
                }
            } catch {
                Write-Host "." -NoNewline
                Start-Sleep -Seconds 2
            }
        }
    }
}

# Show access URLs
function Show-Urls {
    Write-Host ""
    Log-Info "===================================="
    Log-Info "Services are ready! 🎉"
    Log-Info "===================================="
    Write-Host ""

    if (docker ps | Select-String "library-app") {
        Write-Host "📱 Application:      http://localhost:8080"
        Write-Host "📊 Health:           http://localhost:8080/management/health"
        Write-Host "📈 Metrics:          http://localhost:8080/management/prometheus"
        Write-Host "📖 API Docs:         http://localhost:8080/v3/api-docs"
        Write-Host "🔍 Swagger UI:       http://localhost:8080/swagger-ui/index.html"
    }

    if (docker ps | Select-String "kibana") {
        Write-Host "📊 Kibana:           http://localhost:5601"
    }

    if (docker ps | Select-String "elasticsearch") {
        Write-Host "🔍 Elasticsearch:    http://localhost:9200"
    }

    Write-Host ""
    Log-Info "To view logs:"
    Write-Host "  docker logs library-app -f"
    Write-Host "  docker-compose -f docker-compose-full.yml logs -f"
    Write-Host ""
    Log-Info "To stop services:"
    Write-Host "  docker-compose -f docker-compose-full.yml down"
    Write-Host ""
}

# Main menu
function Main {
    Write-Host ""
    Write-Host "Select deployment option:"
    Write-Host "1) Full Stack (App + Database + ELK)"
    Write-Host "2) App Only (App + Database)"
    Write-Host "3) ELK Stack Only"
    Write-Host "4) Exit"
    Write-Host ""
    $option = Read-Host "Enter option [1-4]"

    switch ($option) {
        "1" {
            Check-Prerequisites
            Build-Image
            Start-Services -mode "full"
            Wait-ForServices
            Show-Urls
        }
        "2" {
            Check-Prerequisites
            Build-Image
            Start-Services -mode "app"
            Wait-ForServices
            Show-Urls
        }
        "3" {
            Check-Prerequisites
            Start-Services -mode "elk"
            Wait-ForServices
            Show-Urls
        }
        "4" {
            Log-Info "Exiting..."
            exit 0
        }
        default {
            Log-Error "Invalid option"
            exit 1
        }
    }
}

# Run main
Main
