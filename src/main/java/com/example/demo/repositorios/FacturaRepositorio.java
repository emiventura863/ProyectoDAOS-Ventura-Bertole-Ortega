package com.example.demo.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entidades.Factura;
import com.example.demo.entidades.EstadoFactura;

public interface FacturaRepositorio
        extends JpaRepository<Factura, Long> {

    // Buscar facturas de un contrato
    List<Factura> findByContratoId(Long contratoId);

    // Buscar facturas de un periodo
    List<Factura> findByPeriodo(String periodo);

    // Buscar facturas por estado
    List<Factura> findByEstado(EstadoFactura estado);

    // Verificar si ya existe una factura mensual no anulada
    boolean existsByContratoIdAndPeriodoAndFacturacionMensualTrueAndEstadoNot(
            Long contratoId,
            String periodo,
            EstadoFactura estado
    );
}
