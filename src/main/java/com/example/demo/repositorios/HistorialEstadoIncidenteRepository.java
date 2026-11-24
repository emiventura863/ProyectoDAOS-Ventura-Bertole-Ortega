package com.example.demo.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entidades.HistorialEstadoIncidente;

public interface HistorialEstadoIncidenteRepository extends JpaRepository<HistorialEstadoIncidente, Long> {

    List<HistorialEstadoIncidente> findByIncidenteIdOrderByFechaHoraAscIdAsc(Long incidenteId);
}