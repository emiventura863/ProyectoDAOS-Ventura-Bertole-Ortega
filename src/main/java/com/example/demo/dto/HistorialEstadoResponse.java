package com.example.demo.dto;

import java.time.LocalDateTime;

import com.example.demo.entidades.EstadoIncidente;
import com.example.demo.entidades.HistorialEstadoIncidente;

public class HistorialEstadoResponse {

    private Long id;
    private EstadoIncidente estadoAnterior;
    private EstadoIncidente estadoNuevo;
    private LocalDateTime fechaHora;

    public static HistorialEstadoResponse fromEntity(HistorialEstadoIncidente entity) {
        HistorialEstadoResponse dto = new HistorialEstadoResponse();
        dto.id = entity.getId();
        dto.estadoAnterior = entity.getEstadoAnterior();
        dto.estadoNuevo = entity.getEstadoNuevo();
        dto.fechaHora = entity.getFechaHora();
        return dto;
    }

    public Long getId() {
        return id;
    }

    public EstadoIncidente getEstadoAnterior() {
        return estadoAnterior;
    }

    public EstadoIncidente getEstadoNuevo() {
        return estadoNuevo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
}