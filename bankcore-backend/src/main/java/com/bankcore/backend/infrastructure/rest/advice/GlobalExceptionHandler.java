package com.bankcore.backend.infrastructure.rest.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manejador Centralizado de Excepciones (AOP - Aspect-Oriented Programming).
 * Implementa el estándar RFC 7807 (Problem Details for HTTP APIs) nativo en Spring Boot 3.
 *
 * Responsabilidad Única (SRP):
 * 1. Interceptar cualquier falla producida en controladores o servicios.
 * 2. Mapear la excepción a un código de estado HTTP semánticamente correcto.
 * 3. Sanitizar las respuestas evitando fugar Stack Traces o detalles internos (OWASP A05).
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String BASE_TYPE_URI = "https://bankcore.digital/errors/";

    /**
     * Captura errores de validación de Bean Validation (@Valid en DTOs de entrada).
     * Retorna HTTP 400 Bad Request con el mapa detallado de campos con error.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Falla de validación defensiva en petición: {} errores detectados", ex.getBindingResult().getErrorCount());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Uno o más campos de la petición no cumplen con las validaciones de contrato."
        );
        problemDetail.setTitle("Error de Validación de Entrada");
        problemDetail.setType(URI.create(BASE_TYPE_URI + "validation-error"));
        problemDetail.setProperty("timestamp", Instant.now());

        Map<String, String> errores = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }
        problemDetail.setProperty("errores", errores);

        return problemDetail;
    }

    /**
     * Captura errores de argumentos ilegales (ej: cuenta no encontrada, origen igual a destino).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Argumento o parámetro inválido: {}", ex.getMessage());

        boolean noEncontrado = ex.getMessage() != null && ex.getMessage().toLowerCase().contains("no encontrada");
        HttpStatus status = noEncontrado ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        String typeSlug = noEncontrado ? "resource-not-found" : "invalid-argument";

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setTitle(noEncontrado ? "Recurso No Encontrado" : "Parámetro Inválido");
        problemDetail.setType(URI.create(BASE_TYPE_URI + typeSlug));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    /**
     * Captura violaciones de reglas de negocio contables (ej: saldo insuficiente, tarjeta bloqueada).
     * Retorna HTTP 422 Unprocessable Entity (el formato es válido, pero el negocio rechaza el cambio).
     */
    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalStateException(IllegalStateException ex) {
        log.warn("Violación de regla de negocio bancaria: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY,
                ex.getMessage()
        );
        problemDetail.setTitle("Regla de Negocio No Cumplida");
        problemDetail.setType(URI.create(BASE_TYPE_URI + "business-rule-violation"));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    /**
     * Red de seguridad para excepciones no controladas de la JVM o del sistema.
     * Retorna HTTP 500 con un UUID de auditoría y CERO filtración de Stack Traces.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        String trackingId = UUID.randomUUID().toString();
        log.error("Error no controlado de servidor [Tracking ID: {}]: ", trackingId, ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error interno en el sistema. Contacte a soporte con la referencia de auditoría."
        );
        problemDetail.setTitle("Error Interno de Servidor");
        problemDetail.setType(URI.create(BASE_TYPE_URI + "internal-error"));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("trackingId", trackingId);

        return problemDetail;
    }
}
