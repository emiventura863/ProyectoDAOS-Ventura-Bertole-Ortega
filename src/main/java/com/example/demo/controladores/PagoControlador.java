package com.example.demo.controladores;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demo.dto.PagoRequest;
import com.example.demo.dto.PagoResponse;
import com.example.demo.entidades.MedioPago;
import com.example.demo.entidades.Pago;
import com.example.demo.servicios.PagoServicio;

@RestController
@RequestMapping("/pagos")
public class PagoControlador {

    private final PagoServicio pagoServicio;

    // Inyección de dependencias
    public PagoControlador(PagoServicio pagoServicio) {
        this.pagoServicio = pagoServicio;
    }

    // Registrar un pago -> 201 Created (con cabecera Location)
    @PostMapping
    public ResponseEntity<EntityModel<PagoResponse>> registrarPago(@RequestBody PagoRequest request) {
        Pago pago = pagoServicio.registrarPago(request);
        EntityModel<PagoResponse> modelo = aModelo(pago);
        return ResponseEntity.created(modelo.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(modelo);
    }

    // Buscar un pago por su ID -> 200 OK
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PagoResponse>> obtenerPago(@PathVariable Long id) {
        return ResponseEntity.ok(aModelo(pagoServicio.buscarPorId(id)));
    }

    // Listar pagos con filtros opcionales (facturaId, medioPago, desde, hasta) -> 200 OK
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<PagoResponse>>> listarPagos(
            @RequestParam(required = false) Long facturaId,
            @RequestParam(required = false) MedioPago medioPago,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        List<EntityModel<PagoResponse>> pagos = pagoServicio.listarPagos(facturaId, medioPago, desde, hasta)
                .stream().map(this::aModelo).toList();
        return ResponseEntity.ok(CollectionModel.of(pagos, linkTo(PagoControlador.class).withSelfRel()));
    }

    // Arma la representación HATEOAS: datos del pago + links self y factura
    private EntityModel<PagoResponse> aModelo(Pago pago) {
        Link self = linkTo(methodOn(PagoControlador.class).obtenerPago(pago.getId())).withSelfRel();
        // El link a la factura se arma por ruta para no depender del controlador de facturas.
        Link factura = Link.of(ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/facturas/{id}").buildAndExpand(pago.getFactura().getId()).toUriString(), "factura");
        return EntityModel.of(PagoResponse.desde(pago), self, factura);
    }
}
