# HU-14 - Gestion De Estudiantes

## Objetivo

Permitir al acudiente administrar estudiantes, contactos de emergencia, acudientes asociados y dispositivos vinculados.

## Cambios agregados

- Modulo `students` con entidades, DTOs, repositorios, controladores y servicios.
- Registro, consulta, actualizacion y eliminacion logica de estudiantes.
- Gestion de contactos de emergencia.
- Vinculacion de acudientes por codigo.
- Vinculacion de dispositivos del estudiante.
- Servicios auxiliares para acceso, mapeo y reglas de negocio.

## Endpoints principales

- `GET /api/students`
- `POST /api/students`
- `PUT /api/students/{id}`
- `DELETE /api/students/{id}`
- `POST /api/students/link-code`
- `POST /api/students/link-device`
- `GET /api/emergency-contacts`

## Commit sugerido

```bash
git commit -m "feat(HU-03): manage students guardians and emergency contacts"
```
