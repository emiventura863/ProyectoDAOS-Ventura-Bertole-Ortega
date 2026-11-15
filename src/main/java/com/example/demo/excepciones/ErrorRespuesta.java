package com.example.demo.excepciones;

import java.time.LocalDateTime;

/** Cuerpo JSON uniforme para los errores de la API. */
public record ErrorRespuesta(
        LocalDateTime fecha,
        int estado,
        String error,
        String mensaje,
        String ruta) {
}
