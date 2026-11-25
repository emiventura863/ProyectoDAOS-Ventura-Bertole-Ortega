package com.example.demo.controladores;

import java.util.List;
import java.util.Map;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demo.dto.CambiarEstadoRequest;
import com.example.demo.dto.CrearIncidenteRequest;
import com.example.demo.dto.HistorialEstadoResponse;
import com.example.demo.dto.IncidenteResponse;
import com.example.demo.entidades.CategoriaIncidente;
import com.example.demo.entidades.EstadoIncidente;
import com.example.demo.entidades.Incidente;
import com.example.demo.entidades.PrioridadIncidente;
import com.example.demo.servicios.IncidenteService;
import com.example.demo.servicios.IncidenteService.IncidenteNoEncontradoException;
import com.example.demo.servicios.IncidenteService.TransicionEstadoInvalidaException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/incidentes")
public class IncidenteController {

    private final IncidenteService incidenteService;

    // Inyeccion de dependencias
    public IncidenteController(IncidenteService incidenteService) {
        this.incidenteService = incidenteService;
    }

    // Crear un incidente
    @PostMapping
    public ResponseEntity<EntityModel<IncidenteResponse>> crear(
            @Valid @RequestBody CrearIncidenteRequest request) {

        Incidente creado = incidenteService.crearIncidente(
                request.getContratoId(),
                request.getTitulo(),
                request.getDescripcion(),
                request.getCategoria(),
                request.getPrioridad());

        EntityModel<IncidenteResponse> modelo =
                convertirAResponse(creado);

        return ResponseEntity.created(
                modelo.getRequiredLink("self").toUri())
                .body(modelo);
    }

    // Listar incidentes con filtros opcionales
    @GetMapping
    public ResponseEntity<List<EntityModel<IncidenteResponse>>> listar(
            @RequestParam(required = false) Long contratoId,
            @RequestParam(required = false) EstadoIncidente estado,
            @RequestParam(required = false) PrioridadIncidente prioridad,
            @RequestParam(required = false) CategoriaIncidente categoria) {

        List<EntityModel<IncidenteResponse>> lista = incidenteService
                .listarIncidentes(contratoId, estado, prioridad, categoria)
                .stream()
                .map(this::convertirAResponse)
                .toList();

        return ResponseEntity.ok(lista);
    }

    // Consultar un incidente por ID
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<IncidenteResponse>> obtenerPorId(
            @PathVariable Long id) {

        Incidente incidente = incidenteService.buscarPorId(id);

        return ResponseEntity.ok(convertirAResponse(incidente));
    }

    // Cambiar el estado de un incidente
    @PatchMapping("/{id}/estado")
    public ResponseEntity<EntityModel<IncidenteResponse>> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoRequest request) {

        Incidente actualizado = incidenteService.cambiarEstado(
                id, request.getEstado());

        return ResponseEntity.ok(convertirAResponse(actualizado));
    }

    // Consultar el historial de estados
    @GetMapping("/{id}/historial")
    public ResponseEntity<List<HistorialEstadoResponse>> obtenerHistorial(
            @PathVariable Long id) {

        List<HistorialEstadoResponse> historial = incidenteService
                .obtenerHistorial(id)
                .stream()
                .map(HistorialEstadoResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(historial);
    }

    // Manejar incidente inexistente
    @ExceptionHandler(IncidenteNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> manejarNoEncontrado(
            IncidenteNoEncontradoException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    // Manejar transiciones de estado invalidas
    @ExceptionHandler(TransicionEstadoInvalidaException.class)
    public ResponseEntity<Map<String, String>> manejarTransicionInvalida(
            TransicionEstadoInvalidaException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }

    // Manejar argumentos invalidos
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> manejarArgumentoInvalido(
            IllegalArgumentException ex) {

        return ResponseEntity.badRequest()
                .body(Map.of("error", ex.getMessage()));
    }

    // Convertir incidente a DTO y agregar enlaces HATEOAS
    private EntityModel<IncidenteResponse> convertirAResponse(
            Incidente incidente) {

        IncidenteResponse response =
                IncidenteResponse.fromEntity(incidente);

        Link self = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(
                        IncidenteController.class)
                        .obtenerPorId(incidente.getId()))
                .withSelfRel();

        Link contrato = Link.of(
                ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/contratos/{id}")
                        .buildAndExpand(incidente.getContratoId())
                        .toUriString(),
                "contrato");

        Link historial = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(
                        IncidenteController.class)
                        .obtenerHistorial(incidente.getId()))
                .withRel("historial");

        return EntityModel.of(response, self, contrato, historial);
    }
}