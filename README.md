# Guardian Escolar API

Backend monolitico modular para Guardian Escolar.

## Requisitos

- Java 21
- Maven 3.9+

## Ejecucion local

```bash
mvn spring-boot:run
```

La API queda disponible bajo:

```text
http://localhost:8080/api
```

## Endpoints tecnicos de HU-02

```text
GET /api/health
GET /api/actuator/health
GET /api/swagger-ui.html
GET /api/v3/api-docs
```

## Pruebas

```bash
mvn test
mvn clean verify
```

## Variables de entorno

Usar `.env.example` como referencia. No guardar secretos reales en archivos versionados.
