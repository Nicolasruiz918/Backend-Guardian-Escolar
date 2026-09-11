# HU-02 - Autenticacion

## Objetivo

Implementar el modulo de autenticacion para permitir registro, inicio de sesion, verificacion de correo, recuperacion de contrasena y emision de JWT.

## Cambios agregados

- Modulo `auth` con controlador, DTOs y servicios.
- Modulo `security` con usuarios, roles, permisos, sesiones y configuracion de seguridad.
- Servicios de correo para verificacion y recuperacion.
- Validacion de politicas de contrasena.
- Soporte de confirmacion de dispositivo y doble factor.
- Manejo centralizado de errores.
- Prueba automatizada para generacion de JWT.

## Endpoints principales

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/verify-email`
- `POST /api/auth/password/forgot`
- `POST /api/auth/password/reset`
- `POST /api/auth/verify-code`
- `GET /api/auth/confirm-login`

## Commit sugerido

```bash
git commit -m "feat(HU-02): implement authentication and account recovery"
```