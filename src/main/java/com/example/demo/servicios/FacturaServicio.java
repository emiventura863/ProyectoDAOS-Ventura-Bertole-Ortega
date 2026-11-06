package com.example.demo.servicios;

import org.springframework.stereotype.Service;

import com.example.demo.entidades.Factura;
import com.example.demo.repositorios.FacturaRepositorio;
import com.example.demo.entidades.Contrato;
import com.example.demo.entidades.EstadoContrato;
import com.example.demo.entidades.EstadoFactura;
import com.example.demo.repositorios.ContratoRepositorio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

@Service
public class FacturaServicio {

	private final FacturaRepositorio facturaRepositorio; // para consultar y guardar facturas
	private final ContratoRepositorio contratoRepositorio; // para comprobar que el contrato exista y obtener sus datos

	// Constructor: recibe el repositorio
	public FacturaServicio(FacturaRepositorio facturaRepositorio, ContratoRepositorio contratoRepositorio) {

		this.facturaRepositorio = facturaRepositorio;
		this.contratoRepositorio = contratoRepositorio;
	}

	// Buscar una factura por su identificador
	public Factura buscarPorId(Long id) {

		return facturaRepositorio.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("No existe una factura con ID: " + id));
	}

	public Factura emitirFactura(Long contratoId, String periodo, String concepto, LocalDate fechaEmision,
			LocalDate fechaVencimiento, BigDecimal importe) {

		// 1. Buscar el contrato
		Contrato contrato = contratoRepositorio.findById(contratoId)
				.orElseThrow(() -> new IllegalArgumentException("No existe el contrato: " + contratoId));

		// 2. Verificar que este activo y no eliminado
		if (contrato.getEstado() != EstadoContrato.ACTIVO || Boolean.TRUE.equals(contrato.getEliminado())) {
			throw new IllegalStateException("El contrato no esta activo o fue eliminado");
		}

		// 3. Validar el periodo AAAA-MM
		YearMonth mesFacturado;

		try {
			mesFacturado = YearMonth.parse(periodo);
		} catch (DateTimeParseException | NullPointerException e) {
			throw new IllegalArgumentException("El periodo debe tener formato AAAA-MM");
		}

		// 4. Verificar vigencia del contrato
		LocalDate inicio = contrato.getFechaInicio();
		LocalDate fin = inicio.plusMonths(contrato.getDuracionMeses()).minusDays(1);

		if (mesFacturado.atEndOfMonth().isBefore(inicio) || mesFacturado.atDay(1).isAfter(fin)) {
			throw new IllegalStateException("El periodo esta fuera de la vigencia del contrato");
		}

		// 5. Distinguir factura mensual de adicional
		boolean mensual = (importe == null);

		// 6. Evitar duplicados de facturacion mensual
		if (mensual && facturaRepositorio.existsByContratoIdAndPeriodoAndFacturacionMensualTrueAndEstadoNot(contratoId,
				periodo, EstadoFactura.ANULADA)) {
			throw new IllegalStateException("Ya existe una factura mensual para ese periodo");
		}

		// 7. Completar importe
		BigDecimal importeFinal = mensual ? contrato.getImporteMensual() : importe;

		if (importeFinal == null || importeFinal.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("El importe debe ser mayor a cero");
		}

		// 8. Completar fecha de emision
		LocalDate emision = fechaEmision != null ? fechaEmision : LocalDate.now();

		// 9. Calcular vencimiento si no fue indicado
		LocalDate vencimiento = fechaVencimiento;

		if (vencimiento == null) {
			int dia = Math.min(contrato.getDiaVencimiento(), mesFacturado.lengthOfMonth());

			vencimiento = mesFacturado.atDay(dia);
		}

		// 10. Validar fechas
		if (emision.isAfter(vencimiento)) {
			throw new IllegalArgumentException("La emision no puede ser posterior al vencimiento");
		}

		// 11. Crear la factura
		Factura factura = new Factura();

		factura.setContrato(contrato);
		factura.setPeriodo(periodo);
		factura.setConcepto(concepto);
		factura.setFechaEmision(emision);
		factura.setFechaVencimiento(vencimiento);
		factura.setImporte(importeFinal);
		factura.setFacturacionMensual(mensual);
		factura.setEstado(EstadoFactura.PENDIENTE);

		// 12. Guardar en MySQL
		return facturaRepositorio.save(factura);
	}

	// Anular una factura pendiente o vencida
	public Factura anularFactura(Long id) {

		// 1. Buscar la factura
		Factura factura = buscarPorId(id);

		// 2. Comprobar que no este pagada
		if (factura.getEstado() == EstadoFactura.PAGADA) {
			throw new IllegalStateException("No se puede anular una factura pagada");
		}

		// 3. Comprobar que no este anulada
		if (factura.getEstado() == EstadoFactura.ANULADA) {
			throw new IllegalStateException("La factura ya esta anulada");
		}

		// 4. Cambiar el estado
		factura.setEstado(EstadoFactura.ANULADA);

		// 5. Guardar el cambio en MySQL
		return facturaRepositorio.save(factura);
	}

}
