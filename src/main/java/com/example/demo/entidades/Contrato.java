package com.example.demo.entidades;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "contratos")
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private Integer duracionMeses;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal importeMensual;

    @Column(nullable = false)
    private Integer diaVencimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoContrato estado = EstadoContrato.BORRADOR;

    @Column(nullable = false)
    private Boolean eliminado = false;

    public Contrato() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Integer getDuracionMeses() {
        return duracionMeses;
    }

    public void setDuracionMeses(Integer duracionMeses) {
        this.duracionMeses = duracionMeses;
    }

    public BigDecimal getImporteMensual() {
        return importeMensual;
    }

    public void setImporteMensual(BigDecimal importeMensual) {
        this.importeMensual = importeMensual;
    }

    public Integer getDiaVencimiento() {
        return diaVencimiento;
    }

    public void setDiaVencimiento(Integer diaVencimiento) {
        this.diaVencimiento = diaVencimiento;
    }

    public EstadoContrato getEstado() {
        return estado;
    }

    public void setEstado(EstadoContrato estado) {
        this.estado = estado;
    }

    public Boolean getEliminado() {
        return eliminado;
    }

    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado;
    }
}
