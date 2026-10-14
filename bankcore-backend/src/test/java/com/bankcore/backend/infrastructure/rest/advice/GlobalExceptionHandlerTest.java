package com.bankcore.backend.infrastructure.rest.advice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Pruebas Unitarias de GlobalExceptionHandler (RFC 7807 Problem Details).
 * Garantiza el 100% de cobertura de ramas y sanitización de errores defensiva.
 * Ejecución pura en memoria sin arrancar servidor web.
 */
@DisplayName("Infraestructura REST: Pruebas Unitarias de GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Nested
    @DisplayName("Mapeo de Errores de Validación (@Valid)")
    class ValidationExceptions {

        @Test
        @DisplayName("DADO MethodArgumentNotValidException con campos inválidos, ENTONCES retorna HTTP 400 con mapa de errores")
        @SuppressWarnings("unchecked")
        void debeMapearMethodArgumentNotValidException() throws NoSuchMethodException {
            // GIVEN: Usamos un MethodParameter real basado en un método existente del handler
            java.lang.reflect.Method method = GlobalExceptionHandler.class.getMethod("handleGenericException", Exception.class);
            org.springframework.core.MethodParameter parameter = new org.springframework.core.MethodParameter(method, 0);

            org.springframework.validation.BeanPropertyBindingResult bindingResult =
                    new org.springframework.validation.BeanPropertyBindingResult(new Object(), "cuentaDto");
            bindingResult.addError(new FieldError("cuentaDto", "numeroCuenta", "El número de cuenta es obligatorio"));
            bindingResult.addError(new FieldError("cuentaDto", "saldo", "El saldo no puede ser negativo"));

            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

            // WHEN
            ProblemDetail response = exceptionHandler.handleValidationExceptions(ex);

            // THEN
            assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(response.getTitle()).isEqualTo("Error de Validación de Entrada");
            assertThat(response.getType()).isEqualTo(URI.create("https://bankcore.digital/errors/validation-error"));
            assertThat(response.getProperties()).containsKey("timestamp");
            assertThat(response.getProperties()).containsKey("errores");

            Map<String, String> errores = (Map<String, String>) response.getProperties().get("errores");
            assertThat(errores).containsEntry("numeroCuenta", "El número de cuenta es obligatorio");
            assertThat(errores).containsEntry("saldo", "El saldo no puede ser negativo");
        }
    }

    @Nested
    @DisplayName("Mapeo de Argumentos Inválidos (IllegalArgumentException)")
    class IllegalArgumentExceptions {

        @Test
        @DisplayName("DADO mensaje que contiene 'no encontrada', ENTONCES retorna HTTP 404 NOT_FOUND")
        void debeRetornar404CuandoMensajeIndicaNoEncontrado() {
            // GIVEN
            IllegalArgumentException ex = new IllegalArgumentException("Cuenta de origen no encontrada con ID 99");

            // WHEN
            ProblemDetail response = exceptionHandler.handleIllegalArgumentException(ex);

            // THEN
            assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(response.getTitle()).isEqualTo("Recurso No Encontrado");
            assertThat(response.getDetail()).isEqualTo("Cuenta de origen no encontrada con ID 99");
            assertThat(response.getType()).isEqualTo(URI.create("https://bankcore.digital/errors/resource-not-found"));
            assertThat(response.getProperties()).containsKey("timestamp");
        }

        @Test
        @DisplayName("DADO mensaje de argumento inválido genérico, ENTONCES retorna HTTP 400 BAD_REQUEST")
        void debeRetornar400ParaArgumentoInvalidoGenerico() {
            // GIVEN
            IllegalArgumentException ex = new IllegalArgumentException("La cuenta de origen y destino no pueden ser iguales");

            // WHEN
            ProblemDetail response = exceptionHandler.handleIllegalArgumentException(ex);

            // THEN
            assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(response.getTitle()).isEqualTo("Parámetro Inválido");
            assertThat(response.getDetail()).isEqualTo("La cuenta de origen y destino no pueden ser iguales");
            assertThat(response.getType()).isEqualTo(URI.create("https://bankcore.digital/errors/invalid-argument"));
            assertThat(response.getProperties()).containsKey("timestamp");
        }

        @Test
        @DisplayName("DADO mensaje nulo en IllegalArgumentException, ENTONCES maneja el null-safety y retorna HTTP 400")
        void debeManejarMensajeNuloConSeguridad() {
            // GIVEN
            IllegalArgumentException ex = new IllegalArgumentException((String) null);

            // WHEN
            ProblemDetail response = exceptionHandler.handleIllegalArgumentException(ex);

            // THEN
            assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(response.getTitle()).isEqualTo("Parámetro Inválido");
            assertThat(response.getType()).isEqualTo(URI.create("https://bankcore.digital/errors/invalid-argument"));
        }
    }

    @Nested
    @DisplayName("Mapeo de Reglas de Negocio Bancarias (IllegalStateException)")
    class IllegalStateExceptions {

        @Test
        @DisplayName("DADO violación de regla de negocio, ENTONCES retorna HTTP 422 UNPROCESSABLE_ENTITY")
        void debeRetornar422ParaReglaDeNegocio() {
            // GIVEN
            IllegalStateException ex = new IllegalStateException("Fondos insuficientes para realizar la transferencia");

            // WHEN
            ProblemDetail response = exceptionHandler.handleIllegalStateException(ex);

            // THEN
            assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
            assertThat(response.getTitle()).isEqualTo("Regla de Negocio No Cumplida");
            assertThat(response.getDetail()).isEqualTo("Fondos insuficientes para realizar la transferencia");
            assertThat(response.getType()).isEqualTo(URI.create("https://bankcore.digital/errors/business-rule-violation"));
            assertThat(response.getProperties()).containsKey("timestamp");
        }
    }

    @Nested
    @DisplayName("Red de Seguridad para Excepciones Genéricas (Exception)")
    class GenericExceptions {

        @Test
        @DisplayName("DADO error inesperado del sistema, ENTONCES retorna HTTP 500 con trackingId y sin fuga de Stack Trace")
        void debeRetornar500ConTrackingIdAuditable() {
            // GIVEN
            NullPointerException ex = new NullPointerException("NPE inesperado en tiempo de ejecución");

            // WHEN
            ProblemDetail response = exceptionHandler.handleGenericException(ex);

            // THEN
            assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
            assertThat(response.getTitle()).isEqualTo("Error Interno de Servidor");
            assertThat(response.getDetail()).isEqualTo("Ocurrió un error interno en el sistema. Contacte a soporte con la referencia de auditoría.");
            assertThat(response.getType()).isEqualTo(URI.create("https://bankcore.digital/errors/internal-error"));
            assertThat(response.getProperties()).containsKey("timestamp");
            assertThat(response.getProperties()).containsKey("trackingId");
            assertThat(response.getProperties().get("trackingId").toString()).isNotEmpty();
        }
    }
}
