# -----------------------------
# 1) Build stage: compile app
# -----------------------------
FROM maven:3.9.4-eclipse-temurin-21-alpine AS builder
WORKDIR /workspace

# Copy Maven wrapper and settings
COPY mvnw .
COPY .mvn/ .mvn/
RUN chmod +x mvnw

# Cache dependencies
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B

# Copy source and build
COPY src/ src/
RUN ./mvnw clean package -DskipTests -B

# --------------------------------------
# 2) Runtime stage: minimal JRE + jar
# --------------------------------------
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app

# Copy only the fat jar from builder
COPY --from=builder /workspace/target/*.jar app.jar

# Expose default Spring Boot port
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
