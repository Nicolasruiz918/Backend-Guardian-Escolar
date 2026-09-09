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

Swagger:

```text
http://localhost:8080/api/swagger-ui.html
```

## Funcionalidad acumulada HU-01 a HU-12

El backend cubre autenticacion y sesiones JWT, recuperacion de cuenta y 2FA, perfil y preferencias del acudiente,
gestion de estudiantes, dispositivos y ubicaciones GPS, tracking en tiempo real por WebSocket, zonas seguras,
rutas, eventos, notificaciones e historial, dashboard/reportes y registro de dispositivos para notificaciones push.

Los endpoints de negocio requieren `Authorization: Bearer <token>` salvo los endpoints publicos de autenticacion,
health, Swagger y el handshake WebSocket configurado por la aplicacion.

### Integracion push HU-12

El frontend registra el token de notificaciones del dispositivo autenticado con:

```text
PUT /api/push/devices
GET /api/push/devices
DELETE /api/push/devices/{deviceId}
```

Ejemplo de registro/actualizacion idempotente por token:

```json
{
  "token": "ExponentPushToken[xxxxxxxxxxxxxxxxxxxxxx]",
  "platform": "ANDROID",
  "deviceName": "Telefono principal"
}
```

`platform` acepta `ANDROID` o `IOS`. Si el mismo token vuelve a registrarse, se actualiza su propietario autenticado,
plataforma, nombre y ultima actividad, evitando duplicados. El backend no expone el valor del token en las respuestas.

Cuando se genera una alerta `OUTSIDE_SAFE_ZONE`, primero se persisten el evento, historial y notificacion de HU-10.
Despues de confirmar la transaccion se intenta entregar la alerta a los dispositivos activos del acudiente. La entrega
push respeta `pushEnabled` de las preferencias del perfil y un fallo del proveedor no revierte la informacion de negocio.

El proveedor implementado es Expo Push API. Por seguridad y para permitir pruebas/local sin trafico externo, push esta
deshabilitado por defecto y se habilita explicitamente por entorno.

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
JWT_SECRET=change-me-use-at-least-32-characters-for-local-development
JWT_ACCESS_EXPIRATION=15m
JWT_REFRESH_EXPIRATION=30d
SECURITY_CODE_EXPIRATION=10m
SECURITY_CODE_MAX_ATTEMPTS=5
PUSH_ENABLED=false
PUSH_PROVIDER=expo
EXPO_PUSH_ENDPOINT=https://exp.host/--/api/v2/push/send
EXPO_ACCESS_TOKEN=
PUSH_CONNECT_TIMEOUT=3s
PUSH_REQUEST_TIMEOUT=5s
```

`EXPO_ACCESS_TOKEN` es opcional y se usa como Bearer token cuando el proyecto Expo tiene habilitada seguridad de acceso.

## Persistencia

Spring Data JPA usa PostgreSQL y Flyway. HU-12 agrega `V7__create_push_devices.sql` para persistir los tokens push
asociados al usuario autenticado.

## Pruebas

```bash
mvn test
mvn clean verify
```
