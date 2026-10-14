package com.bankcore.backend.infrastructure.messaging.consumer;

import com.bankcore.backend.config.RabbitMQConfig;
import com.bankcore.backend.domain.event.TransaccionRealizadaEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor de Dead Letter Queue (DLQ Consumer).
 * Monitorea y procesa mensajes que no pudieron ser procesados por la cola principal.
 * 
 * En producción bancaria, este consumidor:
 * 1. Alerta a los equipos de SRE / SecOps mediante PagerDuty o SIEM.
 * 2. Guarda el mensaje en una tabla de auditoría de incidentes para posterior remediación (Replay).
 */
@Slf4j
@Component
public class AuditoriaDlqConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_AUDIT_DLQ)
    public void procesarMensajeMuerto(TransaccionRealizadaEvent evento) {
        log.error("🚨 ALERTA SRE / DLQ: Mensaje envenenado capturado en [{}]. ID: [{}], Monto: [{}], Concepto: [{}]",
                RabbitMQConfig.QUEUE_AUDIT_DLQ,
                evento.idTransaccion(),
                evento.monto(),
                evento.concepto());
    }
}
