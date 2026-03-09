# ================================
# Dockerfile para Ionic App
# Multi-stage build para optimizar tamaño
# ================================

# Stage 1: Build
FROM node:20-alpine AS builder

WORKDIR /app

# Copiar package.json y package-lock.json
COPY package*.json ./

# Instalar dependencias
RUN npm ci --legacy-peer-deps

# Copiar código fuente
COPY . .

# Build de producción
RUN npm run build -- --configuration=production

# Stage 2: Serve con nginx
FROM nginx:alpine

# Copiar archivos compilados desde build stage
COPY --from=builder /app/www /usr/share/nginx/html

# Copiar configuración de nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Exponer puerto 8100
EXPOSE 8100

# Healthcheck
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8100 || exit 1

CMD ["nginx", "-g", "daemon off;"]
