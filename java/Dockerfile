# Build stage
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Copy the JAR from builder stage
COPY --from=builder --chown=appuser:appgroup /app/target/time-deposit-kata-1.0-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Switch to non-root user
USER appuser

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]