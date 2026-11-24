package com.example.demo.controladores;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CambiarEstadoRequest;
import com.example.demo.dto.CrearIncidenteRequest;
import com.example.demo.dto.HistorialEstadoResponse;
import com.example.demo.dto.IncidenteResponse;
import com.example.demo.entidades.Incidente;
import com.example.demo.servicios.IncidenteService;
import com.example.demo.servicios.IncidenteService.IncidenteNoEncontradoException;
import com.example.demo.servicios.IncidenteService.TransicionEstadoInvalidaException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/incidentes")
public class IncidenteController {

    private final IncidenteService incidenteService;

    public IncidenteController(IncidenteService incidenteService) {
        this.incidenteService = incidenteService;
    }

    @PostMapping
    public ResponseEntity<IncidenteResponse> crear(@Valid @RequestBody CrearIncidenteRequest request) {
        Incidente creado = incidenteService.crearIncidente(
                request.getContratoId(),
                request.getTitulo(),
                request.getDescripcion(),
                request.getCategoria(),
                request.getPrioridad()
        );
        IncidenteResponse body = IncidenteResponse.fromEntity(creado);
        return ResponseEntity
                .created(URI.create("/api/incidentes/" + creado.getId()))
                .body(body);
    }

    @GetMapping
    public ResponseEntity<List<IncidenteResponse>> listar() {
        List<IncidenteResponse> lista = incidenteService.listarIncidentes().stream()
                .map(IncidenteResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidenteResponse> obtenerPorId(@PathVariable Long id) {
        Incidente incidente = incidenteService.buscarPorId(id);
        return ResponseEntity.ok(IncidenteResponse.fromEntity(incidente));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<IncidenteResponse> cambiarEstado(@PathVariable Long id,
                                                          @Valid @RequestBody CambiarEstadoRequest request) {
        Incidente actualizado = incidenteService.cambiarEstado(id, request.getEstado());
        return ResponseEntity.ok(IncidenteResponse.fromEntity(actualizado));
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<List<HistorialEstadoResponse>> obtenerHistorial(@PathVariable Long id) {
        List<HistorialEstadoResponse> historial = incidenteService.obtenerHistorial(id).stream()
                .map(HistorialEstadoResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(historial);
    }

    @ExceptionHandler(IncidenteNoEncontradoException.class)
    public ResponseEntity manejarNoEncontrado(IncidenteNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(TransicionEstadoInvalidaException.class)
    public ResponseEntity manejarTransicionInvalida(TransicionEstadoInvalidaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity manejarArgumentoInvalido(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", ex.getMessage()));
    }
}