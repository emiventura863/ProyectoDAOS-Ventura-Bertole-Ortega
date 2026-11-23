package com.example.demo.excepciones;

/** Choque con el estado actual o con una regla de negocio -> HTTP 409. */
public class ConflictoNegocioException extends RuntimeException {
    public ConflictoNegocioException(String mensaje) {
        super(mensaje);
    }
}
