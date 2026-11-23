package com.example.demo.excepciones;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Traduce excepciones a respuestas HTTP. Es COMPARTIDO por todos los servicios del grupo.
 * ResponseStatusException (la que ya usa FacturaServicio) NO se captura acá: Spring la
 * resuelve solo, así que el código de facturas sigue funcionando igual.
 */
@RestControllerAdvice
public class ManejadorGlobalErrores {

    @ExceptionHandler(DatosInvalidosException.class)
    public ResponseEntity<ErrorRespuesta> datosInvalidos(DatosInvalidosException ex, HttpServletRequest req) {
        return responder(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorRespuesta> noEncontrado(RecursoNoEncontradoException ex, HttpServletRequest req) {
        return responder(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(ConflictoNegocioException.class)
    public ResponseEntity<ErrorRespuesta> conflicto(ConflictoNegocioException ex, HttpServletRequest req) {
        return responder(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    /**
     * FacturaServicio lanza IllegalArgumentException (datos inválidos) e IllegalStateException
     * (estado que no lo permite). Sin estos dos métodos, Spring las devuelve como 500.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorRespuesta> argumentoInvalido(IllegalArgumentException ex, HttpServletRequest req) {
        return responder(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorRespuesta> estadoInvalido(IllegalStateException ex, HttpServletRequest req) {
        return responder(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    /** Red de seguridad: la base de datos rechazó la operación (ej. restricción UNIQUE). */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorRespuesta> integridad(DataIntegrityViolationException ex, HttpServletRequest req) {
        return responder(HttpStatus.CONFLICT,
                "La operación entra en conflicto con una restricción de datos (posible duplicado)", req);
    }

    /** JSON mal formado, fecha con formato inválido, medio de pago inexistente, etc. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorRespuesta> jsonInvalido(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return responder(HttpStatus.BAD_REQUEST,
                "El cuerpo de la petición es inválido: revise el JSON, las fechas (AAAA-MM-DD) y los valores permitidos", req);
    }

    /** Parámetro de URL con tipo incorrecto (ej. /pagos/abc o ?medioPago=XXX). */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorRespuesta> tipoIncorrecto(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        return responder(HttpStatus.BAD_REQUEST,
                "Valor inválido para el parámetro '" + ex.getName() + "'", req);
    }

    private ResponseEntity<ErrorRespuesta> responder(HttpStatus estado, String mensaje, HttpServletRequest req) {
        ErrorRespuesta cuerpo = new ErrorRespuesta(LocalDateTime.now(), estado.value(),
                estado.getReasonPhrase(), mensaje, req.getRequestURI());
        return ResponseEntity.status(estado).body(cuerpo);
    }
}
