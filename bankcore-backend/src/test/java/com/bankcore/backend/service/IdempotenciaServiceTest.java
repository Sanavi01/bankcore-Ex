package com.bankcore.backend.service;

import com.bankcore.backend.dto.TransferenciaResponseDto;
import com.bankcore.backend.infrastructure.persistence.entity.RegistroIdempotenciaJpaEntity;
import com.bankcore.backend.infrastructure.persistence.repository.SpringDataIdempotenciaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas Unitarias de IdempotenciaService.
 * Cobertura de todas las ramas defensivas y manejo de errores de serialización JSON.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Servicio: IdempotenciaService")
class IdempotenciaServiceTest {

    @Mock
    private SpringDataIdempotenciaRepository idempotenciaRepository;

    @Mock
    private ObjectMapper objectMapper;

    private IdempotenciaService idempotenciaService;

    @BeforeEach
    void setUp() {
        idempotenciaService = new IdempotenciaService(idempotenciaRepository, objectMapper);
    }

    @Nested
    @DisplayName("Búsqueda de Respuestas Previas")
    class ObtenerRespuestaPreviaRules {

        @Test
        @DisplayName("DADO clave nula o vacía, ENTONCES retorna Optional.empty()")
        void debeRetornarVacioSiClaveEsNulaOVacia() {
            assertThat(idempotenciaService.obtenerRespuestaPrevia(null, TransferenciaResponseDto.class)).isEmpty();
            assertThat(idempotenciaService.obtenerRespuestaPrevia("", TransferenciaResponseDto.class)).isEmpty();
            assertThat(idempotenciaService.obtenerRespuestaPrevia("   ", TransferenciaResponseDto.class)).isEmpty();
            verifyNoInteractions(idempotenciaRepository);
        }

        @Test
        @DisplayName("DADO clave inexistente en base de datos, ENTONCES retorna Optional.empty()")
        void debeRetornarVacioSiNoExisteEnRepositorio() {
            when(idempotenciaRepository.findById("clave-123")).thenReturn(Optional.empty());

            Optional<TransferenciaResponseDto> resultado =
                    idempotenciaService.obtenerRespuestaPrevia("clave-123", TransferenciaResponseDto.class);

            assertThat(resultado).isEmpty();
        }

        @Test
        @DisplayName("DADO clave existente con JSON válido, ENTONCES deserializa y retorna DTO")
        void debeRetornarDtoDeserializado() throws Exception {
            String json = "{\"id\":\"tx-1\"}";
            RegistroIdempotenciaJpaEntity entity = RegistroIdempotenciaJpaEntity.builder()
                    .clave("clave-123")
                    .operacion("TRANSFERENCIA")
                    .codigoEstado(200)
                    .respuestaJson(json)
                    .build();

            TransferenciaResponseDto dto = new TransferenciaResponseDto(
                    "tx-1", "001", "002", BigDecimal.TEN, "EXITOSA", LocalDateTime.now(), "Ok"
            );

            when(idempotenciaRepository.findById("clave-123")).thenReturn(Optional.of(entity));
            when(objectMapper.readValue(json, TransferenciaResponseDto.class)).thenReturn(dto);

            Optional<TransferenciaResponseDto> resultado =
                    idempotenciaService.obtenerRespuestaPrevia("clave-123", TransferenciaResponseDto.class);

            assertThat(resultado).isPresent().contains(dto);
        }

        @Test
        @DisplayName("DADO falla en deserialización JSON, ENTONCES lanza IllegalStateException")
        void debeLanzarExcepcionSiFallaDeserializacion() throws Exception {
            RegistroIdempotenciaJpaEntity entity = RegistroIdempotenciaJpaEntity.builder()
                    .clave("clave-123")
                    .operacion("TRANSFERENCIA")
                    .codigoEstado(200)
                    .respuestaJson("{json_invalido}")
                    .build();

            when(idempotenciaRepository.findById("clave-123")).thenReturn(Optional.of(entity));
            when(objectMapper.readValue("{json_invalido}", TransferenciaResponseDto.class))
                    .thenThrow(new JsonProcessingException("Error sintaxis JSON") {});

            assertThatThrownBy(() -> idempotenciaService.obtenerRespuestaPrevia("clave-123", TransferenciaResponseDto.class))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Error al recuperar respuesta idempotente previa");
        }
    }

    @Nested
    @DisplayName("Registro de Nuevas Respuestas")
    class RegistrarRespuestaRules {

        @Test
        @DisplayName("DADO clave nula o vacía al registrar, ENTONCES ignora y no interactúa con el repositorio")
        void debeIgnorarRegistroSiClaveEsInvalida() {
            idempotenciaService.registrarRespuesta(null, "OP", 200, "DATA");
            idempotenciaService.registrarRespuesta("", "OP", 200, "DATA");
            idempotenciaService.registrarRespuesta("   ", "OP", 200, "DATA");

            verifyNoInteractions(idempotenciaRepository);
        }

        @Test
        @DisplayName("DADO clave y payload válidos, ENTONCES serializa y guarda en repositorio")
        void debeGuardarRegistroExitosamente() throws Exception {
            String data = "OK_DATA";
            when(objectMapper.writeValueAsString(data)).thenReturn("\"OK_DATA\"");

            idempotenciaService.registrarRespuesta("clave-789", "TRANSFERENCIA", 200, data);

            verify(idempotenciaRepository).save(any(RegistroIdempotenciaJpaEntity.class));
        }

        @Test
        @DisplayName("DADO error en serialización Jackson, ENTONCES lanza IllegalStateException")
        void debeLanzarExcepcionSiFallaSerializacion() throws Exception {
            when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Error") {});

            assertThatThrownBy(() -> idempotenciaService.registrarRespuesta("clave-789", "TRANSFERENCIA", 200, "DATA"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Error al registrar respuesta para idempotencia");
        }
    }
}
