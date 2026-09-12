# HU-15 - Rutas Escolares

## Objetivo

Implementar la gestion y consulta de rutas escolares, paradas y asignaciones de estudiantes a rutas.

## Cambios agregados

- Modulo `routes` con entidades, DTOs, repositorios, controlador y servicios.
- Consulta de rutas disponibles.
- Administracion de paradas.
- Asignacion de estudiantes a rutas.
- Validaciones de acceso segun usuario autenticado.
- Separacion de responsabilidades mediante mapper, validaciones y control de acceso.

## Endpoints principales

- `GET /api/routes`
- `GET /api/routes/{id}`
- `POST /api/routes`
- `PUT /api/routes/{id}`
- `DELETE /api/routes/{id}`

## Commit sugerido

```bash
git commit -m "feat(HU-04): manage school routes and stops"
```