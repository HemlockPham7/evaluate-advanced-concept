# ============================================================
# Stage 1: Build
# ============================================================
FROM maven:3.9-eclipse-temurin-25 AS builder

WORKDIR /build

# Copy pom first để tận dụng Docker layer cache
COPY commonlibrary/pom.xml ./pom.xml

# Download dependencies trước
RUN mvn dependency:go-offline -B

# Copy source code
COPY commonlibrary/src ./src

# Build Spring Boot application
RUN mvn clean package -DskipTests


# ============================================================
# Stage 2: Runtime
# ============================================================
FROM eclipse-temurin:25-jre

WORKDIR /app

# Spring Boot application
COPY --from=builder /build/target/*.jar /app/app.jar

# OpenTelemetry Java Agent
COPY deployment/opentelemetry/opentelemetry-javaagent.jar \
     /opt/opentelemetry/opentelemetry-javaagent.jar

# OpenTelemetry configuration
COPY deployment/opentelemetry/opentelemetry-config.properties \
     /opt/opentelemetry/opentelemetry-config.properties

EXPOSE 8080

ENTRYPOINT ["java"]