# Use OpenJDK as the base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container
COPY build/shadow/vitaledge-data-bridge.jar /app/vitaledge-data-bridge.jar

# Copy the configuration file into the container
COPY src/main/resources/application.yaml /app/src/main/resources/application.yaml

# Expose the port used by DataBridge
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "vitaledge-data-bridge.jar"]
