package com.bankcore.backend.service;

import com.bankcore.backend.domain.event.TransaccionRealizadaEvent;
import com.bankcore.backend.domain.model.RegistroAuditoria;
import com.bankcore.backend.domain.port.AuditoriaRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas Unitarias de AuditoriaService (Servicio de Aplicación).
 * Cobertura exhaustiva de reglas de riesgo SARLAFT/AML y delegación al puerto desacoplado.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Servicio de Aplicación: AuditoriaService")
class AuditoriaServiceTest {

    @Mock
    private AuditoriaRepositoryPort auditoriaRepositoryPort;

    private AuditoriaService auditoriaService;

    @BeforeEach
    void setUp() {
        auditoriaService = new AuditoriaService(auditoriaRepositoryPort);
    }

    @Test
    @DisplayName("DADO monto inferior al umbral AML (< 5,000,000), CUANDO se registra auditoría, ENTONCES clasifica como BAJO_RIESGO_CONFORME")
    void debeRegistrarAuditoriaBajoRiesgo() {
        TransaccionRealizadaEvent evento = new TransaccionRealizadaEvent(
                "tx-100",
                "1001",
                "1002",
                new BigDecimal("4999999.99"),
                "TRANSFERENCIA",
                LocalDateTime.now(),
                "EXITOSA",
                "Pago regular"
        );

        when(auditoriaRepositoryPort.guardar(any(RegistroAuditoria.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RegistroAuditoria resultado = auditoriaService.registrarAuditoria(evento);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdTransaccion()).isEqualTo("tx-100");
        assertThat(resultado.getClasificacionRiesgo()).isEqualTo(AuditoriaService.RIESGO_BAJO);
        assertThat(resultado.getCanal()).isEqualTo(AuditoriaService.CANAL_RABBITMQ);

        ArgumentCaptor<RegistroAuditoria> captor = ArgumentCaptor.forClass(RegistroAuditoria.class);
        verify(auditoriaRepositoryPort).guardar(captor.capture());
        assertThat(captor.getValue().getClasificacionRiesgo()).isEqualTo(AuditoriaService.RIESGO_BAJO);
    }

    @Test
    @DisplayName("DADO monto superior o igual al umbral AML (>= 5,000,000), CUANDO se registra auditoría, ENTONCES clasifica como ALERTA_AML_REVISION")
    void debeRegistrarAuditoriaAltoRiesgoAml() {
        TransaccionRealizadaEvent evento = new TransaccionRealizadaEvent(
                "tx-200",
                "1001",
                "1002",
                new BigDecimal("5000000.00"),
                "TRANSFERENCIA",
                LocalDateTime.now(),
                "EXITOSA",
                "Compra de vehículo"
        );

        when(auditoriaRepositoryPort.guardar(any(RegistroAuditoria.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RegistroAuditoria resultado = auditoriaService.registrarAuditoria(evento);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdTransaccion()).isEqualTo("tx-200");
        assertThat(resultado.getClasificacionRiesgo()).isEqualTo(AuditoriaService.RIESGO_ALTO);

        ArgumentCaptor<RegistroAuditoria> captor = ArgumentCaptor.forClass(RegistroAuditoria.class);
        verify(auditoriaRepositoryPort).guardar(captor.capture());
        assertThat(captor.getValue().getClasificacionRiesgo()).isEqualTo(AuditoriaService.RIESGO_ALTO);
    }

    @Test
    @DisplayName("DADO monto nulo, CUANDO se evalúa riesgo, ENTONCES retorna BAJO_RIESGO_CONFORME")
    void debeEvaluarRiesgoConMontoNulo() {
        String riesgo = auditoriaService.evaluarRiesgo(null);
        assertThat(riesgo).isEqualTo(AuditoriaService.RIESGO_BAJO);
    }

    @Test
    @DisplayName("DADO idTransaccion, CUANDO se consulta, ENTONCES delega a AuditoriaRepositoryPort")
    void debeBuscarPorIdTransaccion() {
        RegistroAuditoria mockRegistro = new RegistroAuditoria(1L, "tx-300", "1001", "1002", BigDecimal.TEN, AuditoriaService.RIESGO_BAJO, LocalDateTime.now(), LocalDateTime.now(), "RABBITMQ_ASYNC");
        when(auditoriaRepositoryPort.buscarPorIdTransaccion("tx-300")).thenReturn(Optional.of(mockRegistro));

        Optional<RegistroAuditoria> resultado = auditoriaService.buscarPorIdTransaccion("tx-300");

        assertThat(resultado).isPresent().contains(mockRegistro);
        verify(auditoriaRepositoryPort).buscarPorIdTransaccion("tx-300");
    }

    @Test
    @DisplayName("DADO clasificacionRiesgo, CUANDO se consulta, ENTONCES delega a AuditoriaRepositoryPort")
    void debeBuscarPorClasificacionRiesgo() {
        RegistroAuditoria mockRegistro = new RegistroAuditoria(2L, "tx-400", "1001", "1002", BigDecimal.TEN, AuditoriaService.RIESGO_ALTO, LocalDateTime.now(), LocalDateTime.now(), "RABBITMQ_ASYNC");
        when(auditoriaRepositoryPort.buscarPorClasificacionRiesgo(AuditoriaService.RIESGO_ALTO)).thenReturn(List.of(mockRegistro));

        List<RegistroAuditoria> resultado = auditoriaService.buscarPorClasificacionRiesgo(AuditoriaService.RIESGO_ALTO);

        assertThat(resultado).hasSize(1).contains(mockRegistro);
        verify(auditoriaRepositoryPort).buscarPorClasificacionRiesgo(AuditoriaService.RIESGO_ALTO);
    }
}
