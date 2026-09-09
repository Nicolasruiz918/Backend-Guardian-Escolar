# Guardian Escolar API

Backend monolitico modular para Guardian Escolar.

## Requisitos

- Java 21
- Maven 3.9+
- PostgreSQL 15+

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

```text
SERVER_PORT=8080
CORS_ALLOWED_ORIGINS=http://localhost:8081,http://localhost:19006,http://localhost:3000
DB_HOST=localhost
DB_PORT=5432
DB_NAME=guardian_escolar
DB_USERNAME=guardian
DB_PASSWORD=guardian
```

## Persistencia

HU-03 deja configurados Spring Data JPA, PostgreSQL y Flyway. Las migraciones de negocio empiezan en las HUs funcionales posteriores.
