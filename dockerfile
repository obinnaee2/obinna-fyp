FROM alpine:3.19.1

# Install OpenJDK 17
RUN apk --update add openjdk17 \
    && rm -rf /var/cache/apk/*

# Set working directory
WORKDIR /app

# Expose application port
EXPOSE 9091

# Set environment variables
ENV PORT=9091
ENV JAVA_OPTS="-Xms1024m -Xmx1300m"

# Copy application configuration and JAR files
COPY 78ConfigFiles/78financials ./appconfig/78financials
COPY target ./target
RUN chmod 777 ./target

# Copy Elastic APM agent
COPY --from=docker.elastic.co/observability/apm-agent-java:1.45.0 /usr/agent/elastic-apm-agent.jar /elastic-apm-agent.jar

# Set application service configuration path
ENV APP_SERVICE_CONFIG=/app/appconfig

# Remove specific configuration file
RUN rm ./appconfig/78financials/config/routing.properties

# Modified ENTRYPOINT with increased heap size
ENTRYPOINT ["java", "-Xms1g", "-Xmx3g", "-javaagent:/elastic-apm-agent.jar", "-jar", "./target/reconciliation-automation-service-0.0.1-SNAPSHOT.jar"]

