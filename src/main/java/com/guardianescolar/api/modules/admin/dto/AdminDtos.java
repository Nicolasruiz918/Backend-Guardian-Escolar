package com.guardianescolar.api.modules.admin.dto;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

public final class AdminDtos {

    private AdminDtos() {
    }

    public record DashboardResponse(
            long usuariosRegistrados,
            long estudiantesRegistrados,
            long trayectosActivos,
            long alertasRegistradas,
            long erroresRegistrados) {
    }

    public record AlertSummaryResponse(
            UUID id,
            UUID trayectoId,
            String tipoEvento,
            String mensaje,
            OffsetDateTime fechaHoraEvento,
            Boolean enviado) {
    }

    public record AuditResponse(
            UUID id,
            UUID usuarioId,
            String usuarioCorreo,
            String accion,
            String descripcion,
            String ipOrigen,
            String aplicacion,
            String metadatos,
            LocalDateTime creadoEn) {
    }

    public record ErrorResponse(
            UUID id,
            UUID usuarioId,
            String usuarioCorreo,
            String tipoError,
            String descripcion,
            String ipOrigen,
            String metadatos,
            LocalDateTime creadoEn) {
    }
}
