# HU-05 - Trayectos GPS

## Objetivo

Implementar el seguimiento de trayectos escolares, registro de coordenadas y control del estado del recorrido.

## Cambios agregados

- Modulo `trips` con entidades, DTOs, repositorios, controlador y servicios.
- Creacion y consulta de trayectos.
- Registro de coordenadas GPS.
- Actualizacion del estado del trayecto.
- Calculo de distancia y desviacion mediante `GeoMathService`.
- Generacion de alertas operativas mediante `TripAlertService`.
- Pruebas automatizadas de calculo geografico.

## Endpoints principales

- `GET /api/trips`
- `POST /api/trips`
- `PATCH /api/trips/{tripId}/status`
- `GET /api/trips/{tripId}/coordinates`
- `POST /api/trips/{tripId}/coordinates`
