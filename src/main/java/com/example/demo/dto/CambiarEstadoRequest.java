package com.example.demo.dto;

import com.example.demo.entidades.EstadoIncidente;

import jakarta.validation.constraints.NotNull;

public class CambiarEstadoRequest {

    @NotNull(message = "El estado es obligatorio")
    private EstadoIncidente estado;

    public EstadoIncidente getEstado() {
        return estado;
    }

    public void setEstado(EstadoIncidente estado) {
        this.estado = estado;
    }
}