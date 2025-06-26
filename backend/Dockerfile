# Multi-stage build for PhysioApp Backend
# Build Stage
FROM eclipse-temurin:21-jdk-alpine AS build

# Set working directory
WORKDIR /app

# Install necessary packages and Maven
RUN apk add --no-cache curl maven

# Copy pom.xml first for better caching
COPY pom.xml .

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Production Stage
FROM eclipse-temurin:21-jre-alpine

# Create app user for security
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Set working directory
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Change ownership to app user
RUN chown -R appuser:appgroup /app

# Switch to app user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/health || exit 1

# Run the application with Docker profile
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]
