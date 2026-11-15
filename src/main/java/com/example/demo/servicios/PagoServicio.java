package com.example.demo.servicios;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.PagoRequest;
import com.example.demo.entidades.EstadoFactura;
import com.example.demo.entidades.Factura;
import com.example.demo.entidades.MedioPago;
import com.example.demo.entidades.Pago;
import com.example.demo.excepciones.ConflictoNegocioException;
import com.example.demo.excepciones.DatosInvalidosException;
import com.example.demo.excepciones.RecursoNoEncontradoException;
import com.example.demo.repositorios.FacturaRepositorio;
import com.example.demo.repositorios.PagoRepositorio;

@Service
public class PagoServicio {

    private final PagoRepositorio pagoRepositorio;
    private final FacturaRepositorio facturaRepositorio; // repositorio de Emiliano (S01)

    public PagoServicio(PagoRepositorio pagoRepositorio, FacturaRepositorio facturaRepositorio) {
        this.pagoRepositorio = pagoRepositorio;
        this.facturaRepositorio = facturaRepositorio;
    }

    /**
     * Registra un pago y marca la factura como pagada en UNA sola transacción:
     * si cualquier paso lanza una excepción, Spring hace rollback y no queda nada guardado.
     */
    @Transactional
    public Pago registrarPago(PagoRequest req) {
        // 1) 400: datos faltantes o fuera de rango
        validarDatos(req);

        // 2) 404: la factura debe existir
        Factura factura = facturaRepositorio.findById(req.facturaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "La factura " + req.facturaId() + " no existe"));

        // 3) 409: reglas de negocio contra el estado de la factura.
        // Se admiten PENDIENTE y VENCIDA (la vencida se calcula al consultar, no se guarda).
        if (factura.getEstado() == EstadoFactura.ANULADA) {
            throw new ConflictoNegocioException("No se puede registrar un pago de una factura anulada");
        }
        if (factura.getEstado() == EstadoFactura.PAGADA
                || pagoRepositorio.existsByFacturaId(factura.getId())) {
            throw new ConflictoNegocioException("La factura ya tiene un pago registrado");
        }

        LocalDate fechaPago = (req.fechaPago() != null) ? req.fechaPago() : LocalDate.now();
        if (fechaPago.isBefore(factura.getFechaEmision())) {
            throw new ConflictoNegocioException("La fecha de pago no puede ser anterior a la emisión de la factura");
        }

        // importe pagado - interés debe ser igual al importe de la factura (sin pagos parciales).
        // compareTo ignora la escala (150000.0 == 150000.00); equals no.
        if (req.importePagado().subtract(req.interes()).compareTo(factura.getImporte()) != 0) {
            throw new ConflictoNegocioException(
                    "El importe pagado menos el interés debe ser igual al importe de la factura ("
                            + factura.getImporte() + "); no se admiten pagos parciales");
        }

        // 4) Guardar el pago (flush: si la restricción UNIQUE falla, falla ACÁ y se revierte todo)
        Pago pago = pagoRepositorio.saveAndFlush(new Pago(factura, fechaPago, req.medioPago(),
                req.importePagado(), req.interes()));

        // 5) Cambiar el estado de la factura (misma transacción)
        factura.setEstado(EstadoFactura.PAGADA);
        facturaRepositorio.save(factura);

        return pago;
    }

    @Transactional(readOnly = true)
    public Pago buscarPorId(Long id) {
        return pagoRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El pago " + id + " no existe"));
    }

    @Transactional(readOnly = true)
    public List<Pago> listarPagos(Long facturaId, MedioPago medioPago, LocalDate desde, LocalDate hasta) {
        return pagoRepositorio.filtrar(facturaId, medioPago, desde, hasta);
    }

    private void validarDatos(PagoRequest req) {
        if (req == null) {
            throw new DatosInvalidosException("Falta el cuerpo de la petición");
        }
        if (req.facturaId() == null) {
            throw new DatosInvalidosException("La factura asociada es obligatoria");
        }
        if (req.medioPago() == null) {
            throw new DatosInvalidosException("El medio de pago es obligatorio");
        }
        if (req.importePagado() == null || req.importePagado().signum() <= 0) {
            throw new DatosInvalidosException("El importe pagado es obligatorio y debe ser mayor a cero");
        }
        if (req.interes() == null || req.interes().signum() < 0) {
            throw new DatosInvalidosException("El interés es obligatorio y debe ser mayor o igual a cero");
        }
        if (req.fechaPago() != null && req.fechaPago().isAfter(LocalDate.now())) {
            throw new DatosInvalidosException("La fecha de pago no puede ser futura");
        }
    }
}
