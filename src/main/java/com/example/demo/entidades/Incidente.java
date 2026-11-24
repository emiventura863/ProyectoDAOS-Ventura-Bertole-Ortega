package com.example.demo.entidades;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "incidentes")
public class Incidente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contrato_id", nullable = false)
    private Long contratoId;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaIncidente categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadIncidente prioridad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoIncidente estado;

    @Column(name = "fecha_hora_alta", nullable = false)
    private LocalDateTime fechaHoraAlta;

    @OneToMany(mappedBy = "incidente", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fechaHora ASC, id ASC")
    private List<HistorialEstadoIncidente> historial = new ArrayList<>();

    public Incidente() {
    }

    public void agregarHistorial(EstadoIncidente estadoAnterior, EstadoIncidente estadoNuevo, LocalDateTime fechaHora) {
        HistorialEstadoIncidente entrada = new HistorialEstadoIncidente(this, estadoAnterior, estadoNuevo, fechaHora);
        this.historial.add(entrada);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContratoId() {
        return contratoId;
    }

    public void setContratoId(Long contratoId) {
        this.contratoId = contratoId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public CategoriaIncidente getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaIncidente categoria) {
        this.categoria = categoria;
    }

    public PrioridadIncidente getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(PrioridadIncidente prioridad) {
        this.prioridad = prioridad;
    }

    public EstadoIncidente getEstado() {
        return estado;
    }

    public void setEstado(EstadoIncidente estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaHoraAlta() {
        return fechaHoraAlta;
    }

    public void setFechaHoraAlta(LocalDateTime fechaHoraAlta) {
        this.fechaHoraAlta = fechaHoraAlta;
    }

    public List<HistorialEstadoIncidente> getHistorial() {
        return historial;
    }

    public void setHistorial(List<HistorialEstadoIncidente> historial) {
        this.historial = historial;
    }
}