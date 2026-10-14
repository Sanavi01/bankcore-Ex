package com.bankcore.backend.service;

import com.bankcore.backend.domain.event.TransaccionRealizadaEvent;
import com.bankcore.backend.domain.model.RegistroAuditoria;
import com.bankcore.backend.domain.port.AuditoriaRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de Aplicación para Auditoría Forense y Cumplimiento Normativo (SARLAFT/AML).
 * Aplica reglas de riesgo sobre las transacciones bancarias recibidas asíncronamente vía RabbitMQ
 * y delega la persistencia al puerto desacoplado AuditoriaRepositoryPort.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditoriaService {

    public static final BigDecimal UMBRAL_AML_MONTO_ALTO = new BigDecimal("5000000"); // 5 Millones COP / USD
    public static final String RIESGO_ALTO = "ALERTA_AML_REVISION";
    public static final String RIESGO_BAJO = "BAJO_RIESGO_CONFORME";
    public static final String CANAL_RABBITMQ = "RABBITMQ_ASYNC";

    private final AuditoriaRepositoryPort auditoriaRepositoryPort;

    /**
     * Registra el evento de auditoría recibido asíncronamente desde el broker RabbitMQ.
     */
    @Transactional
    public RegistroAuditoria registrarAuditoria(TransaccionRealizadaEvent evento) {
        String clasificacion = evaluarRiesgo(evento.monto());

        RegistroAuditoria registro = new RegistroAuditoria(
                null,
                evento.idTransaccion(),
                evento.cuentaOrigen(),
                evento.cuentaDestino(),
                evento.monto(),
                clasificacion,
                evento.fechaHora(),
                LocalDateTime.now(),
                CANAL_RABBITMQ
        );

        RegistroAuditoria guardado = auditoriaRepositoryPort.guardar(registro);

        log.info("Auditoría forense persistida exitosamente: ID [{}], Transacción [{}], Riesgo: [{}]",
                guardado.getId(), guardado.getIdTransaccion(), guardado.getClasificacionRiesgo());

        return guardado;
    }

    /**
     * Regla de Negocio AML: Clasifica el nivel de riesgo según el monto transaccionado.
     */
    public String evaluarRiesgo(BigDecimal monto) {
        if (monto != null && monto.compareTo(UMBRAL_AML_MONTO_ALTO) >= 0) {
            return RIESGO_ALTO;
        }
        return RIESGO_BAJO;
    }

    /**
     * Consulta el registro de auditoría por el ID único de la transacción.
     */
    @Transactional(readOnly = true)
    public RegistroAuditoria consultarPorIdTransaccion(String idTransaccion) {
        return auditoriaRepositoryPort.buscarPorIdTransaccion(idTransaccion)
                .orElseThrow(() -> new IllegalArgumentException("No existe registro de auditoría para la transacción: " + idTransaccion));
    }

    @Transactional(readOnly = true)
    public Optional<RegistroAuditoria> buscarPorIdTransaccion(String idTransaccion) {
        return auditoriaRepositoryPort.buscarPorIdTransaccion(idTransaccion);
    }

    /**
     * Consulta los registros de auditoría filtrados por clasificación de riesgo.
     */
    @Transactional(readOnly = true)
    public List<RegistroAuditoria> consultarPorRiesgo(String clasificacionRiesgo) {
        return auditoriaRepositoryPort.buscarPorClasificacionRiesgo(clasificacionRiesgo);
    }

    @Transactional(readOnly = true)
    public List<RegistroAuditoria> buscarPorClasificacionRiesgo(String clasificacionRiesgo) {
        return consultarPorRiesgo(clasificacionRiesgo);
    }
}
