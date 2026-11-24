package com.example.demo.dto;

import com.example.demo.entidades.CategoriaIncidente;
import com.example.demo.entidades.PrioridadIncidente;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class CrearIncidenteRequest {

    @NotNull(message = "El contratoId es obligatorio")
    @Positive(message = "El contratoId debe ser mayor a cero")
    private Long contratoId;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "El título no puede superar los 150 caracteres")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "La categoría es obligatoria")
    private CategoriaIncidente categoria;

    @NotNull(message = "La prioridad es obligatoria")
    private PrioridadIncidente prioridad;

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
}