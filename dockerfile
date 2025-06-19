# Use a lightweight JDK base image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory inside container
WORKDIR /app

# Copy the built jar file into the container (adjust if your jar name changes)
COPY target/*.jar app.jar

# Expose the port your app runs on (usually 8080 for Spring Boot)
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
