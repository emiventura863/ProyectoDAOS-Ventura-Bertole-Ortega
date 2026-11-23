package com.example.demo.entidades;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "facturas")
public class Factura {

    // Identificador generado automaticamente
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Contrato al que pertenece la factura
    @ManyToOne(optional = false)
    @JoinColumn(name = "contrato_id", nullable = false)
    private Contrato contrato;

    // Periodo facturado en formato AAAA-MM
    @Column(nullable = false, length = 7)
    private String periodo;
    
    // Descripcion de la factura
    private String concepto;

    // Fecha en que se emite
    @Column(nullable = false)
    private LocalDate fechaEmision;

    // Fecha limite para pagar
    @Column(nullable = false)
    private LocalDate fechaVencimiento;

    // Importe guardado al emitir la factura
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal importe;

    // Distingue facturacion mensual de adicional
    @Column(nullable = false)
    private Boolean facturacionMensual;

    // Estado administrado por el sistema
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoFactura estado = EstadoFactura.PENDIENTE;

    // Constructor vacio requerido por JPA
    public Factura() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public Boolean getFacturacionMensual() {
        return facturacionMensual;
    }

    public void setFacturacionMensual(Boolean facturacionMensual) {
        this.facturacionMensual = facturacionMensual;
    }

    public EstadoFactura getEstado() {
        return estado;
    }

    public void setEstado(EstadoFactura estado) {
        this.estado = estado;
    }
}
