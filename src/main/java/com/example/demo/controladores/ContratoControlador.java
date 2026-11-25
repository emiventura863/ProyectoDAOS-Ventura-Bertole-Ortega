package com.example.demo.controladores;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entidades.Contrato;
import com.example.demo.repositorios.ContratoRepositorio;

@RestController
@RequestMapping("/contratos")
public class ContratoControlador {

	private final ContratoRepositorio contratoRepositorio;

	public ContratoControlador(ContratoRepositorio contratoRepositorio) {
		this.contratoRepositorio = contratoRepositorio;
	}

	// Consultar un contrato por ID
	@GetMapping("/{id}")
	public Contrato obtenerContrato(@PathVariable Long id) {

		return contratoRepositorio.findById(id).filter(c -> !Boolean.TRUE.equals(c.getEliminado()))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato no encontrado"));
	}
}