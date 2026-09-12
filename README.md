# HU-17 - Zonas Seguras

## Objetivo

Permitir la administracion de zonas seguras asociadas a estudiantes para apoyar el monitoreo de ubicaciones relevantes.

## Cambios agregados

- Modulo `zones` con entidad, DTOs, repositorio, controlador y servicio.
- Creacion, consulta, actualizacion y eliminacion logica de zonas seguras.
- Validaciones de acceso sobre estudiantes asociados al usuario autenticado.
- Preparacion para alertas relacionadas con entrada o salida de zonas.

## Endpoints principales

- `GET /api/safe-zones`
- `POST /api/safe-zones`
- `PUT /api/safe-zones/{id}`
- `DELETE /api/safe-zones/{id}`
