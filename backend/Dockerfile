# ================================
# Optimized Dockerfile for Jenkins CI/CD
# Assumes JAR is already built by Jenkins
# Single-stage runtime image only
# ================================

FROM eclipse-temurin:17-jre-alpine

# Install curl for healthchecks
RUN apk add --no-cache curl

# Create app user (non-root for security)
RUN addgroup -g 1001 -S appuser && \
    adduser -u 1001 -S appuser -G appuser

WORKDIR /app

# Copy pre-built JAR (built by Jenkins)
COPY --chown=appuser:appuser target/*.jar app.jar

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
