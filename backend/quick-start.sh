#!/bin/bash

# ================================
# Quick Start Script
# Builds and deploys the full stack
# ================================

set -e  # Exit on error

echo "🚀 Library Application - Quick Start"
echo "===================================="

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Functions
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check prerequisites
check_prerequisites() {
    log_info "Checking prerequisites..."

    if ! command -v docker &> /dev/null; then
        log_error "Docker is not installed"
        exit 1
    fi

    if ! command -v docker-compose &> /dev/null; then
        log_error "Docker Compose is not installed"
        exit 1
    fi

    log_info "✓ Prerequisites OK"
}

# Build Docker image
build_image() {
    log_info "Building Docker image..."
    cd backend
    docker build -t library:latest .
    log_info "✓ Image built successfully"
}

# Start services
start_services() {
    log_info "Starting services..."

    case $1 in
        "full")
            log_info "Starting full stack (App + DB + ELK)..."
            docker-compose -f docker-compose-full.yml up -d
            ;;
        "app")
            log_info "Starting app only (App + DB)..."
            docker-compose -f src/main/docker/app.yml up -d
            ;;
        "elk")
            log_info "Starting ELK stack only..."
            docker-compose -f src/main/docker/elk-stack.yml up -d
            ;;
        *)
            log_error "Invalid option: $1"
            exit 1
            ;;
    esac

    log_info "✓ Services started"
}

# Wait for services
wait_for_services() {
    log_info "Waiting for services to be healthy..."
    sleep 10

    # Check app health
    if docker ps | grep -q "library-app"; then
        log_info "Checking application health..."
        for i in {1..30}; do
            if curl -sf http://localhost:8080/management/health > /dev/null 2>&1; then
                log_info "✓ Application is healthy"
                break
            fi
            echo -n "."
            sleep 2
        done
    fi

    # Check Elasticsearch
    if docker ps | grep -q "elasticsearch"; then
        log_info "Checking Elasticsearch..."
        for i in {1..20}; do
            if curl -sf http://localhost:9200/_cluster/health > /dev/null 2>&1; then
                log_info "✓ Elasticsearch is healthy"
                break
            fi
            echo -n "."
            sleep 2
        done
    fi
}

# Show access URLs
show_urls() {
    echo ""
    log_info "===================================="
    log_info "Services are ready! 🎉"
    log_info "===================================="
    echo ""

    if docker ps | grep -q "library-app"; then
        echo "📱 Application:      http://localhost:8080"
        echo "📊 Health:           http://localhost:8080/management/health"
        echo "📈 Metrics:          http://localhost:8080/management/prometheus"
        echo "📖 API Docs:         http://localhost:8080/v3/api-docs"
        echo "🔍 Swagger UI:       http://localhost:8080/swagger-ui/index.html"
    fi

    if docker ps | grep -q "kibana"; then
        echo "📊 Kibana:           http://localhost:5601"
    fi

    if docker ps | grep -q "elasticsearch"; then
        echo "🔍 Elasticsearch:    http://localhost:9200"
    fi

    echo ""
    log_info "To view logs:"
    echo "  docker logs library-app -f"
    echo "  docker-compose -f docker-compose-full.yml logs -f"
    echo ""
    log_info "To stop services:"
    echo "  docker-compose -f docker-compose-full.yml down"
    echo ""
}

# Main menu
main() {
    echo ""
    echo "Select deployment option:"
    echo "1) Full Stack (App + Database + ELK)"
    echo "2) App Only (App + Database)"
    echo "3) ELK Stack Only"
    echo "4) Exit"
    echo ""
    read -p "Enter option [1-4]: " option

    case $option in
        1)
            check_prerequisites
            build_image
            start_services "full"
            wait_for_services
            show_urls
            ;;
        2)
            check_prerequisites
            build_image
            start_services "app"
            wait_for_services
            show_urls
            ;;
        3)
            check_prerequisites
            start_services "elk"
            wait_for_services
            show_urls
            ;;
        4)
            log_info "Exiting..."
            exit 0
            ;;
        *)
            log_error "Invalid option"
            exit 1
            ;;
    esac
}

# Run main
main
