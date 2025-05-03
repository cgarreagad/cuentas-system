# Cuentas API

Este proyecto implementa una API REST para un sistema bancario con PostgreSQL 16, siguiendo las especificaciones requeridas.

## Estructura del Proyecto

El proyecto está organizado en una arquitectura de capas:

- **Controllers**: Manejo de endpoints HTTP
- **Services**: Lógica de negocio
- **Repositories**: Acceso a datos
- **Entities**: Mapeo de tablas de la base de datos
- **DTOs**: Objetos de transferencia de datos
- **Exceptions**: Manejo de errores centralizado

## Tecnologías utilizadas

- Spring Boot 3.4.5
- Maven
- JPA/Hibernate
- OpenJDK 21
- PostgreSQL 16
- OpenAPI/Swagger
- ModelMapper
- Lombok
- Docker y Docker Compose
- JUnit 5 y Mockito para pruebas

## Instrucciones de despliegue

### Requisitos previos

- Docker y Docker Compose instalados
- Java 21 (para desarrollo local)

### Pasos para el despliegue

1. Clonar el repositorio
2. Navegar a la carpeta del proyecto
3. Ejecutar `docker-compose up -d`
4. La API estará disponible en http://localhost:8080
5. La documentación Swagger estará disponible en http://localhost:8080/swagger-ui.html

### Desarrollo local

1. Configurar las propiedades de la base de datos en `application.properties`
2. Ejecutar `mvn clean install` para compilar y ejecutar las pruebas
3. Ejecutar `mvn spring-boot:run` para iniciar la aplicación

## Endpoints principales

- **Clientes**: `/clientes` - CRUD de clientes
- **Cuentas**: `/cuentas` - CRUD de cuentas y generación de reportes
- **Movimientos**: `/movimientos` - CRUD de movimientos

## Características principales

- CRUD completo de las tablas generadas
- Validación de saldo para movimientos de débito
- Generación de estados de cuenta en formato JSON y PDF (Base64)
- Documentación completa con OpenAPI
- Pruebas unitarias para cada endpoint