# HU-19-Administracion Y Auditoria

## Objetivo

Implementar el soporte administrativo para monitorear usuarios, estudiantes, trayectos activos, alertas, auditorias y errores del sistema.

## Cambios agregados

- Modulo `admin` con controlador, DTOs y servicio.
- Modulo `audit` con entidades, repositorios y servicios para auditoria y errores.
- Interceptor de auditoria HTTP.
- Handler global de excepciones con registro de errores.
- Endpoints administrativos protegidos por rol `ADMIN`.
- DTOs alineados con el frontend administrativo.

## Endpoints principales

- `GET /api/admin/dashboard`
- `GET /api/admin/alerts`
- `GET /api/admin/audit`
- `GET /api/admin/errors`
