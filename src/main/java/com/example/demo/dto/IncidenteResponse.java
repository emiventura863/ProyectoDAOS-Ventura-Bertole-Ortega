package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.entidades.CategoriaIncidente;
import com.example.demo.entidades.EstadoIncidente;
import com.example.demo.entidades.Incidente;
import com.example.demo.entidades.PrioridadIncidente;

public class IncidenteResponse {

    private Long id;
    private Long contratoId;
    private String titulo;
    private String descripcion;
    private CategoriaIncidente categoria;
    private PrioridadIncidente prioridad;
    private EstadoIncidente estado;
    private LocalDateTime fechaHoraAlta;
    private List<HistorialEstadoResponse> historial;

    public static IncidenteResponse fromEntity(Incidente entity) {
        IncidenteResponse dto = new IncidenteResponse();
        dto.id = entity.getId();
        dto.contratoId = entity.getContratoId();
        dto.titulo = entity.getTitulo();
        dto.descripcion = entity.getDescripcion();
        dto.categoria = entity.getCategoria();
        dto.prioridad = entity.getPrioridad();
        dto.estado = entity.getEstado();
        dto.fechaHoraAlta = entity.getFechaHoraAlta();
        dto.historial = entity.getHistorial().stream()
                .map(HistorialEstadoResponse::fromEntity)
                .toList();
        return dto;
    }

    public Long getId() {
        return id;
    }

    public Long getContratoId() {
        return contratoId;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public CategoriaIncidente getCategoria() {
        return categoria;
    }

    public PrioridadIncidente getPrioridad() {
        return prioridad;
    }

    public EstadoIncidente getEstado() {
        return estado;
    }

    public LocalDateTime getFechaHoraAlta() {
        return fechaHoraAlta;
    }

    public List<HistorialEstadoResponse> getHistorial() {
        return historial;
    }
}