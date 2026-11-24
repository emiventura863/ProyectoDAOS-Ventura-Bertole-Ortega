package com.example.demo.entidades;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "historial_estado_incidente")
public class HistorialEstadoIncidente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "incidente_id", nullable = false)
    private Incidente incidente;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior")
    private EstadoIncidente estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", nullable = false)
    private EstadoIncidente estadoNuevo;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    public HistorialEstadoIncidente() {
    }

    public HistorialEstadoIncidente(Incidente incidente, EstadoIncidente estadoAnterior,
                                  EstadoIncidente estadoNuevo, LocalDateTime fechaHora) {
        this.incidente = incidente;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.fechaHora = fechaHora;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Incidente getIncidente() {
        return incidente;
    }

    public void setIncidente(Incidente incidente) {
        this.incidente = incidente;
    }

    public EstadoIncidente getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(EstadoIncidente estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public EstadoIncidente getEstadoNuevo() {
        return estadoNuevo;
    }

    public void setEstadoNuevo(EstadoIncidente estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
}