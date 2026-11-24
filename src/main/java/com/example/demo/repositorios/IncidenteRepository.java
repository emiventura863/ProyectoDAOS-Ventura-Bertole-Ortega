package com.example.demo.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entidades.CategoriaIncidente;
import com.example.demo.entidades.EstadoIncidente;
import com.example.demo.entidades.Incidente;
import com.example.demo.entidades.PrioridadIncidente;

public interface IncidenteRepository extends JpaRepository<Incidente, Long> {

    List<Incidente> findByContratoId(Long contratoId);

    List<Incidente> findByEstado(EstadoIncidente estado);

    List<Incidente> findByPrioridad(PrioridadIncidente prioridad);

    List<Incidente> findByCategoria(CategoriaIncidente categoria);
}