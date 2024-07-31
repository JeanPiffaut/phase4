# Use an official Maven image to build the project with OpenJDK 11
FROM maven:3.8.5-openjdk-11 AS build

# Set the working directory
WORKDIR /app

# Copy the project files to the Docker image
COPY . .

# Package the application
RUN mvn clean package

# Use an official OpenJDK runtime as a parent image
FROM openjdk:11-jre-slim

# Set the working directory
WORKDIR /app

# Copy the project files from the build stage
COPY --from=build /app /app

# Copy the configuration file to the proper directory
COPY src/main/resources/application.properties /app/src/main/resources/application.properties

# Expose the port the application runs on
EXPOSE 8080

# Define environment variable
ENV NAME AS4

# Run the application
CMD ["java", "-jar", "target/phase4-server-webapp-*.jar"]
