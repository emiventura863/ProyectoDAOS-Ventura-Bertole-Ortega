package com.example.demo.excepciones;

/** Datos faltantes, formatos inválidos o valores fuera de rango -> HTTP 400. */
public class DatosInvalidosException extends RuntimeException {
    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }
}
