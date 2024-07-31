# Usar una imagen oficial de Maven para construir el proyecto con OpenJDK 11
FROM maven:3.8.5-openjdk-11 AS build

# Establecer el directorio de trabajo al root del proyecto
WORKDIR /app

# Copiar los archivos del proyecto al contenedor
COPY . .

# Navegar al subproyecto específico y empaquetar la aplicación
WORKDIR /app
RUN mvn clean package

# Usar una imagen oficial de Tomcat como imagen base para la ejecución
FROM tomcat:9.0-jdk11-openjdk-slim

# Establecer el directorio de trabajo
WORKDIR /usr/local/tomcat/webapps

# Copiar el archivo WAR generado desde la fase de construcción
COPY --from=build /app/phase4-peppol-server-webapp/target/phase4-peppol-server-webapp-*.war ./ROOT.war

# Exponer el puerto en el que se ejecutará la aplicación
EXPOSE 8080

# Iniciar el servidor Tomcat
CMD ["catalina.sh", "run"]
