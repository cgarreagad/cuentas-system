FROM openjdk:21-jdk-slim
LABEL authors="chris"

WORKDIR /app

COPY target/cuentas-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto en el que Spring Boot corre por defecto (8080)
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]