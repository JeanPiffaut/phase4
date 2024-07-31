# Use an official Maven image to build the project with OpenJDK 11
FROM maven:3.8.5-openjdk-11 AS build

# Set the working directory to the project root
WORKDIR /app

# Copy the project files to the Docker image
COPY . .

# Navigate to the specific subproject and package the application
RUN mvn clean package -DskipTests

# Use an official Tomcat image as a parent image
FROM tomcat:9.0-jdk11-openjdk-slim

# Set the working directory
WORKDIR /usr/local/tomcat/webapps

# Copy the WAR file from the build stage
COPY --from=build /app/phase4-peppol-server-webapp/target/phase4-peppol-server-webapp-*.war ./ROOT.war

# Expose the port the application runs on
EXPOSE 8080

# Start Tomcat server
CMD ["catalina.sh", "run"]
