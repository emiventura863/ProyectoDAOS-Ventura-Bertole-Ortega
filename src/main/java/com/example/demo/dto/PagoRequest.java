package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.demo.entidades.MedioPago;

/** Datos que el cliente envía en POST /pagos. */
public record PagoRequest(
        Long facturaId,
        LocalDate fechaPago,
        MedioPago medioPago,
        BigDecimal importePagado,
        BigDecimal interes) {
}
