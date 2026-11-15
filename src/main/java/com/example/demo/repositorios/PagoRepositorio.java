package com.example.demo.repositorios;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entidades.MedioPago;
import com.example.demo.entidades.Pago;

public interface PagoRepositorio extends JpaRepository<Pago, Long> {

    // Spring Data arma la consulta solo a partir del nombre: busca por factura.id
    boolean existsByFacturaId(Long facturaId);

    // Listado con filtros opcionales: un parámetro null significa "no filtrar por eso"
    @Query("""
            select p from Pago p
            where (:facturaId is null or p.factura.id = :facturaId)
              and (:medioPago is null or p.medioPago = :medioPago)
              and (:desde is null or p.fechaPago >= :desde)
              and (:hasta is null or p.fechaPago <= :hasta)
            order by p.fechaPago desc, p.id desc
            """)
    List<Pago> filtrar(@Param("facturaId") Long facturaId,
            @Param("medioPago") MedioPago medioPago,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta);
}
