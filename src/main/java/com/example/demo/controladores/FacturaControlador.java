
package com.example.demo.controladores;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entidades.Factura;
import com.example.demo.servicios.FacturaServicio;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.example.demo.dto.FacturaRequest;

import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.demo.entidades.EstadoFactura;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.server.ResponseStatusException;
import com.example.demo.dto.FacturaResponse;

import org.springframework.hateoas.Link;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

@RestController
@RequestMapping("/facturas")
public class FacturaControlador {

	private final FacturaServicio facturaServicio;

	// Inyeccion de dependencias
	public FacturaControlador(FacturaServicio facturaServicio) {
		this.facturaServicio = facturaServicio;
	}

	// Consultar una factura por ID
	@GetMapping("/{id}")
	public EntityModel<FacturaResponse> obtenerFactura(@PathVariable Long id) {

		Factura factura = facturaServicio.buscarPorId(id);

		return convertirAResponse(factura);
	}

	// Emitir una nueva factura
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public EntityModel<FacturaResponse> emitirFactura(@RequestBody FacturaRequest request) {

		Factura factura = facturaServicio.emitirFactura(request.getContratoId(), request.getPeriodo(),
				request.getConcepto(), request.getFechaEmision(), request.getFechaVencimiento(), request.getImporte());

		return convertirAResponse(factura);
	}

	// Listar facturas con filtros opcionales -> @RequestParam permite recibir
	// parámetros opcionales desde la URL

	// Listar facturas con filtros opcionales
	@GetMapping
	public List<EntityModel<FacturaResponse>> listarFacturas(@RequestParam(required = false) Long contratoId,
			@RequestParam(required = false) String periodo, @RequestParam(required = false) EstadoFactura estado) {

		return facturaServicio.listarFacturas(contratoId, periodo, estado).stream().map(this::convertirAResponse)
				.toList();
	}

	// Anular una factura pendiente o vencida

	// Anular una factura
	@PatchMapping("/{id}/estado")
	public EntityModel<FacturaResponse> anularFactura(@PathVariable Long id) {

		try {
			Factura factura = facturaServicio.anularFactura(id);

			return convertirAResponse(factura);

		} catch (IllegalStateException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
	}

	// Convertir una factura en DTO con enlaces HATEOAS
	private EntityModel<FacturaResponse> convertirAResponse(Factura factura) {

		Long facturaId = factura.getId();
		Long contratoId = factura.getContrato().getId();

		FacturaResponse response = new FacturaResponse(facturaId, contratoId, factura.getPeriodo(),
				factura.getConcepto(), factura.getFechaEmision(), factura.getFechaVencimiento(), factura.getImporte(),
				factura.getFacturacionMensual(), facturaServicio.obtenerEstadoActual(factura));

		Link self = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(FacturaControlador.class).obtenerFactura(facturaId)).withSelfRel();

		Link contrato = Link.of("http://localhost:8080/contratos/" + contratoId, "contrato");

		return EntityModel.of(response, self, contrato);
	}

}
