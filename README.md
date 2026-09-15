# HU-18- Notificaciones

## Objetivo

Implementar la gestion de notificaciones, configuraciones de aviso y dispositivos del usuario para recibir alertas del sistema.

## Cambios agregados

- Modulo `notifications` con controlador, DTOs, entidades, repositorios y servicios.
- Consulta de notificaciones recibidas.
- Marcado de notificaciones como leidas.
- Configuracion de preferencias de notificacion.
- Registro y desactivacion de dispositivos de usuario.
- Integracion base con Expo Push.
- Integracion configurable con SMS.
- Separacion de responsabilidades en mapper y servicio de entrega.

## Endpoints principales

- `GET /api/notifications`
- `PATCH /api/notifications/{id}/read`
- `GET /api/notifications/settings`
- `PUT /api/notifications/settings`
- `POST /api/devices`
- `GET /api/devices`
- `DELETE /api/devices/{id}`
