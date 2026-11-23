package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.demo.entidades.MedioPago;
import com.example.demo.entidades.Pago;

/** Datos que la API devuelve de un pago (los links HATEOAS se agregan en el controlador). */
public record PagoResponse(
        Long id,
        Long facturaId,
        LocalDate fechaPago,
        MedioPago medioPago,
        BigDecimal importePagado,
        BigDecimal interes) {

    public static PagoResponse desde(Pago p) {
        return new PagoResponse(p.getId(), p.getFactura().getId(), p.getFechaPago(),
                p.getMedioPago(), p.getImportePagado(), p.getInteres());
    }
}
