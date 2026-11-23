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
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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

		return facturaRepositorio.findById(id).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe una factura con ID: " + id));
	}

	public Factura emitirFactura(Long contratoId, String periodo, String concepto, LocalDate fechaEmision,
			LocalDate fechaVencimiento, BigDecimal importe) {

		// 1. Validar que se haya indicado un contrato
		if (contratoId == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID del contrato es obligatorio");
		}

		// 2. Validar el periodo AAAA-MM
		YearMonth mesFacturado;

		try {
			if (periodo == null || !periodo.matches("\\d{4}-(0[1-9]|1[0-2])")) {
				throw new IllegalArgumentException();
			}

			mesFacturado = YearMonth.parse(periodo);

		} catch (DateTimeParseException | IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El periodo debe tener formato AAAA-MM");
		}

		// 3. Buscar el contrato
		Contrato contrato = contratoRepositorio.findById(contratoId).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el contrato con ID: " + contratoId));

		// 4. Verificar que el contrato este activo
		if (contrato.getEstado() != EstadoContrato.ACTIVO || Boolean.TRUE.equals(contrato.getEliminado())) {

			throw new ResponseStatusException(HttpStatus.CONFLICT, "El contrato no esta activo o fue eliminado");
		}

		// 5. Verificar vigencia del contrato
		LocalDate inicio = contrato.getFechaInicio();
		LocalDate fin = inicio.plusMonths(contrato.getDuracionMeses()).minusDays(1);

		if (mesFacturado.atEndOfMonth().isBefore(inicio) || mesFacturado.atDay(1).isAfter(fin)) {

			throw new ResponseStatusException(HttpStatus.CONFLICT, "El periodo esta fuera de la vigencia del contrato");
		}

		// 6. Distinguir factura mensual de adicional
		boolean mensual = (importe == null);

		// 7. Evitar duplicados mensuales
		if (mensual && facturaRepositorio.existsByContratoIdAndPeriodoAndFacturacionMensualTrueAndEstadoNot(contratoId,
				periodo, EstadoFactura.ANULADA)) {

			throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una factura mensual para ese periodo");
		}

		// 8. Calcular y validar el importe
		BigDecimal importeFinal = mensual ? contrato.getImporteMensual() : importe;

		if (importeFinal == null || importeFinal.compareTo(BigDecimal.ZERO) <= 0) {

			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El importe debe ser mayor a cero");
		}

		// 9. Completar fecha de emision
		LocalDate emision = fechaEmision != null ? fechaEmision : LocalDate.now();

		// 10. Calcular vencimiento
		LocalDate vencimiento = fechaVencimiento;

		if (vencimiento == null) {
			int dia = Math.min(contrato.getDiaVencimiento(), mesFacturado.lengthOfMonth());

			vencimiento = mesFacturado.atDay(dia);
		}

		// 11. Validar fechas
		if (emision.isAfter(vencimiento)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"La emision no puede ser posterior al vencimiento");
		}

		// 12. Crear factura
		Factura factura = new Factura();

		factura.setContrato(contrato);
		factura.setPeriodo(periodo);
		factura.setConcepto(concepto);
		factura.setFechaEmision(emision);
		factura.setFechaVencimiento(vencimiento);
		factura.setImporte(importeFinal);
		factura.setFacturacionMensual(mensual);
		factura.setEstado(EstadoFactura.PENDIENTE);

		// 13. Guardar en MySQL
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

	// Listar facturas con filtros opcionales
	public List<Factura> listarFacturas(Long contratoId, String periodo, EstadoFactura estado) {

		List<Factura> facturas = facturaRepositorio.findAll();

		// Filtrar por contrato
		if (contratoId != null) {
			facturas = facturas.stream().filter(f -> f.getContrato().getId().equals(contratoId))
					.collect(Collectors.toList());
		}

		// Filtrar por periodo
		if (periodo != null) {
			facturas = facturas.stream().filter(f -> f.getPeriodo().equals(periodo)).collect(Collectors.toList());
		}

		// Filtrar por estado
		if (estado != null) {
			facturas = facturas.stream().filter(f -> obtenerEstadoActual(f) == estado).collect(Collectors.toList());
		}

		return facturas;
	}

	// Calcular si una factura esta vencida
	public EstadoFactura obtenerEstadoActual(Factura factura) {

		if (factura.getEstado() == EstadoFactura.PENDIENTE && LocalDate.now().isAfter(factura.getFechaVencimiento())) {
			return EstadoFactura.VENCIDA;
		}

		return factura.getEstado();
	}

}
