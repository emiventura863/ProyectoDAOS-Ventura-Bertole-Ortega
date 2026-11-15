package com.example.demo.excepciones;

/** El recurso pedido (o uno relacionado) no existe -> HTTP 404. */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
