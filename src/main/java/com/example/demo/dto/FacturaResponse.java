
package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.demo.entidades.EstadoFactura;

// record, una característica de Java que permite definir clases sencillas para transportar datos sin escribir manualmente constructores, getters y setters
public record FacturaResponse(Long id, Long contratoId, String periodo, String concepto, LocalDate fechaEmision,
		LocalDate fechaVencimiento, BigDecimal importe, Boolean facturacionMensual, EstadoFactura estado) {
}
 // ¿Qué es HATEOAS? Es una forma de incluir enlaces en las respuestas de una API REST para que el cliente sepa cómo acceder a recursos relacionados