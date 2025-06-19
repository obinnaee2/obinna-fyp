# Step 1: Build the app using Maven
FROM maven:3.9.4-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy all source files
COPY src ./src

# Package the app
RUN mvn clean package -DskipTests

# Step 2: Use a lightweight image to run the app
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the jar from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port 8080 (or your app port)
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
