# =====================================================
# Dockerfile per aquariums-service (Spring Boot microservice)
# =====================================================
# 
# Multi-stage build per immagine ottimizzata (~150MB)
# Stage 1: BUILD con Maven + JDK 21
# Stage 2: RUNTIME con JRE leggero
# 

FROM eclipse-temurin:21-jdk-alpine AS builder
# Installa Maven (non presente in Alpine JDK)
RUN apk add --no-cache maven
WORKDIR /app
# Copia pom.xml per cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src src
# Build JAR senza test (più veloce)
RUN mvn clean package -DskipTests

# =====================================================
# RUNTIME: App con JRE minimo
# =====================================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copia JAR dal builder (solo ~50MB)
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8081
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8081/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
