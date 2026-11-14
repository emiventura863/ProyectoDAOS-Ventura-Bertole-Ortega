
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

@RestController
@RequestMapping("/facturas")
public class FacturaControlador {

	private final FacturaServicio facturaServicio;

	// Inyeccion de dependencias
	public FacturaControlador(FacturaServicio facturaServicio) {
		this.facturaServicio = facturaServicio;
	}

	// Buscar una factura por su ID
	@GetMapping("/{id}")
	public Factura obtenerFactura(@PathVariable Long id) {
		return facturaServicio.buscarPorId(id);
	}

	// Emitir una nueva factura
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Factura emitirFactura(@RequestBody FacturaRequest request) {

		return facturaServicio.emitirFactura(request.getContratoId(), request.getPeriodo(), request.getConcepto(),
				request.getFechaEmision(), request.getFechaVencimiento(), request.getImporte());
	}

	// Listar facturas con filtros opcionales -> @RequestParam permite recibir parámetros opcionales desde la URL
	@GetMapping
	public List<Factura> listarFacturas(@RequestParam(required = false) Long contratoId,
			@RequestParam(required = false) String periodo, @RequestParam(required = false) EstadoFactura estado) {

		return facturaServicio.listarFacturas(contratoId, periodo, estado);
	}

}
