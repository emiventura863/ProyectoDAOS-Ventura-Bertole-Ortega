package com.example.demo.entidades;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "pagos", uniqueConstraints = @UniqueConstraint(name = "uk_pago_factura", columnNames = "factura_id"))
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // La restricción UNIQUE sobre factura_id garantiza en la BASE DE DATOS "un pago por factura".
    @OneToOne(optional = false)
    @JoinColumn(name = "factura_id", nullable = false)
    private Factura factura;

    @Column(nullable = false)
    private LocalDate fechaPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MedioPago medioPago;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal importePagado;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal interes;

    // Constructor vacío requerido por JPA
    protected Pago() {
    }

    public Pago(Factura factura, LocalDate fechaPago, MedioPago medioPago,
            BigDecimal importePagado, BigDecimal interes) {
        this.factura = factura;
        this.fechaPago = fechaPago;
        this.medioPago = medioPago;
        this.importePagado = importePagado;
        this.interes = interes;
    }

    public Long getId() { return id; }
    public Factura getFactura() { return factura; }
    public LocalDate getFechaPago() { return fechaPago; }
    public MedioPago getMedioPago() { return medioPago; }
    public BigDecimal getImportePagado() { return importePagado; }
    public BigDecimal getInteres() { return interes; }
}
