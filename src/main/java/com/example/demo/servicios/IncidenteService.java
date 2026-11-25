package com.example.demo.servicios;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entidades.CategoriaIncidente;
import com.example.demo.entidades.EstadoIncidente;
import com.example.demo.entidades.HistorialEstadoIncidente;
import com.example.demo.entidades.Incidente;
import com.example.demo.entidades.PrioridadIncidente;
import com.example.demo.repositorios.HistorialEstadoIncidenteRepository;
import com.example.demo.repositorios.IncidenteRepository;
import com.example.demo.servicios.IncidenteService.IncidenteNoEncontradoException;
import com.example.demo.entidades.Contrato;
import com.example.demo.entidades.EstadoContrato;
import com.example.demo.repositorios.ContratoRepositorio;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;

@Service
public class IncidenteService {

	private final IncidenteRepository incidenteRepository;
	private final HistorialEstadoIncidenteRepository historialRepository;
	private final ContratoRepositorio contratoRepositorio;

	private static final Map<EstadoIncidente, Set<EstadoIncidente>> TRANSICIONES_PERMITIDAS = new EnumMap<>(
			EstadoIncidente.class);

	static {
		TRANSICIONES_PERMITIDAS.put(EstadoIncidente.ABIERTO,
				EnumSet.of(EstadoIncidente.EN_PROCESO, EstadoIncidente.CANCELADO));

		TRANSICIONES_PERMITIDAS.put(EstadoIncidente.EN_PROCESO, EnumSet.of(EstadoIncidente.CANCELADO));

		TRANSICIONES_PERMITIDAS.put(EstadoIncidente.RESUELTO, EnumSet.of(EstadoIncidente.REABIERTO));

		TRANSICIONES_PERMITIDAS.put(EstadoIncidente.REABIERTO, EnumSet.of(EstadoIncidente.EN_PROCESO));

		TRANSICIONES_PERMITIDAS.put(EstadoIncidente.CANCELADO, EnumSet.noneOf(EstadoIncidente.class));
	}

	public IncidenteService(IncidenteRepository incidenteRepository,
			HistorialEstadoIncidenteRepository historialRepository, ContratoRepositorio contratoRepositorio) {

		this.incidenteRepository = incidenteRepository;
		this.historialRepository = historialRepository;
		this.contratoRepositorio = contratoRepositorio;
	}

	@Transactional
	public Incidente crearIncidente(Long contratoId, String titulo, String descripcion, CategoriaIncidente categoria,
			PrioridadIncidente prioridad) {
		if (contratoId == null || contratoId <= 0) {
			throw new IllegalArgumentException("El contratoId es obligatorio y debe ser mayor a cero.");
		}
		if (titulo == null || titulo.isBlank()) {
			throw new IllegalArgumentException("El título es obligatorio.");
		}
		if (descripcion == null || descripcion.isBlank()) {
			throw new IllegalArgumentException("La descripción es obligatoria.");
		}
		if (categoria == null) {
			throw new IllegalArgumentException("La categoría es obligatoria.");
		}
		if (prioridad == null) {
			throw new IllegalArgumentException("La prioridad es obligatoria.");
		}

		// Verificar que el contrato exista
		Contrato contrato = contratoRepositorio.findById(contratoId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato no encontrado"));

		// Verificar que el contrato este activo y no eliminado
		if (contrato.getEstado() != EstadoContrato.ACTIVO || Boolean.TRUE.equals(contrato.getEliminado())) {

			throw new ResponseStatusException(HttpStatus.CONFLICT, "El contrato debe estar activo y no eliminado");
		}

		LocalDateTime ahora = LocalDateTime.now();

		// Verificar la vigencia del contrato
		LocalDate fechaAlta = ahora.toLocalDate();

		LocalDate fechaInicio = contrato.getFechaInicio();

		LocalDate fechaFin = fechaInicio.plusMonths(contrato.getDuracionMeses()).minusDays(1);

		if (fechaAlta.isBefore(fechaInicio) || fechaAlta.isAfter(fechaFin)) {

			throw new ResponseStatusException(HttpStatus.CONFLICT, "El contrato no se encuentra vigente");
		}

		Incidente incidente = new Incidente();
		incidente.setContratoId(contratoId);
		incidente.setTitulo(titulo.trim());
		incidente.setDescripcion(descripcion.trim());
		incidente.setCategoria(categoria);
		incidente.setPrioridad(prioridad);
		incidente.setEstado(EstadoIncidente.ABIERTO);
		incidente.setFechaHoraAlta(ahora);
		incidente.agregarHistorial(null, EstadoIncidente.ABIERTO, ahora);

		return incidenteRepository.save(incidente);
	}

	// Listar incidentes aplicando filtros opcionales
	@Transactional(readOnly = true)
	public List<Incidente> listarIncidentes(Long contratoId, EstadoIncidente estado, PrioridadIncidente prioridad,
			CategoriaIncidente categoria) {

		return incidenteRepository.findAll().stream()
				.filter(i -> contratoId == null || contratoId.equals(i.getContratoId()))
				.filter(i -> estado == null || i.getEstado() == estado)
				.filter(i -> prioridad == null || i.getPrioridad() == prioridad)
				.filter(i -> categoria == null || i.getCategoria() == categoria).toList();
	}

	@Transactional(readOnly = true)
	public Incidente buscarPorId(Long id) {
		return incidenteRepository.findById(id)
				.orElseThrow(() -> new IncidenteNoEncontradoException("Incidente no encontrado con id: " + id));
	}

	@Transactional
	public Incidente cambiarEstado(Long id, EstadoIncidente nuevoEstado) {
		if (nuevoEstado == null) {
			throw new IllegalArgumentException("El nuevo estado es obligatorio.");
		}

		Incidente incidente = buscarPorId(id);
		EstadoIncidente estadoActual = incidente.getEstado();

		if (estadoActual == nuevoEstado) {
			throw new TransicionEstadoInvalidaException("El incidente ya se encuentra en estado " + estadoActual);
		}

		Set<EstadoIncidente> permitidos = TRANSICIONES_PERMITIDAS.getOrDefault(estadoActual,
				EnumSet.noneOf(EstadoIncidente.class));

		if (!permitidos.contains(nuevoEstado)) {
			throw new TransicionEstadoInvalidaException("No se permite pasar de " + estadoActual + " a " + nuevoEstado);
		}

		LocalDateTime ahora = LocalDateTime.now();
		incidente.setEstado(nuevoEstado);
		incidente.agregarHistorial(estadoActual, nuevoEstado, ahora);

		return incidenteRepository.save(incidente);
	}

	@Transactional(readOnly = true)
	public List<HistorialEstadoIncidente> obtenerHistorial(Long incidenteId) {
		buscarPorId(incidenteId);
		return historialRepository.findByIncidenteIdOrderByFechaHoraAscIdAsc(incidenteId);
	}

	public static class IncidenteNoEncontradoException extends RuntimeException {
		public IncidenteNoEncontradoException(String message) {
			super(message);
		}
	}

	public static class TransicionEstadoInvalidaException extends RuntimeException {
		public TransicionEstadoInvalidaException(String message) {
			super(message);
		}
	}
}