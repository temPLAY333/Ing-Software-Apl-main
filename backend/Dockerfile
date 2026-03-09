# ================================
# Multi-Stage Dockerfile for Library Application
# Stage 1: Build - Compile Java backend and Angular frontend
# Stage 2: Runtime - Minimal JRE image
# ================================

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copy Maven files first for layer caching
COPY pom.xml .

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src
COPY package.json package-lock.json ./
COPY angular.json tsconfig.json tsconfig.app.json tsconfig.spec.json ./
COPY webpack ./webpack
COPY sonar-project.properties .

# Build the application
# - Uses production profile
# - Skips tests (run in CI/CD)
# - Skips code quality checks (Checkstyle, Modernizer)
RUN mvn clean package -Pprod -DskipTests -Dcheckstyle.skip=true -Dmodernizer.skip=true

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

# Install curl for healthchecks
RUN apk add --no-cache curl

# Create app user (non-root for security)
RUN addgroup -g 1001 -S appuser && \
    adduser -u 1001 -S appuser -G appuser

WORKDIR /app

# Copy JAR from build stage
COPY --from=build --chown=appuser:appuser /app/target/*.jar app.jar

# Create logs directory for ELK collection
RUN mkdir -p /app/logs && chown -R appuser:appuser /app/logs

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/management/health || exit 1

# JVM optimizations for containers
ENV JAVA_OPTS="-Xmx512m -Xms256m \
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=/app/logs/heap-dump.hprof \
    -Djava.security.egd=file:/dev/./urandom"

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
